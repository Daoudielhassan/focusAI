package com.focus.mob.data.repository

import com.focus.mob.data.SessionDao
import com.focus.mob.data.SessionRecord

/**
 * Repository module for handling data operations.
 * Separates the data source from the rest of the app.
 */
class SessionRepository(private val sessionDao: SessionDao) {

    suspend fun insertSession(session: SessionRecord) {
        sessionDao.insert(session)
    }

    suspend fun getAllSessions(): List<SessionRecord> {
        return sessionDao.getAllSessions()
    }

    suspend fun getTotalFocusTime(): Int {
        return sessionDao.getTotalFocusTime()
    }

    suspend fun getLastSession(): SessionRecord? {
        return sessionDao.getLastSession()
    }

    suspend fun getTodayFocusTime(startOfDay: Long): Int {
        return sessionDao.getTodayFocusTime(startOfDay)
    }

    suspend fun deleteAllSessions() {
        sessionDao.deleteAllSessions()
    }
}
