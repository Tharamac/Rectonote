package com.app.rectonote


import android.app.Activity
import android.content.Context
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
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import com.app.rectonote.musictheory.TrackSequencer
import com.beust.klaxon.Klaxon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*


class AddTrackToProjectActivity : AppCompatActivity() {
    lateinit var draftTrack: DraftTrackData

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
        val trackTypeIcon = findViewById<ImageView>(R.id.track_type)
        val trackTypeLabel = findViewById<TextView>(R.id.track_type_label)
        val mode = intent.getStringExtra("convert_mode")?.split(" ")?.get(2)!!
        Log.d("MODE", mode)
        if (mode.toLowerCase() == "chord") trackTypeIcon.setImageResource(R.drawable.ic_baseline_queue_music_24)
        trackTypeLabel.text = mode

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
        val tempoDisplay = findViewById<TextView>(R.id.project_tempo)
        val keyDisplay = findViewById<TextView>(R.id.project_key)
        cppScope.launch {
            val draftTrack = audioConvert()
            withContext(Dispatchers.Main) {
                tempoDisplay.text = draftTrack.tempo.toString()
                keyDisplay.text = draftTrack.key.reduced
                btnConfirm.visibility = View.VISIBLE
            }
            Log.i("NOTEOUT", Klaxon().toJsonString(draftTrack.trackSequence))
        }
        val soloTrackButton = findViewById<ImageButton>(R.id.solo_button)
        var isPlaying = false
        soloTrackButton.setOnClickListener {
            val soloChannel = MIDIPlayerChannel(draftTrack)
            val audioOutScope = CoroutineScope(Dispatchers.IO)
            soloChannel.nativeLoadSoundfont("$filesDir/tmp_sndfnt.sf2")
            if (!isPlaying) {
                soloTrackButton.setImageResource(R.drawable.ic_outline_stop_circle_24)
                audioOutScope.launch {
                    soloChannel.playDraftTrackSequence()
                    withContext(Dispatchers.Main) {
                        isPlaying = false
                        soloTrackButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                }
                isPlaying = true


            } else {
                audioOutScope.launch {
                    soloChannel.stopMessage()
                }
                isPlaying = false
            }
        }


    }


    @Throws(IOException::class)
    fun copyAssetToTempFile(fileName: String): String {
        assets.open(fileName).use { `is` ->
            val tempFileName = "tmp_$fileName"
            openFileOutput(tempFileName, Context.MODE_PRIVATE).use { fos ->
                var bytesRead: Int
                val buffer = ByteArray(4096)
                while (`is`.read(buffer).also { bytesRead = it } != -1) {
                    fos.write(buffer, 0, bytesRead)
                }
            }
            return "$filesDir/$tempFileName"
        }
    }

    private suspend fun audioConvert(): DraftTrackData {
        val mode = intent.getStringExtra("convert_mode")?.split(" ")?.get(2)!!

        val cppOut =
            startConvert(8000, "${filesDir}/voice16bit.pcm")

        Log.i("NOTEOUT", cppOut.contentToString())
        val detectedNoteResult = Note.transformNotes(cppOut, Note(NotePitch.C, 3))
        Log.i("NOTEOUT", detectedNoteResult.contentToString())
        val trackSequencer = TrackSequencer()
        var melody = trackSequencer.generateTrack(detectedNoteResult, mode)

        melody =
            trackSequencer.removeNoise(melody)

        melody =
            trackSequencer.cleanTrack(melody)

        melody =
            trackSequencer.calcDurations(melody)

        val pitchProfile =
            trackSequencer.calcPitchProfile(melody)

        val key =
            trackSequencer.calcKey(pitchProfile)

        val tempo =
            trackSequencer.calcTempo(melody, 0.04)
        melody =
            trackSequencer.chordCorrect(melody, key)
        return DraftTrackData(key, tempo, mode, melody)
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
        } else if (trackNameInput.containsIllegalCharacters()) {
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
            } else if (projectNameInput.containsIllegalCharacters()) {
                Toast.makeText(this, "Project name invalid", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (isNameExisted) {
                Toast.makeText(this, "\"${projectNameInput}\" existed.", Toast.LENGTH_LONG).show()
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
                    ).show()
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
        val projectDir = "${getExternalFilesDir(null)}/$projectName"
        File(projectDir).mkdir()
        val fileDir = "$projectDir/$trackName.json"
        File(fileDir).writeText(Klaxon().toJsonString(draftTrack.trackSequence))
        val trackTempo = draftTrack.tempo
        val trackKey = draftTrack.key
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
        val projectDir = "${getExternalFilesDir(null)}/${project.name}"
        val fileDir = "$projectDir/$trackName.json"
        File(fileDir).writeText(Klaxon().toJsonString(draftTrack.trackSequence))
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


