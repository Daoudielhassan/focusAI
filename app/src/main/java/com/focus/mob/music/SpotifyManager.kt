package com.focus.mob.music

import javax.inject.Inject
import javax.inject.Singleton

/** Stub for future Spotify SDK integration. */
@Singleton
class SpotifyManager @Inject constructor() {

    fun connect() { /* TODO: Spotify SDK connect */ }

    fun play(trackUri: String) { /* TODO: Spotify SDK play */ }

    fun pause() { /* TODO: Spotify SDK pause */ }

    fun disconnect() { /* TODO: Spotify SDK disconnect */ }
}
