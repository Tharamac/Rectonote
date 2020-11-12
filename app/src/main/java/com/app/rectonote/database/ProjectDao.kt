package com.app.rectonote.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newProject(vararg project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY date_modified DESC")
    fun loadAllProjects(): LiveData<List<ProjectEntity>>

    @Query("SELECT project_name FROM projects")
    fun getNames(): LiveData<List<String>>

    @Query("SELECT projectId FROM projects WHERE project_name = :projectName")
    fun getIdFromProject(projectName: String): LiveData<List<Int>>

    @Query("SELECT * FROM projects WHERE project_name = :projectName")
    fun getProjectFromName(projectName: String): LiveData<List<ProjectEntity>>

    @Delete
    fun deleteProject(project: ProjectEntity)
}