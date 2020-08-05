package com.app.rectonote

import android.content.Context
import androidx.room.*
import java.util.*

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val projectId: Int,
    @ColumnInfo(name = "project_name")
    val name: String,
    @ColumnInfo(name = "project_tempo")
    val tempo: Int,
    @ColumnInfo(name = "project_key")
    val key: Int, // 0 = key C to 11 = key B
    @ColumnInfo(name = "date_modified")
    var dateModified: Date

)

@Entity(tableName = "draft_tracks")
data class DraftTracksEntity(
    @PrimaryKey(autoGenerate = true)
    val tracksId: Int,
    @ColumnInfo(name = "tracks_name")
    val name: String,
    @ColumnInfo(name = "tracks_tempo")
    val tempo: Int,
    @ColumnInfo(name = "tracks_key")
    val key: Int, // 0 = key C to 11 = key B
    @ColumnInfo(name = "project_id")
    val projectId: Int,
    @ColumnInfo(name = "tracks_number")
    var trackNo: Int,
    @ColumnInfo(name = "date_modified")
    var dateModified: Date
)

data class DraftTracksInProject(
    @Embedded val project: ProjectEntity,
    var numOfTracks : Int,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "project_id"
    )
    val tracks : List<DraftTracksEntity>
)

@Dao
interface ProjectDao{

}

@Dao
interface DraftTracksDao{

}
class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Database(entities = arrayOf(ProjectEntity::class, DraftTracksEntity::class), version = 1)
@TypeConverters(DateConverters::class)
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



