package com.app.rectonote.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.util.*


@Entity(tableName = "draft_tracks")
data class DraftTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val tracksId: Int? = null,
    @ColumnInfo(name = "tracks_name")
    val name: String,
    @ColumnInfo(name = "tracks_tempo")
    val tempo: Int,
    @ColumnInfo(name = "tracks_key")
    val key: String, // 0 = key C to 11 = key B
    @ColumnInfo(name = "project_id")
    val projectId: Int,
    @ColumnInfo(name = "tracks_number")
    var trackNo: Int,
    @ColumnInfo(name = "date_modified")
    var dateModified: Date,
    var color: String
)