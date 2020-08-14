package com.app.rectonote

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class AddTrackToProjectActivity : AppCompatActivity() {
    private lateinit var addTrackOptions:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_track_to_project)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_track)
        addTrackOptions = findViewById(R.id.add_track_options_spinner)
        val optionsAdapter = ArrayAdapter<String>(this,R.layout.simple_spinner_item,resources.getStringArray(R.array.draft_track_option))
        addTrackOptions.adapter = optionsAdapter
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}