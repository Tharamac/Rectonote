package com.app.rectonote.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DraftTracksViewModel(
    private val tracksDao:DraftTrackDao
) : ViewModel(){



    //Tracks DAO
    fun addNewTrack(track: DraftTrackEntity) = viewModelScope.launch {
        tracksDao.newDraftTrack(track)
    }

    fun loadTracksFromProject(projectId: Int): LiveData<List<DraftTrackEntity>> {
        val result = MutableLiveData<List<DraftTrackEntity>>()
        viewModelScope.launch {
            val repo = tracksDao.loadTracksFromProject(projectId)
            result.postValue(repo)
        }
        return result
    }
}