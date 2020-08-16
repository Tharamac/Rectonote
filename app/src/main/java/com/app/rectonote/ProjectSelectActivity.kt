package com.app.rectonote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.database.ProjectDatabaseViewModel
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.listAdapter.ProjectListAdapter
import com.app.rectonote.listAdapter.ProjectSelectAdapter
import java.security.interfaces.RSAMultiPrimePrivateCrtKey

class ProjectSelectActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    lateinit var dbView: ProjectDatabaseViewModel
    lateinit var projectsDatabase: ProjectsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_select)
        projectsDatabase = ProjectsDatabase.getInstance(applicationContext) //??
        dbView = ProjectDatabaseViewModel(projectsDatabase.projectDAO())
        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_select)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "Select Project"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        recyclerView = findViewById(R.id.project_view_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dbView.loadAllProjects().observe(this, androidx.lifecycle.Observer {
            recyclerView.adapter = ProjectSelectAdapter(it, this)
        })

    }
}