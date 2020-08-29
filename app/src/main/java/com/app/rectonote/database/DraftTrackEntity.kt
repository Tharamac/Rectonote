package com.app.rectonote.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

import java.util.*


@Entity(
    tableName = "draft_tracks",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = arrayOf("projectId"),
        childColumns = arrayOf("project_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DraftTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val tracksId: Int? = null,
    @ColumnInfo(name = "tracks_name")
    val name: String,
    @ColumnInfo(name = "tracks_tempo")
    val tempo: Int,
    @ColumnInfo(name = "tracks_key")
    val key: String, // 0 = key C to 11 = key B
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "project_id")
    val projectId: Int,
    @ColumnInfo(name = "date_modified")
    var dateModified: Date,
    var color: String
)