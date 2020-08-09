package com.app.rectonote.database

import androidx.room.Embedded
import androidx.room.Relation

data class DraftTracksInProject(
    @Embedded val project: ProjectEntity,
    var numOfTracks : Int,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "project_id"
    )
    val tracks : List<DraftTrackEntity>
)

