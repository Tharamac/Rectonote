package com.app.rectonote

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.app.rectonote.database.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.runBlocking
import java.util.*

class AddTrackToProjectActivity : AppCompatActivity() {

    val color: Array<String> = arrayOf<String>("#DF008C","#0079d6", "#01706c","#007A41","#FF7600","#842E9A","#BE0423","#707070")
    private lateinit var projectsDatabase:ProjectsDatabase
    private lateinit var dbViewModel: ProjectDatabaseViewModel
    private var projectData: ProjectEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_track_to_project)
        projectsDatabase = ProjectsDatabase.getInstance(applicationContext)
        dbViewModel = ProjectDatabaseViewModel(projectsDatabase.projectDAO())
        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_track)
        findViewById<TextView>(R.id.track_name).text = "Play Processed Track"
        val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
        val optionsAdapter = ArrayAdapter<String>(this,R.layout.item_add_to_project_spinner,resources.getStringArray(R.array.draft_track_option))
        val btnConfirm = findViewById<FloatingActionButton>(R.id.fabtn_confirm)
        btnConfirm.setOnClickListener(confirmAddTrack)

        addTrackOptions.adapter = optionsAdapter
        setSupportActionBar(toolbar)
        val selectButton = findViewById<TextView>(R.id.project_selected)
        selectButton.text = "<Tap to select project>"
        val projectCard = findViewById<CardView>(R.id.btn_project_selector)
        projectCard.setCardBackgroundColor(Color.parseColor( "#777777"))
        if((callingActivity?.className ?: "null") == ProjectSelectActivity::class.qualifiedName){
            addTrackOptions.setSelection(optionsAdapter.getPosition("Add to Existing Project"))
        }
        projectCard.setOnClickListener { _ ->
            val intent = Intent(this, ProjectSelectActivity::class.java)
            startActivityForResult(intent,1)
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

    private val confirmAddTrack = View.OnClickListener {
        val choice = findViewById<Spinner>(R.id.add_track_options_spinner).selectedItemPosition
        val trackNameInput = findViewById<EditText>(R.id.new_track_input).text.toString()
        if (trackNameInput.isBlank()) {
            Toast.makeText(this, "Track name cannot be empty", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        } else if (trackNameInput.containsSpecialCharacters()) {
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        val confirmDialog = AlertDialog.Builder(this)
        confirmDialog.setCancelable(false)

        if (choice == 0) {
            //add new
            val projectNameInput = findViewById<EditText>(R.id.new_project_input).text.toString()
            val isNameExisted = runBlocking {
                projectsDatabase.projectDAO().getNames()
            }.any { eachName -> eachName == projectNameInput }
            if (projectNameInput.isBlank()) {
                Toast.makeText(this, "Project name cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (projectNameInput.containsSpecialCharacters()) {
                Toast.makeText(this, "Project name invalid", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (isNameExisted) {
                Toast.makeText(this, "\"${projectNameInput}\" existed.", Toast.LENGTH_LONG)
            } else {
                confirmDialog.apply {
                    setMessage("Are you sure want to add this track to a new project (${projectNameInput})?")
                    setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                        addToNewProject(trackNameInput, projectNameInput)
                    })
                    setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
                }
            }
        } else if (choice == 1) {
            //add existing
            val isNameExisted = runBlocking {
                projectsDatabase.drafttracksDAO().loadTrackNames()
            }.any { eachName -> eachName == trackNameInput }
            if (projectData != null) {
                if (isNameExisted) {
                    Toast.makeText(
                        this,
                        "\"${trackNameInput}\" existed in \"${projectData!!.name}\".",
                        Toast.LENGTH_LONG
                    )
                } else {
                    confirmDialog.apply {
                        setMessage("Are you sure want to add this track to \"${projectData!!.name}\" project?")
                        setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                            addToExistingProject(trackNameInput, projectData!!)
                        })
                        setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
                    }
                }
            } else {
                Toast.makeText(this, "Project field cannot be blank ", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

        }
        val dialog = confirmDialog.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
                addTrackOptions.setSelection(1)
                projectData = data?.getSerializableExtra("project") as ProjectEntity
                val selectButton = findViewById<TextView>(R.id.project_selected)
                selectButton.text = projectData?.name
                val projectCard = findViewById<CardView>(R.id.btn_project_selector)
                projectCard.setCardBackgroundColor(Color.parseColor(projectData?.color))
            }
        }
    }

    private fun addToNewProject(trackName: String, projectName: String){
        val trackTempo = 128
        val trackKey = Key.D
        val newProject = ProjectEntity(
            name = projectName,
            tempo = trackTempo,            //dummy param
            key = trackKey.label,      //dummy param
            dateModified = Date(),
            color = color.random()
        )
        var targetProjectId: Int = -1
        runBlocking {
            projectsDatabase.projectDAO().newProject(newProject)
            targetProjectId = projectsDatabase.projectDAO().getIdFromProject(projectName)[0]
        }
        val newTrack = DraftTrackEntity(
            name = trackName,
            tempo = trackTempo,
            type = "Melody",
            key = trackKey.label,
            dateModified = Date(),
            color = "#590044",
            projectId = targetProjectId
        )
        runBlocking {
            projectsDatabase.drafttracksDAO().newDraftTrack(newTrack)
        }
        finish()

    }

    private fun addToExistingProject(trackName: String,project: ProjectEntity){
        val newTrack = project.projectId?.let {
            DraftTrackEntity(
                name = trackName,
                tempo = project.tempo,
                type = "Melody",
                key = project.key,
                dateModified = Date(),
                color = "#590044",
                projectId = it
            )
        }
        runBlocking {
            if (newTrack != null) {
                projectsDatabase.drafttracksDAO().newDraftTrack(newTrack)
            }
        }
        finish()
        Log.d("EXIST" , project.toString())
    }


    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you want discard this track?")
            setPositiveButton("Yes"){ _, _ ->
                super.onBackPressed()
            }
            setNegativeButton("No"){_,_->{

            }}
        }
        val dialog = builder.create()
        dialog.show()

    }

}


