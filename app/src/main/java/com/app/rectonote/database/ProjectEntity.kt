package com.app.rectonote.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

import java.util.*

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val projectId: Int? = null,
    @ColumnInfo(name = "project_name")
    val name: String,
    @ColumnInfo(name = "project_tempo")
    val tempo: Int,
    @ColumnInfo(name = "project_key")
    val key: String,
    @ColumnInfo(name = "date_modified")
    var dateModified: Date,
    var color: String

): Serializable