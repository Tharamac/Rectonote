package com.app.rectonote.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProjectDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newProject(vararg project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY date_modified DESC")
    suspend fun loadAllProjects(): Array<ProjectEntity>
}