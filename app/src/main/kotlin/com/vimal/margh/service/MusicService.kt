package com.vimal.margh.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vimal.margh.R
import com.vimal.margh.models.ModelRadio
import com.vimal.margh.util.Config
import com.vimal.margh.util.Constant.ACTION_DATA
import com.vimal.margh.util.Constant.ACTION_NEXT
import com.vimal.margh.util.Constant.ACTION_PAUSE
import com.vimal.margh.util.Constant.ACTION_PLAY
import com.vimal.margh.util.Constant.ACTION_PLAYER_STATE
import com.vimal.margh.util.Constant.ACTION_PREVIOUS
import com.vimal.margh.util.Constant.ACTION_STATE
import com.vimal.margh.util.Constant.ACTION_STOP
import com.vimal.margh.util.Constant.CHANNEL_ID
import com.vimal.margh.util.Utils.getParcelableExtraCompat

class MusicService : Service() {

    private val binder = LocalBinder()
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null

    inner class LocalBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        player = ExoPlayer.Builder(this).build()
        setupMediaSession()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    playMusic()
                }

                override fun onPause() {
                    super.onPause()
                    pauseMusic()
                }

                override fun onStop() {
                    super.onStop()
                    stopMusic()
                }
            })
            isActive = true
            setPlaybackState(createPlaybackState(PlaybackStateCompat.STATE_PAUSED))
        }
    }

    private fun createPlaybackState(state: Int): PlaybackStateCompat {
        val playbackActions = when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP
            }

            else -> {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_STOP
            }
        }
        return PlaybackStateCompat.Builder()
            .setActions(playbackActions)
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0F)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val data = it.getParcelableExtraCompat(ACTION_DATA) as ModelRadio?
            data?.let { radioData ->
                playUrl(radioData.radio_url)
                getNotification(
                    radioData.radio_name,
                    radioData.category_name,
                    radioData.radio_image
                ) { notification ->
                    startForeground(1, notification)
                }

            }
            when (it.action) {
                ACTION_PLAY -> playMusic()
                ACTION_PAUSE -> pauseMusic()
                ACTION_STOP -> stopMusic()
                else -> {}
            }
        }
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_NOT_STICKY
    }


    fun playMusic() {
        if (player?.isPlaying == false) {
            player?.play()
            mediaSession?.setPlaybackState(createPlaybackState(PlaybackStateCompat.STATE_PLAYING))
            sendPlayerStateBroadcast(PlaybackStateCompat.STATE_PLAYING)
        }
    }



    fun pauseMusic() {
        if (player?.isPlaying == true) {
            player?.pause()
            mediaSession?.setPlaybackState(createPlaybackState(PlaybackStateCompat.STATE_PAUSED))
            sendPlayerStateBroadcast(PlaybackStateCompat.STATE_PAUSED)
        }
    }


    fun stopMusic() {
        if (player?.isPlaying == true) {
            player?.stop()
            player?.prepare()
            mediaSession?.setPlaybackState(createPlaybackState(PlaybackStateCompat.STATE_STOPPED))
            sendPlayerStateBroadcast(PlaybackStateCompat.STATE_STOPPED)
        }
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    private fun sendPlayerStateBroadcast(state: Int) {
        val intent = Intent(ACTION_PLAYER_STATE)
        intent.putExtra(ACTION_STATE, state)
        sendBroadcast(intent)
    }

    private fun playUrl(url: String) {
        player?.apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            play()
            mediaSession?.setPlaybackState(createPlaybackState(PlaybackStateCompat.STATE_PLAYING))
            Log.d("MusicService", "Playing URL: $url")
        }
    }

    private fun getNotification(
        title: String,
        category: String,
        imageUrl: String,
        callback: (Notification) -> Unit
    ) {
        loadImage(Config.REST_API_URL + "/upload/" + imageUrl.replace(" ", "%20")) { bitmap ->
            val playIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PLAY }
            val playPendingIntent =
                PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

            val pauseIntent = Intent(this, MusicService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent =
                PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

            val stopIntent = Intent(this, MusicService::class.java).apply { action = ACTION_STOP }
            val stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)


            val nextIntent = Intent(this, MusicService::class.java).apply { action = ACTION_NEXT }
            val nextPendingIntent =
                PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)


            val previousIntent =
                Intent(this, MusicService::class.java).apply { action = ACTION_PREVIOUS }
            val previousPendingIntent =
                PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE)


            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(category)
                .setSmallIcon(R.drawable.ic_music)
                .addAction(R.drawable.ic_play, "Play", playPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_skip_next_black, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_skip_previous_black, "Previous", previousPendingIntent)
                .setStyle(
                    MediaStyle()
                        .setMediaSession(mediaSession?.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2)
                )

            bitmap?.let {
                builder.setLargeIcon(it)
            }

            callback(builder.build())
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_NONE
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            Log.d("MusicService", "Notification channel created")
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun loadImage(url: String, callback: (Bitmap?) -> Unit) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(null)
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        mediaSession?.release()
        mediaSession = null
        Log.d("MusicService", "Service destroyed")
    }
}
