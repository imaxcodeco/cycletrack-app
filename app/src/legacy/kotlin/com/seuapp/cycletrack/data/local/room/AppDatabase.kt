package com.seuapp.cycletrack.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.seuapp.cycletrack.data.local.room.dao.AnalyticsEventDao
import com.seuapp.cycletrack.data.local.room.dao.UserSessionDao
import com.seuapp.cycletrack.data.local.room.entities.AnalyticsEvent
import com.seuapp.cycletrack.data.local.room.entities.UserSession

/**
 * Room database for analytics and session tracking.
 * Clean-room implementation separate from Realm for analytics isolation.
 */
@Database(
    entities = [
        AnalyticsEvent::class,
        UserSession::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun analyticsEventDao(): AnalyticsEventDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "cycletrack_analytics.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2) // For future migrations
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration example for future schema changes
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: database.execSQL("ALTER TABLE analytics_events ADD COLUMN new_field TEXT")
            }
        }
    }
}

/**
 * Type converters for Room.
 */
class Converters {
    // Add custom type converters here if needed
    // e.g., for Date, Enum, List, etc.
}