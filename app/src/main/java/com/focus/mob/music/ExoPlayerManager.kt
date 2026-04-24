package com.focus.mob.music

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var player: ExoPlayer? = null

    fun prepareAndPlay(streamUrl: String, startPaused: Boolean = false) {
        release()
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> Timber.d("ExoPlayer: buffering")
                        Player.STATE_READY -> {
                            Timber.d("ExoPlayer: ready")
                            if (!startPaused) play()
                        }
                        Player.STATE_ENDED -> Timber.d("ExoPlayer: ended")
                        Player.STATE_IDLE  -> Timber.d("ExoPlayer: idle")
                    }
                }
            })
        }
    }

    fun play()    { player?.play() }
    fun pause()   { player?.pause() }

    fun release() {
        player?.release()
        player = null
    }
}
