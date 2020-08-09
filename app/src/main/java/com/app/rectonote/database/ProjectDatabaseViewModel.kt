package com.app.rectonote.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProjectDatabaseViewModel(
    val database: ProjectDao,
    application: Application
) : AndroidViewModel(application){

}