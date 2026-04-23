package com.focus.mob.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    void insert(SessionRecord session);

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    List<SessionRecord> getAllSessions();

    @Query("SELECT SUM(durationMinutes) FROM sessions")
    int getTotalFocusTime();

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC LIMIT 1")
    SessionRecord getLastSession();

    @Query("DELETE FROM sessions")
    void deleteAllSessions();
}
