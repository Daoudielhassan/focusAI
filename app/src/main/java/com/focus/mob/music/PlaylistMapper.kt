package com.focus.mob.music

/** Maps a sound-category string to a Radio Browser API tag. */
object PlaylistMapper {

    fun toRadioTag(soundCategory: String): String = when (soundCategory.lowercase()) {
        "ambient"   -> "ambient"
        "lofi"      -> "lofi"
        "jazz"      -> "jazz"
        "classical" -> "classical"
        "nature"    -> "nature"
        "focus"     -> "study"
        else        -> "ambient"
    }
}
