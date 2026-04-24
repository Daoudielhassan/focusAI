package com.focus.mob.music

import com.focus.mob.data.repository.RadioRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class SoundInfo(val title: String, val subtitle: String)

private val FALLBACK = SoundInfo(title = "Playlist Relax", subtitle = "Hors-ligne")

@Singleton
class MusicProvider @Inject constructor(
    private val radioRepository: RadioRepository,
    private val exoPlayerManager: ExoPlayerManager
) {
    /**
     * Fetches a radio station matching [soundCategory], prepares ExoPlayer, and starts playback.
     * Returns [SoundInfo] with the station's display labels (or fallback if offline).
     */
    suspend fun loadAndPlay(soundCategory: String, startPaused: Boolean = false): SoundInfo {
        val tag = PlaylistMapper.toRadioTag(soundCategory)
        val station = radioRepository.fetchStation(tag)
        return if (station != null) {
            Timber.i("MusicProvider: playing '${station.name}' @ ${station.streamUrl}")
            exoPlayerManager.prepareAndPlay(station.streamUrl, startPaused)
            SoundInfo(
                title    = station.name.trim(),
                subtitle = "${station.codec} · ${station.bitrate}kbps · En direct"
            )
        } else {
            Timber.w("MusicProvider: no station found, offline fallback")
            FALLBACK
        }
    }

    fun play()    { exoPlayerManager.play() }
    fun pause()   { exoPlayerManager.pause() }
    fun release() { exoPlayerManager.release() }
}
