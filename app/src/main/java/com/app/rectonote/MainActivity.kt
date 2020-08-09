package com.app.rectonote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.rectonote.database.ProjectDatabaseViewModel
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import java.util.*

enum class Key {
    C, Db, D, Eb, E, F, Gb, G, Ab, A, Bb, B;

    //C, C#, D, D#, E, F, F#, G, G#, A, A#, B
    companion object{
        fun fromInt(value: Int) = Key.values().first { it.ordinal == value }
    }

}


class MainActivity : AppCompatActivity() {

    //lateinit var projectsDatabase: ProjectsDatabase
    var p1 = ProjectEntity(
        name = "1",
        tempo = 125,
        dateModified = Date(),
        key = Key.D
    )
    //lateinit val dbView: ProjectDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val projectsDatabase = ProjectsDatabase.getInstance(applicationContext);
        val dbView = ProjectDatabaseViewModel(projectsDatabase.projectDAO())
        dbView.newProjects(p1)
        dbView.loadAllProjects().observe(this, androidx.lifecycle.Observer {
            Log.d("DB", it.toString())
        })
        // Example of a call to a native method
        //sample_text.text = stringFromJNI()
    }

    fun addIdea(view: View) {
        val intent = Intent(this, AddNewIdea::class.java)
        startActivity(intent)
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
