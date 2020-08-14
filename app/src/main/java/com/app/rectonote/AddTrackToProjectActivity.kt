package com.app.rectonote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.app.rectonote.database.ProjectEntity

class AddTrackToProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_track_to_project)
        val projectData = intent.getSerializableExtra("project") as ProjectEntity?

        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_track)
        val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
        val optionsAdapter = ArrayAdapter<String>(this,R.layout.item_add_to_project_spinner,resources.getStringArray(R.array.draft_track_option))
        val projectSelector = findViewById<CardView>(R.id.btnProjectSelector)

        addTrackOptions.adapter = optionsAdapter
        setSupportActionBar(toolbar)
        findViewById<TextView>(R.id.project_selected).text = projectData?.name ?: "<Tap to select project>"
        Log.d("ADD_TO_PROJECT",callingActivity?.className ?: "null")
        Log.d("ADD_TO_PROJECT",ProjectSelectActivity::class.qualifiedName)
        if(callingActivity?.className ?: "null"  == ProjectSelectActivity::class.qualifiedName){
            addTrackOptions.setSelection(optionsAdapter.getPosition("Add to Existing Project"))
        }
        projectSelector.setOnClickListener { _ ->
            val intent = Intent(this, ProjectSelectActivity::class.java)
            startActivity(intent)
        }
        addTrackOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            val addToNew = findViewById<LinearLayout>(R.id.add_new_proj)
            val addExisting = findViewById<LinearLayout>(R.id.add_existing_proj)
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
               if(position == 0){
                   addToNew.visibility = View.VISIBLE
                   addToNew.isEnabled = true
                   addExisting.visibility = View.INVISIBLE
                   addExisting.isEnabled = false
               }else if(position == 1){
                   addExisting.visibility = View.VISIBLE
                   addExisting.isEnabled = true
                   addToNew.visibility = View.INVISIBLE
                   addToNew.isEnabled = false
               }
            }
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}


