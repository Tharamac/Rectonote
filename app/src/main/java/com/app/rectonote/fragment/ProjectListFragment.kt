package com.app.rectonote.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.R
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectDatabaseViewModel
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.listAdapter.ProjectListAdapter
import java.util.*

class ProjectListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var dbView: ProjectDatabaseViewModel
    lateinit var projectsDatabase: ProjectsDatabase
    var p1 = ProjectEntity(
        name = "1",
        tempo = 125,
        dateModified = Date(),
        key = Key.D.label
    )
    var p2 = ProjectEntity(
        name = "Tomorrow",
        tempo = 75,
        dateModified = Date(),
        key = Key.Abm.label
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectsDatabase = ProjectsDatabase.getInstance(activity!!.applicationContext); //??
        dbView = ProjectDatabaseViewModel(projectsDatabase.projectDAO())
        //dbView.newProjects(p1, p2)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_project_list, container, false)
        recyclerView = view.findViewById(R.id.project_view_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        dbView.loadAllProjects().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            recyclerView.adapter = ProjectListAdapter(it)
        })
        return view
    }

    companion object {

    }
}