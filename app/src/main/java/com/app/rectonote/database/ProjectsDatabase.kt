package com.app.rectonote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ProjectEntity::class, DraftTrackEntity::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class ProjectsDatabase : RoomDatabase() {
    abstract fun projectDAO(): ProjectDao
    abstract fun drafttracksDAO(): DraftTrackDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectsDatabase? = null
        fun getInstance(context: Context): ProjectsDatabase {
            synchronized(this) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(
                        context,
                        ProjectsDatabase::class.java,
                        "projectsDatabase.db"
                    ).build()
                return INSTANCE!!
            }
        }
    }
}



