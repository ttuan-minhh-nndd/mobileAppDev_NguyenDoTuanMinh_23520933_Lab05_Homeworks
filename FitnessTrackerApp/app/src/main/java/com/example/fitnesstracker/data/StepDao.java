package com.example.fitnesstracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface StepDao {
    @Insert
    void insertStep(StepEntity step);

    @Query("SELECT SUM(stepCount) FROM steps")
    int getTotalSteps();

    @Query("SELECT * FROM steps ORDER BY timestamp DESC LIMIT 100")
    List<StepEntity> getRecentSteps();
}
