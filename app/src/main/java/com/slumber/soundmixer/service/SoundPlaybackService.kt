package com.slumber.soundmixer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.slumber.soundmixer.MainActivity
import com.slumber.soundmixer.R
import com.slumber.soundmixer.data.repository.PlaybackRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SoundPlaybackService : Service() {

    @Inject lateinit var repo: PlaybackRepository

    private val players = mutableMapOf<String, MediaPlayer>()
    private var countDownTimer: CountDownTimer? = null
    private var isFading = false
    private var isDucking = false
    private var wasPlayingBeforeFocusLoss = false
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                isDucking = false
                if (!isFading) restoreVolumes()
                if (wasPlayingBeforeFocusLoss) {
                    wasPlayingBeforeFocusLoss = false
                    ensurePlayersCreated()
                    players.values.forEach { runCatching { it.start() } }
                    repo.setPlaying(true)
                    updateNotification()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                wasPlayingBeforeFocusLoss = false
                if (repo.isPlaying.value) {
                    players.values.forEach { runCatching { it.pause() } }
                    repo.setPlaying(false)
                    updateNotification()
                }
                abandonAudioFocus()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                wasPlayingBeforeFocusLoss = repo.isPlaying.value
                if (repo.isPlaying.value) {
                    players.values.forEach { runCatching { it.pause() } }
                    repo.setPlaying(false)
                    updateNotification()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                isDucking = true
                repo.activeSounds.value.forEach { (id, s) ->
                    players[id]?.setVolume(s.volume * 0.2f, s.volume * 0.2f)
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "slumber_playback"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.slumber.ACTION_PLAY"
        const val ACTION_PAUSE = "com.slumber.ACTION_PAUSE"
        const val ACTION_SET_TIMER = "com.slumber.ACTION_SET_TIMER"
        const val ACTION_CANCEL_TIMER = "com.slumber.ACTION_CANCEL_TIMER"
        const val ACTION_STOP = "com.slumber.ACTION_STOP"
        const val EXTRA_TIMER_MINUTES = "timer_minutes"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
        observeActiveSounds()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundCompat()
        when (intent?.action) {
            ACTION_PLAY -> {
                requestAudioFocus()
                ensurePlayersCreated()
                repo.setPlaying(true)
                players.values.forEach { runCatching { it.start() } }
            }
            ACTION_PAUSE -> {
                repo.setPlaying(false)
                players.values.forEach { runCatching { it.pause() } }
                if (repo.timerMillisRemaining.value == 0L) {
                    abandonAudioFocus()
                    stopSelf()
                }
            }
            ACTION_SET_TIMER -> {
                val minutes = intent.getIntExtra(EXTRA_TIMER_MINUTES, 30)
                repo.setTimer(minutes)
                startCountdown(minutes * 60_000L)
            }
            ACTION_CANCEL_TIMER -> {
                countDownTimer?.cancel()
                countDownTimer = null
                isFading = false
                restoreVolumes()
                repo.clearTimer()
                if (!repo.isPlaying.value) {
                    abandonAudioFocus()
                    stopSelf()
                }
            }
            ACTION_STOP -> {
                cleanup()
                stopSelf()
                return START_NOT_STICKY
            }
        }
        updateNotification()
        return START_NOT_STICKY
    }

    private fun startForegroundCompat() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun ensurePlayersCreated() {
        repo.activeSounds.value.forEach { (id, activeSound) ->
            if (id !in players) {
                MediaPlayer.create(this, activeSound.sound.resId)?.apply {
                    isLooping = true
                    setVolume(activeSound.volume, activeSound.volume)
                    players[id] = this
                }
            }
        }
    }

    private fun startCountdown(totalMillis: Long) {
        countDownTimer?.cancel()
        isFading = false
        countDownTimer = object : CountDownTimer(totalMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                repo.tickTimer(millisUntilFinished)
                if (repo.fadeEnabled.value && millisUntilFinished <= 60_000L) {
                    isFading = true
                    val fadeProgress = millisUntilFinished / 60_000f
                    repo.activeSounds.value.forEach { (id, s) ->
                        players[id]?.setVolume(s.volume * fadeProgress, s.volume * fadeProgress)
                    }
                }
                updateNotification()
            }

            override fun onFinish() {
                isFading = false
                repo.setPlaying(false)
                repo.clearTimer()
                players.values.forEach { runCatching { it.pause() } }
                abandonAudioFocus()
                updateNotification()
                stopSelf()
            }
        }.start()
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusListener)
                .build()
            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusListener)
        }
        isDucking = false
        wasPlayingBeforeFocusLoss = false
    }

    private fun restoreVolumes() {
        repo.activeSounds.value.forEach { (id, s) ->
            players[id]?.setVolume(s.volume, s.volume)
        }
    }

    private fun observeActiveSounds() {
        serviceScope.launch {
            repo.activeSounds.collect { sounds ->
                (players.keys - sounds.keys).toList().forEach { id ->
                    players[id]?.apply { runCatching { stop() }; release() }
                    players.remove(id)
                }
                if (repo.isPlaying.value) {
                    (sounds.keys - players.keys).forEach { id ->
                        val s = sounds[id] ?: return@forEach
                        MediaPlayer.create(this@SoundPlaybackService, s.sound.resId)?.apply {
                            isLooping = true
                            setVolume(s.volume, s.volume)
                            runCatching { start() }
                            players[id] = this
                        }
                    }
                }
                if (!isFading) {
                    sounds.forEach { (id, s) -> players[id]?.setVolume(s.volume, s.volume) }
                }
                if (sounds.isEmpty() && repo.timerMillisRemaining.value == 0L) {
                    repo.setPlaying(false)
                    stopSelf()
                }
                updateNotification()
            }
        }
    }

    private fun buildNotification(): Notification {
        val sounds = repo.activeSounds.value
        val remaining = repo.timerMillisRemaining.value
        val playing = repo.isPlaying.value

        val soundsText = if (sounds.isEmpty()) "No sounds selected"
        else sounds.values.joinToString(" · ") { "${it.sound.emoji} ${it.sound.name}" }

        val timerText = if (remaining > 0L) {
            val m = (remaining / 60_000L).toInt()
            val s = ((remaining % 60_000L) / 1_000L).toInt()
            " · %d:%02d".format(m, s)
        } else ""

        val toggleAction = if (playing) ACTION_PAUSE else ACTION_PLAY
        val toggleLabel = if (playing) "Pause" else "Play"

        val togglePi = PendingIntent.getService(
            this, 0,
            Intent(this, SoundPlaybackService::class.java).apply { action = toggleAction },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPi = PendingIntent.getService(
            this, 1,
            Intent(this, SoundPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val contentPi = PendingIntent.getActivity(
            this, 2,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Sleep Sounds")
            .setContentText(soundsText + timerText)
            .setContentIntent(contentPi)
            .addAction(if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, toggleLabel, togglePi)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPi)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification() {
        runCatching {
            notificationManager.notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sleep Sounds Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for sleep sound playback"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun cleanup() {
        countDownTimer?.cancel()
        countDownTimer = null
        players.values.forEach { player ->
            runCatching { player.stop() }
            player.release()
        }
        players.clear()
        repo.setPlaying(false)
        repo.clearTimer()
        abandonAudioFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        cleanup()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
