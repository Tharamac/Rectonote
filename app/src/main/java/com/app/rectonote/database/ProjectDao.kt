package com.app.rectonote.database

import androidx.room.*

@Dao
interface ProjectDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newProject(vararg project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY date_modified DESC")
    suspend fun loadAllProjects(): List<ProjectEntity>

    @Query("SELECT project_name FROM projects")
    suspend fun getNames():List<String>

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}