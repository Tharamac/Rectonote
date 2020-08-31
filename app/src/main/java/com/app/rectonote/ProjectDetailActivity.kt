package com.app.rectonote

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.database.DraftTracksViewModel
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.recyclerViewAdapter.DraftTracksAdapter
import kotlinx.coroutines.runBlocking


class ProjectDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var projectDatabase: ProjectsDatabase
    lateinit var adapter: DraftTracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val dbView = DraftTracksViewModel(projectDatabase.drafttracksDAO())
        val projectData = intent.getSerializableExtra("project") as ProjectEntity?
        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_detail)
        val toolbarTitle = findViewById<TextView>(R.id.project_detail_title)
        val projectTempo = findViewById<TextView>(R.id.project_tempo)
        val projectKey = findViewById<TextView>(R.id.project_key)
        val newTrackBtn = findViewById<CardView>(R.id.add_track_to_project_button)

        recyclerView = findViewById<RecyclerView>(R.id.tracks_list_view)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ProjectDetailActivity)
        }
        if (projectData != null) {
            toolbarTitle.text = projectData.name
            projectTempo.text = projectData.tempo.toString()
            projectKey.text = Key.reduceKey(projectData.key)
//            projectData.projectId?.let { projectId ->
//                dbView.loadTracksFromProject(projectId).observe(this, Observer {
//                    recyclerView.adapter = DraftTracksAdapter(it)
//                })
//            }

        }
        newTrackBtn.setOnClickListener(newTrackFormProjectDetail(projectData))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val projectData = intent.getSerializableExtra("project") as ProjectEntity?

        runBlocking {
            projectData?.projectId.let {
                if (it != null) {
                    adapter = DraftTracksAdapter(
                        projectDatabase.drafttracksDAO().loadTracksFromProject(
                            it
                        )
                    )
                }
            }
            recyclerView.adapter = adapter
        }
        adapter.notifyDataSetChanged()

    }

    private fun newTrackFormProjectDetail(projectData: ProjectEntity?) = View.OnClickListener {
        val intent = Intent(this, RecordingActivity::class.java)
        intent.putExtra("project", projectData?.name)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_project_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_change_project -> {
            true
        }
        R.id.action_delete_project -> {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("Are you want delete this project?")
                setPositiveButton("Yes") { _, _ ->
                    deleteProject()
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            val dialog = builder.create()
            dialog.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteProject() {
        val projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        if (projectData != null) {
            runBlocking {
                projectDatabase.projectDAO().deleteProject(projectData)
            }
        }
        finish()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.groupId

        return when (item.itemId) {
            1 -> {
                spawnDialogChangeName(position)


                true
            }
            2 -> {
                spawnDialogDeleteTrack(position)
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    private fun spawnDialogChangeName(trackViewId: Int) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        builder.apply {
            setTitle("Edit Name")
            setView(input)
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val changedName = input.text.toString()
                val isNameExisted = runBlocking {
                    projectDatabase.drafttracksDAO().loadTrackNames()
                }.any { eachName -> eachName == changedName }
                when {
                    changedName.isEmpty() -> {
                        Toast.makeText(
                            this@ProjectDetailActivity,
                            "Name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    isNameExisted -> {
                        Toast.makeText(
                            this@ProjectDetailActivity,
                            "This name is existed on this project.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    changedName.containsSpecialCharacters() -> {
                        Toast.makeText(
                            this@ProjectDetailActivity,
                            "Name Invalid",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    else -> {
                        changeTrackName(changedName, trackViewId)
                    }
                }


            })
            setNegativeButton("Cancel") { _, _ ->

            }
        }
        builder.create().show()
    }

    private fun changeTrackName(changedName: String, trackViewId: Int) {
        var trackData = adapter.getDatasetPosition(trackViewId)
        trackData.name = changedName
        runBlocking {
            projectDatabase.drafttracksDAO().changeData(trackData)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun spawnDialogDeleteTrack(trackViewId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you sure want to delete a track?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                deleteTrack(trackViewId)
            })
            setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->

            })
        }
        builder.create().show()
    }

    private fun deleteTrack(trackViewId: Int) {
        val trackData = adapter.getDatasetPosition(trackViewId)
        runBlocking {
            projectDatabase.drafttracksDAO().deleteTrack(trackData)
        }
        adapter.removeAt(trackViewId)
    }

}