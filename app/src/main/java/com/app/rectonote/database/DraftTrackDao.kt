package com.app.rectonote.database

import androidx.room.*

@Dao
interface DraftTrackDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newDraftTrack(track: DraftTrackEntity)

    @Query("SELECT * FROM draft_tracks WHERE project_id = :projectId  ORDER BY date_modified DESC")
    suspend fun loadTracksFromProject(projectId: Int): List<DraftTrackEntity>

    @Delete
    suspend fun deleteTrack(track: DraftTrackEntity)
}