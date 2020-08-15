package com.app.rectonote

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.rectonote.database.Key
import com.app.rectonote.database.ProjectDatabaseViewModel
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_project.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.absoluteValue

class AddTrackToProjectActivity : AppCompatActivity() {

    val color: Array<String> = arrayOf<String>("#DF008C","#0079d6", "#01706c","#007A41","#FF7600","#842E9A","#BE0423","#707070")
    private lateinit var projectsDatabase:ProjectsDatabase
    private lateinit var dbViewModel: ProjectDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_track_to_project)
        val projectData = intent.getSerializableExtra("project") as ProjectEntity?
        projectsDatabase = ProjectsDatabase.getInstance(applicationContext)
        dbViewModel = ProjectDatabaseViewModel(projectsDatabase.projectDAO())
        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_track)
        val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
        val optionsAdapter = ArrayAdapter<String>(this,R.layout.item_add_to_project_spinner,resources.getStringArray(R.array.draft_track_option))
        val projectSelector = findViewById<CardView>(R.id.btnProjectSelector)
        val btnConfirm = findViewById<FloatingActionButton>(R.id.fabtn_confirm)
        btnConfirm.setOnClickListener { _ ->
            val choice = findViewById<Spinner>(R.id.add_track_options_spinner).selectedItemPosition
            val trackNameInput = findViewById<EditText>(R.id.new_track_input).text.toString()
            if(trackNameInput.isBlank()) {
                Toast.makeText(this,"Track name cannot be empty" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val confirmDialog = AlertDialog.Builder(this)
            confirmDialog.setCancelable(false)

            if(choice == 0){
                //add new
                val projectNameInput = findViewById<EditText>(R.id.new_project_input).text.toString()
                if(projectNameInput.isBlank()){
                    Toast.makeText(this,"Project name cannot be empty" , Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val isNameExisted = runBlocking {
                    projectsDatabase.projectDAO().getNames()
                }.any { eachName -> eachName == projectNameInput }
                if(isNameExisted){
                    Toast.makeText(this,"\"${projectNameInput}\" existed.", Toast.LENGTH_LONG)
                }else {
                    confirmDialog.apply {
                        setMessage("Are you sure want to add this track to a new project (${projectNameInput})?")
                        setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                            addToNewProject(trackNameInput, projectNameInput)
                        })
                        setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
                    }
                }
            }else if(choice == 1) {
                    //add existing
                    if(projectData != null) {
                        confirmDialog.apply{
                            setMessage("Are you sure want to add this track to \"${projectData.name}\" project?")
                            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                                addToExistingProject(trackNameInput,projectData)
                            })
                            setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
                        }
                    }else{
                        Toast.makeText(this,"Project field cannot be blank " , Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

            }
            val dialog = confirmDialog.create()
            dialog.show()
        }



        addTrackOptions.adapter = optionsAdapter
        setSupportActionBar(toolbar)
        findViewById<TextView>(R.id.project_selected).text = projectData?.name ?: "<Tap to select project>"
        if((callingActivity?.className ?: "null") == ProjectSelectActivity::class.qualifiedName){
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


    private fun addToNewProject(trackName: String, projectName: String){
        val newProject = ProjectEntity(
            name = projectName,
            tempo = 128,
            key = Key.D.label,
            dateModified = Date(),
            color = color.random()
        )
        Log.d("NEW",newProject.toString())

    }

    private fun addToExistingProject(trackName: String,project: ProjectEntity){
        Log.d("EXIST" , project.toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

}


