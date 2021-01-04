package com.app.rectonote

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.rectonote.adapter.PreviewTrackAdapter
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.fragment.ProjectDataFragment
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.musictheory.*
import kotlinx.coroutines.*
import java.util.*


class ProjectDetailActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private val titles = arrayOf("DETAIL", "PREVIEW")
    lateinit var fragment: ProjectDataFragment
    lateinit var recyclerView: RecyclerView
    lateinit var projectDatabase: ProjectsDatabase
    lateinit var projectData: ProjectEntity
    private var isPlaying: Boolean = false
    lateinit var adapter: PreviewTrackAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        projectDatabase = applicationContext?.let { ProjectsDatabase.getInstance(it) }!!
        setContentView(R.layout.activity_project_detail)
        recyclerView = findViewById<RecyclerView>(R.id.preview_track_list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
        projectData = intent.getSerializableExtra("project") as ProjectEntity
        val tempoDisplay = findViewById<TextView>(R.id.project_tempo)
        val keyDisplay = findViewById<TextView>(R.id.project_key)
        tempoDisplay.text = projectData.tempo.toString()
        keyDisplay.text = projectData.key.reduced

        val toolbarTitle = findViewById<TextView>(R.id.project_detail_title)
        toolbarTitle.text = projectData.name

        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val audioOutScope = CoroutineScope(Dispatchers.IO)
        val brightMelodyChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = Key.Gb,
                tempo = 45,
                trackType = "melody",
                trackSequence = arrayListOf(
                    Note(NotePitch.Gb, 4).apply { duration = 4 },
                    Note(NotePitch.Gb, 4).apply { duration = 2 },
                    Note(NotePitch.Ab, 4).apply { duration = 2 },
                    Note(NotePitch.Bb, 4).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(NotePitch.Bb, 4).apply { duration = 2 },
                    Note(NotePitch.Db, 5).apply { duration = 2 },
                    Note(NotePitch.Eb, 5).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(NotePitch.Eb, 5).apply { duration = 2 },
                    Note(NotePitch.Db, 5).apply { duration = 2 },
                    Note(NotePitch.Bb, 4).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(NotePitch.Bb, 4).apply { duration = 1 },
                    Note(NotePitch.Db, 5).apply { duration = 3 },
                )
            )
        )
        val brightChordChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = Key.Gb,
                tempo = 45,
                trackType = "chord",
                trackSequence = arrayListOf(
                    Chord(NotePitch.Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(NotePitch.Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(NotePitch.Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(NotePitch.Gb, 4).apply { chordType = "major"; duration = 8 },


                    )
            )
        )
        val tempSoundfontPath: String = "$filesDir/tmp_sndfnt.sf2"
        brightChordChannel.nativeLoadSoundfont(tempSoundfontPath)
        brightMelodyChannel.nativeLoadSoundfont(tempSoundfontPath)
        brightChordChannel.nativeLoadPreset(0, 0, 0)
        brightMelodyChannel.nativeLoadPreset(0, 0, 35)
        val playMultiTrackButton = findViewById<CardView>(R.id.play_selected_button)
        val multiTrackText = findViewById<TextView>(R.id.multi_track_text)
        val multiTrackIcon = findViewById<ImageView>(R.id.multi_track_icon)
        playMultiTrackButton.setOnClickListener {
            if (!isPlaying) {
                multiTrackText.text = "Stop".toUpperCase(Locale.ROOT)
                multiTrackIcon.setImageResource(R.drawable.ic_baseline_stop_24)
                audioOutScope.launch {
                    brightChordChannel.playDraftTrackSequence()
                }
                audioOutScope.launch {
                    brightMelodyChannel.playDraftTrackSequence()
                    withContext(Dispatchers.Main) {
                        isPlaying = false
                        multiTrackText.text = "Play Selected".toUpperCase(Locale.ROOT)
                        multiTrackIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
                isPlaying = true


            } else {
                multiTrackText.text = "Play Selected".toUpperCase(Locale.ROOT)
                multiTrackIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                audioOutScope.launch {
                    brightChordChannel.stopMessage()
                }
                audioOutScope.launch {
                    brightMelodyChannel.stopMessage()
                }

                isPlaying = false
            }


        }


//        fragment = ProjectDataFragment()
//        transaction = supportFragmentManager.beginTransaction()
//        transaction.add(R.id.project_detail,fragment)
//        transaction.commit()

    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            projectData.projectId?.let {
                adapter = PreviewTrackAdapter(
                    projectDatabase.drafttracksDAO().loadTracksFromProject(it)
                )
            }
            recyclerView.adapter = adapter
        }
        adapter.notifyDataSetChanged()


    }

    //toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_project_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_delete_project -> {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("Are you want delete this project?")
                setPositiveButton("Yes") { _, _ ->
                    deleteProject()
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            val dialog = builder.create()
            dialog.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteProject() {
        val projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        if (projectData != null) {
            runBlocking {
                projectDatabase.projectDAO().deleteProject(projectData)
            }
        }
        finish()
    }

    private fun newTrackFormProjectDetail(projectData: ProjectEntity?) = View.OnClickListener {
        val intent = Intent(this, RecordingActivity::class.java)
        intent.putExtra("project", projectData?.name)
        startActivity(intent)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.groupId

        return when (item.itemId) {
            1 -> {
                spawnDialogChangeName(position)
                true
            }
            2 -> {
                spawnDialogDeleteTrack(position)
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    private fun spawnDialogChangeName(trackViewId: Int) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        builder.apply {
            setTitle("Edit Name")
            setView(input)
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val changedName = input.text.toString()
                val isNameExisted = runBlocking {
                    projectDatabase.drafttracksDAO().loadTrackNames()
                }.any { eachName -> eachName == changedName }
                when {
                    changedName.isEmpty() -> {
                        Toast.makeText(
                            context,
                            "Name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    isNameExisted -> {
                        Toast.makeText(
                            context,
                            "This name is existed on this project.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    changedName.containsIllegalCharacters() -> {
                        Toast.makeText(
                            context,
                            "Name Invalid",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    else -> {
                        changeTrackName(changedName, trackViewId)
                    }
                }


            })
            setNegativeButton("Cancel") { _, _ ->

            }
        }
        builder.create().show()
    }

    private fun changeTrackName(changedName: String, trackViewId: Int) {
        val trackData = adapter.getDatasetPosition(trackViewId)
        trackData.name = changedName
        runBlocking {
            projectDatabase.drafttracksDAO().changeData(trackData)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun spawnDialogDeleteTrack(trackViewId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Are you sure want to delete a track?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                deleteTrack(trackViewId)
            })
            setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->

            })
        }
        builder.create().show()
    }

    private fun deleteTrack(trackViewId: Int) {
        val trackData = adapter.getDatasetPosition(trackViewId)
        runBlocking {
            projectDatabase.drafttracksDAO().deleteTrack(trackData)
        }
        adapter.removeAt(trackViewId)
    }


}