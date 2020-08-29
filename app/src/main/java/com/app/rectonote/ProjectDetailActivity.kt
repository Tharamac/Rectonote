package com.app.rectonote

import android.content.DialogInterface
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.database.DraftTracksViewModel
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.listAdapter.DraftTracksAdapter
import kotlinx.coroutines.runBlocking

class ProjectDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        val projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val dbView = DraftTracksViewModel(projectDatabase.drafttracksDAO())
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_detail)
        val toolbarTitle = findViewById<TextView>(R.id.project_detail_title)
        val projectTempo = findViewById<TextView>(R.id.project_tempo)
        val projectKey = findViewById<TextView>(R.id.project_key)
        Log.d("ProjectDetail", projectData.toString())
        recyclerView = findViewById<RecyclerView>(R.id.tracks_list_view)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ProjectDetailActivity)
        }
        if (projectData != null) {
            toolbarTitle.text = projectData.name
            projectTempo.text = projectData.tempo.toString()
            projectKey.text = Key.reduceKey(projectData.key)
            projectData.projectId?.let {projectId ->
                dbView.loadTracksFromProject(projectId).observe(this, Observer {
                    recyclerView.adapter = DraftTracksAdapter(it)
                })
            }

        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_project_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.action_change_project -> {
            true
        }
        R.id.action_delete_project ->{
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("Are you want delete this project?")
                setPositiveButton("Yes"){ _, _ ->
                    deleteProject()
                }
                setNegativeButton("No"){_,_->

                }
            }
            val dialog = builder.create()
            dialog.show()
            true
        }
        else ->{
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteProject(){
        val projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        if (projectData != null) {
            runBlocking {
               projectDatabase.projectDAO().deleteProject(projectData)
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            1 -> {
                spawnDialogChangeName()
                true
            }
            2 -> {
                spawnDialogDeleteTrack()
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    private fun spawnDialogChangeName(){
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        builder.apply {
            setTitle("Edit Name")
            setView(input)
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val changedName = input.text
                if(changedName.isEmpty()){
                    Toast.makeText(this@ProjectDetailActivity,"Name cannot be empty",Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

            })
            setNegativeButton("Cancel") { _, _ ->

            }
        }
        builder.create().show()
    }
    private fun spawnDialogDeleteTrack(){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you sure want to delete a track?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

            })
            setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->

            })
        }
        builder.create().show()
    }

}