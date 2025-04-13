// File: com/example/wellipet/data/model/StepsDao.kt
package com.example.wellipet.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface StepsDao {
    @Query("SELECT * FROM steps")
    suspend fun getAll(): List<StepCount>

    @Query("SELECT * FROM steps WHERE created_at >= date(:startDateTime) " +
            "AND created_at < date(:startDateTime, '+1 day')")
    suspend fun loadAllStepsFromToday(startDateTime: String): Array<StepCount>

    @Insert
    suspend fun insertAll(vararg steps: StepCount)

    @Delete
    suspend fun delete(steps: StepCount)
}
