package com.app.rectonote


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.app.rectonote.database.DraftTrackEntity
import com.app.rectonote.database.ProjectDatabaseViewModel
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.musictheory.Key
import com.app.rectonote.musictheory.Melody
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.util.*

class AddTrackToProjectActivity : AppCompatActivity() {

    val color: Array<String> = arrayOf<String>(
        "#DF008C",
        "#0079d6",
        "#01706c",
        "#007A41",
        "#FF7600",
        "#842E9A",
        "#BE0423",
        "#707070"
    )
    private val projectsDatabase: ProjectsDatabase by lazy {
        ProjectsDatabase.getInstance(applicationContext)
    }
    private val dbViewModel: ProjectDatabaseViewModel by lazy {
        ProjectDatabaseViewModel(projectsDatabase.projectDAO())
    }
    private var projectData: ProjectEntity? = null
    private external fun debug(): String
    private external suspend fun startConvert(
        fs: Int,
        audioPath: String,
    ): IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_track_to_project)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_track)
        findViewById<TextView>(R.id.track_name).text = "Play Processed Track"
        val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
        val btnConfirm = findViewById<FloatingActionButton>(R.id.fabtn_confirm)
        btnConfirm.setOnClickListener(confirmAddTrack)
        val projectsFormProjectDetail = intent.getStringExtra("projectFromProjectDetail")
        val projectCard = findViewById<CardView>(R.id.btn_project_selector)
        val optionsAdapter = ArrayAdapter<String>(
            this,
            R.layout.item_add_to_project_spinner,
            resources.getStringArray(R.array.draft_track_option)
        )

        addTrackOptions.adapter = optionsAdapter
        setSupportActionBar(toolbar)
//        if ((callingActivity?.className ?: "null") == ProjectSelectActivity::class.qualifiedName) {
//            addTrackOptions.setSelection(optionsAdapter.getPosition("Add to Existing Project"))
//        }

        val selectButton = findViewById<TextView>(R.id.project_selected)
        if (projectsFormProjectDetail != null) {
            addTrackOptions.setSelection(1)
            projectData = runBlocking {
                projectsDatabase.projectDAO().getProjectFromName(projectsFormProjectDetail)
            }[0]
            selectButton.text = projectsFormProjectDetail
            projectCard.setBackgroundColor(Color.parseColor(projectData!!.color))
        } else {
            selectButton.text = "<Tap to select project>"
            projectCard.setBackgroundColor(Color.parseColor("#777777"))
        }

        projectCard.setOnClickListener { _ ->
            val intent = Intent(this, ProjectSelectActivity::class.java)
            startActivityForResult(intent, 1)
        }
        addTrackOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            val addToNew = findViewById<LinearLayout>(R.id.add_new_proj)
            val addExisting = findViewById<LinearLayout>(R.id.add_existing_proj)
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    addToNew.visibility = View.VISIBLE
                    addToNew.isEnabled = true
                    addExisting.visibility = View.INVISIBLE
                    addExisting.isEnabled = false
                } else if (position == 1) {
                    addExisting.visibility = View.VISIBLE
                    addExisting.isEnabled = true
                    addToNew.visibility = View.INVISIBLE
                    addToNew.isEnabled = false
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val cppScope = CoroutineScope(Dispatchers.Default + Job())
        var deferredMelody = cppScope.async {
            audioConvert()
        }
        cppScope.launch {
            val melody = deferredMelody.await()
            findViewById<TextView>(R.id.project_tempo).text = melody.tempo.toString()
            findViewById<TextView>(R.id.project_key).text = melody.key?.reduced
        }


    }

    private suspend fun audioConvert(): Melody {
        val mode = intent.getStringExtra("convert_mode")
        val cppOut = withContext(Dispatchers.Default) {
            startConvert(44100, "${filesDir}/voice16bit.pcm")
        }
        var detectedNoteResult = withContext(Dispatchers.Default) {
            Note.transformNotes(cppOut, Note(NotePitch.C, 3))
        }
        Log.i("NOTEOUT", detectedNoteResult.contentToString())
        var melody = withContext(Dispatchers.Default) {
            Melody(detectedNoteResult)
        }
        Log.i("NOTEOUT", melody.toString())
        return melody
    }


    override fun onSupportNavigateUp(): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you want discard this track?")
            setPositiveButton("Yes") { _, _ ->
                super.onSupportNavigateUp()
            }
            setNegativeButton("No") { _, _ -> }
        }
        val dialog = builder.create()
        dialog.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val addTrackOptions = findViewById<Spinner>(R.id.add_track_options_spinner)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                addTrackOptions.setSelection(1)
                projectData = data?.getSerializableExtra("project") as ProjectEntity
                val selectButton = findViewById<TextView>(R.id.project_selected)
                selectButton.text = projectData?.name
                val projectCard = findViewById<CardView>(R.id.btn_project_selector)
                Log.d("h", projectData!!.color)
                projectCard.setBackgroundColor(Color.parseColor(projectData!!.color))
                Log.d("h", projectCard.cardBackgroundColor.toString())
            }
        }
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

    private fun addToNewProject(trackName: String, projectName: String) {
        val trackTempo = 128
        val trackKey = Key.D
        val newProject = ProjectEntity(
            name = projectName,
            tempo = trackTempo,
            key = trackKey.label,
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
        val backIntent = Intent(this, MainActivity::class.java)
        backIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
        startActivity(backIntent)
    }

    private fun addToExistingProject(trackName: String, project: ProjectEntity) {
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
        val backIntent = Intent(this, ProjectDetailActivity::class.java)
        backIntent.putExtra("project", project)
        backIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
        startActivity(backIntent)
    }


    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you want discard this track?")
            setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            setNegativeButton("No") { _, _ ->
            }
        }
        val dialog = builder.create()
        dialog.show()

    }

}


