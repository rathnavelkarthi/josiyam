package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BirthProfileEntity::class, ChartCalculationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JyotishaDatabase : RoomDatabase() {
    abstract fun jyotishaDao(): JyotishaDao

    companion object {
        @Volatile
        private var INSTANCE: JyotishaDatabase? = null

        fun getDatabase(context: Context): JyotishaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JyotishaDatabase::class.java,
                    "jyotisha_production.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
