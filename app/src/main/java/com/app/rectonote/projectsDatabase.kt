package com.app.rectonote

import android.content.Context
import androidx.room.*

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val project_id: Int,
    @ColumnInfo(name = "project_name")
    val name: String,
    @ColumnInfo(name = "project_tempo")
    val tempo: Int,
    @ColumnInfo(name = "project_key")
    val key: Int, // 0 = key C to 11 = key B
    var num_of_tracks : Int
)

@Entity(tableName = "draft_tracks",
        foreignKeys = arrayOf(ForeignKey(entity = ProjectEntity::class,
            parentColumns = arrayOf("project_id"),
            childColumns = arrayOf("project_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
            )))
data class DraftTracksEntity(
    @PrimaryKey(autoGenerate = true)
    val tracks_id: Int,
    @ColumnInfo(name = "tracks_name")
    val name: String,
    @ColumnInfo(name = "tracks_tempo")
    val tempo: Int,
    @ColumnInfo(name = "tracks_key")
    val key: Int, // 0 = key C to 11 = key B
    @ColumnInfo(name = "project_id")
    val project_id: Int,
    @ColumnInfo(name = "tracks_number")
    var track_no: Int
)

@Dao
interface ProjectDao{

}

@Dao
interface DraftTracksDao{

}
@Database(entities = arrayOf(ProjectEntity::class, DraftTracksEntity::class), version = 1)
abstract class projectsDatabase : RoomDatabase(){
    abstract fun projectDAO() : ProjectDao
    abstract fun drafttracksDAO() : DraftTracksDao

    companion object{
        private var INSTANCE: projectsDatabase ?= null
        fun getInstance(context: Context): projectsDatabase{
            if(INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, projectsDatabase::class.java, "projectsDatabase.db").build()
            return INSTANCE!!
        }
    }
}



