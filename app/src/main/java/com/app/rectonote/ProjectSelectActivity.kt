package com.app.rectonote

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.adapter.ProjectSelectAdapter
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import kotlinx.coroutines.runBlocking

class ProjectSelectActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val projectList = ArrayList<ProjectEntity>()
    lateinit var projectsDatabase: ProjectsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_select)
        projectsDatabase = ProjectsDatabase.getInstance(applicationContext) //??
        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_select)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "Select Project"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        recyclerView = findViewById(R.id.project_view_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        projectList.addAll(runBlocking {
            projectsDatabase.projectDAO().loadAllProjects()
        })
        val adapter = ProjectSelectAdapter(projectList, this)
        recyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    val newArrayList = ArrayList<ProjectEntity>()
                    if (newText.isNotEmpty()) {
                        projectList.clear()
                        val searchText = newText.toLowerCase()
                        runBlocking { projectsDatabase.projectDAO().loadAllProjects() }.forEach {
                            if (it.name.toLowerCase().contains(searchText)) projectList.add(it)
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
        }
        return true
    }
}