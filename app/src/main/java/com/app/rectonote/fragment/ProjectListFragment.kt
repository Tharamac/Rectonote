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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectsDatabase = ProjectsDatabase.getInstance(requireActivity().applicationContext) //??
        dbView = ProjectDatabaseViewModel(projectsDatabase.projectDAO())

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

    companion object
}