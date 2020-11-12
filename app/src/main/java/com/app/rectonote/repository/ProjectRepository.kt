package com.app.rectonote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.app.rectonote.database.ProjectDao
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class ProjectRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private lateinit var projectDao: ProjectDao
    private lateinit var allProject: LiveData<MutableList<ProjectEntity>>

    init {
        val db = ProjectsDatabase.getInstance(application)
        projectDao = db.projectDAO()
    }

    fun loadAllProject() = projectDao.loadAllProjects()
    fun loadAllProjectNames() = projectDao.getNames()
}