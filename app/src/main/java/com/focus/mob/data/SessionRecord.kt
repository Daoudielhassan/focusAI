package com.focus.mob.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationMinutes: Int = 0,
    val timestamp: Long = 0L,
    val goal: String = "",
    val ambiance: String = "",
    val moodFeedback: String = ""
)
