package com.app.rectonote.database

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ProjectDatabaseViewModel(
    private val database: ProjectDao
) : ViewModel(){


    fun loadAllProjects(): LiveData<List<ProjectEntity>> {
        val result = MutableLiveData<List<ProjectEntity>>()
        viewModelScope.launch {
            val repo = database.loadAllProjects()
            result.postValue(repo)
        }
        return result
    }

    fun newProjects(project: ProjectEntity) = viewModelScope.launch {
        database.newProject(project)
    }

    fun deleteProject(project: ProjectEntity) = viewModelScope.launch {
        database.deleteProject(project)
    }
}