package com.focus.mob.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: SessionRecord)

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    suspend fun getAllSessions(): List<SessionRecord>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions")
    suspend fun getTotalFocusTime(): Int

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastSession(): SessionRecord?

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions WHERE timestamp >= :startOfDay")
    suspend fun getTodayFocusTime(startOfDay: Long): Int

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}
