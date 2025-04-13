// File: com/example/wellipet/data/model/AppDatabase.kt
package com.example.wellipet.data.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepCount::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepsDao(): StepsDao
}
