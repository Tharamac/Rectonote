package com.app.rectonote.database

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction

interface DraftTracksInProjectDao {
    @Transaction
    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    suspend fun loadProjectWithTracks(projectId: Int): List<DraftTracksInProject>

    @Transaction
    @Delete
    suspend fun deleteProject(wholeProject: DraftTracksInProject)
}