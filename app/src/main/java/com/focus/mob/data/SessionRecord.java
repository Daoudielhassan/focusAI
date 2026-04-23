package com.focus.mob.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class SessionRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int durationMinutes;
    public long timestamp;
    public String goal;
    public String ambiance;
    public String moodFeedback; // e.g. "great", "good", "okay", "bad"
}
