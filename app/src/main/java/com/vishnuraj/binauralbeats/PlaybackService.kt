package com.vishnuraj.binauralbeats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat

/**
 * Lets MainActivity and the notification talk to each other.
 * MainActivity fills these in; the service calls them.
 */
object PlaybackBridge {
    var onToggle: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null
    var isPlaying: (() -> Boolean)? = null
}

/**
 * Shows a media notification (Pause/Play + Close) while a session plays,
 * and keeps the app process alive in the background.
 * The audio itself is still played by AudioEngine inside MainActivity.
 */
class PlaybackService : Service() {

    companion object {
        private const val CHANNEL_ID = "playback"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_UPDATE = "com.vishnuraj.binauralbeats.UPDATE"
        private const val ACTION_TOGGLE = "com.vishnuraj.binauralbeats.TOGGLE"
        private const val ACTION_CLOSE = "com.vishnuraj.binauralbeats.CLOSE"
        private const val ACTION_DISMISS = "com.vishnuraj.binauralbeats.DISMISS"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_SUBTITLE = "subtitle"
        private const val EXTRA_PLAYING = "playing"

        @Volatile
        private var running = false

        /** Call whenever the session or play/pause state changes. */
        fun update(context: Context, title: String, subtitle: String, isPlaying: Boolean) {
            // A paused update only makes sense if the notification already exists.
            if (!isPlaying && !running) return
            val intent = Intent(context, PlaybackService::class.java)
                .setAction(ACTION_UPDATE)
                .putExtra(EXTRA_TITLE, title)
                .putExtra(EXTRA_SUBTITLE, subtitle)
                .putExtra(EXTRA_PLAYING, isPlaying)
            // runCatching: Android can refuse a service start from the background.
            runCatching {
                if (isPlaying) ContextCompat.startForegroundService(context, intent)
                else context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, PlaybackService::class.java))
        }
    }

    private lateinit var session: MediaSessionCompat
    private var title = "Binaural Beats"
    private var subtitle = ""
    private var art: Bitmap? = null
    private var artFor: String? = null

    override fun onCreate() {
        super.onCreate()
        running = true
        createChannel()
        session = MediaSessionCompat(this, "BinauralBeats").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    if (PlaybackBridge.isPlaying?.invoke() != true) handleToggle()
                }

                override fun onPause() {
                    if (PlaybackBridge.isPlaying?.invoke() == true) handleToggle()
                }

                override fun onStop() = handleClose()
            })
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> {
                title = intent.getStringExtra(EXTRA_TITLE) ?: title
                subtitle = intent.getStringExtra(EXTRA_SUBTITLE) ?: subtitle
                render(intent.getBooleanExtra(EXTRA_PLAYING, true))
            }
            ACTION_TOGGLE -> handleToggle()
            ACTION_CLOSE -> handleClose()
            else -> shutdown() // ACTION_DISMISS (notification swiped away) or no intent
        }
        return START_NOT_STICKY
    }

    private fun handleToggle() {
        val toggle = PlaybackBridge.onToggle
        if (toggle == null) {
            // The app was closed, so there is nothing left to control.
            shutdown()
            return
        }
        toggle()
        render(PlaybackBridge.isPlaying?.invoke() ?: false)
    }

    private fun handleClose() {
        PlaybackBridge.onClose?.invoke()
        shutdown()
    }

    private fun shutdown() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun render(playing: Boolean) {
        updateSession(playing)
        val notification = buildNotification(playing)
        if (playing) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            // Keep the notification, but make it swipe-away so the user can dismiss it.
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun updateSession(playing: Boolean) {
        session.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Binaural Beats")
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artwork())
                .build()
        )
        session.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_STOP
                )
                .setState(
                    if (playing) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    if (playing) 1f else 0f
                )
                .build()
        )
    }

    private fun buildNotification(playing: Boolean): android.app.Notification {
        val openApp = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(artwork())
            .setColor(paletteFor(title).deep.toArgb())
            .setColorized(true)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setContentIntent(openApp)
            .setDeleteIntent(servicePending(ACTION_DISMISS, 3))
            .setOngoing(playing)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (playing) "Pause" else "Play",
                servicePending(ACTION_TOGGLE, 1)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Close",
                servicePending(ACTION_CLOSE, 2)
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(session.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .build()
    }

    /** Square "album art": the session's gradient with the app logo in the middle. */
    private fun artwork(): Bitmap {
        art?.let { if (artFor == title) return it }
        val p = paletteFor(title)
        val size = 512
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, size.toFloat(), size.toFloat(),
                p.gradientStart.toArgb(), p.deep.toArgb(),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        ContextCompat.getDrawable(this, R.drawable.ic_brand_logo)?.let { logo ->
            val logoSize = (size * 0.5f).toInt()
            val offset = (size - logoSize) / 2
            logo.setBounds(offset, offset, offset + logoSize, offset + logoSize)
            logo.draw(canvas)
        }
        art = bitmap
        artFor = title
        return bitmap
    }

    private fun servicePending(action: String, requestCode: Int): PendingIntent =
        PendingIntent.getService(
            this,
            requestCode,
            Intent(this, PlaybackService::class.java).setAction(action),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for the session that is playing"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        running = false
        session.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
