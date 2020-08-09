package com.app.rectonote.database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProjectDatabaseViewModelFactory(
    private val dataSource: ProjectDao,
    private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDatabaseViewModel::class.java)) {
            return ProjectDatabaseViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}