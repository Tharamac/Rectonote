package com.app.rectonote

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectEntity

class ProjectDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_detail)
        val toolbarTitle = findViewById<TextView>(R.id.project_detail_title)
        val projectTempo = findViewById<TextView>(R.id.project_tempo)
        val projectKey = findViewById<TextView>(R.id.project_key)
        Log.d("ProjectDetail", projectData.toString())
        if (projectData != null) {
            toolbarTitle.text = projectData.name
            projectTempo.text = projectData.tempo.toString()
            projectKey.text = Key.reduceKey(projectData.key)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

}