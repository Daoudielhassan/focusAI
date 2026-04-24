package com.focus.mob.data
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

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

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}
