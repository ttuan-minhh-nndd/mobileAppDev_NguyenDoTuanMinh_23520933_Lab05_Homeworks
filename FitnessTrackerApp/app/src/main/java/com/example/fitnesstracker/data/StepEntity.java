package com.example.fitnesstracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "steps")
public class StepEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public long timestamp;
    public int stepCount;

    public StepEntity(long timestamp, int stepCount) {
        this.timestamp = timestamp;
        this.stepCount = stepCount;
    }
}
