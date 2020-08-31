package com.app.rectonote.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProjectDatabaseViewModel(
    private val projectDao: ProjectDao
) : ViewModel() {

    //Project DAO
    fun loadAllProjects(): LiveData<List<ProjectEntity>> {
        val result = MutableLiveData<List<ProjectEntity>>()
        viewModelScope.launch {
            val repo = projectDao.loadAllProjects()
            result.postValue(repo)
        }
        return result
    }

    fun newProjects(vararg project: ProjectEntity) = viewModelScope.launch {
        project.forEach {
            projectDao.newProject(it)
        }

    }

    fun deleteProject(project: ProjectEntity) = viewModelScope.launch {
        projectDao.deleteProject(project)
    }

    fun getNames(): MutableLiveData<List<String>> {
        val result = MutableLiveData<List<String>>()
        viewModelScope.launch {
            val repo = projectDao.getNames()
            result.postValue(repo)
        }
        return result
    }

}