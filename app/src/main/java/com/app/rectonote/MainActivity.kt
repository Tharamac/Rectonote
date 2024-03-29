package com.app.rectonote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.adapter.ProjectListAdapter
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var projectsDatabase: ProjectsDatabase
    lateinit var adapter: ProjectListAdapter
    private lateinit var projectSearch: SearchView
    private val projectList = ArrayList<ProjectEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.appbar_main)
        projectsDatabase = ProjectsDatabase.getInstance(applicationContext)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        recyclerView = findViewById(R.id.project_view_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        projectSearch = findViewById<SearchView>(R.id.project_search)
        copyAssetToTempFile("sndfnt.sf2")
        copyAssetToTempFile("Araiwa.mid")
    }

    fun copyAssetToTempFile(fileName: String) {
        assets.open(fileName).use { `is` ->
            val tempFileName = "tmp_$fileName"
            openFileOutput(tempFileName, Context.MODE_PRIVATE).use { fos ->
                var bytesRead: Int
                val buffer = ByteArray(4096)
                while (`is`.read(buffer).also { bytesRead = it } != -1) {
                    fos.write(buffer, 0, bytesRead)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        projectList.clear()
        projectList.addAll(runBlocking {
            projectsDatabase.projectDAO().loadAllProjects()
        })
        runBlocking {
            adapter = ProjectListAdapter(projectList)
            recyclerView.adapter = adapter
            if (adapter.itemCount != 0) {
                findViewById<TextView>(R.id.introText).visibility = GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val searchView = findViewById<SearchView>(R.id.project_search)
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    projectList.clear()
                    val searchText = newText.toLowerCase(Locale.ROOT)
                    runBlocking { projectsDatabase.projectDAO().loadAllProjects() }.forEach {
                        if (it.name.toLowerCase(Locale.ROOT).contains(searchText)) projectList.add(
                            it
                        )
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()

                } else {
                    projectList.clear()
                    projectList.addAll(runBlocking {
                        projectsDatabase.projectDAO().loadAllProjects()
                    })
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }
        })


        return true
    }

    fun addIdea(view: View) {
        val intent = Intent(this, RecordingActivity::class.java)
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
