package com.app.rectonote

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.app.rectonote.database.DraftTrackEntity
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.fragment.ProjectDataFragment
import com.app.rectonote.midiplayback.DraftTrackJsonParser
import com.app.rectonote.midiplayback.GeneralMidiPreset
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.midiplayback.TrackChannelStatus
import com.app.rectonote.musictheory.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class ProjectDetailActivity :
    AppCompatActivity(),
    PreviewTrackAdapter.ChannelStatusCallback {
    private lateinit var viewPager2: ViewPager2
    private val titles = arrayOf("DETAIL", "PREVIEW")
    lateinit var fragment: ProjectDataFragment
    lateinit var recyclerView: RecyclerView
    lateinit var projectDatabase: ProjectsDatabase
    lateinit var projectData: ProjectEntity
    private var isPlaying: Boolean = false
    lateinit var draftTracksData: MutableList<DraftTrackEntity>
    lateinit var adapter: PreviewTrackAdapter
    private val midiChannels = arrayListOf<MIDIPlayerChannel>()
    private val presetState = arrayListOf<TrackChannelStatus>()
    lateinit var playMultiTrackButton: CardView
    private var trackSequenceList = listOf<ArrayList<out Note>>()
    private val audioOutScope = CoroutineScope(Dispatchers.IO)
    private val loadDataScope = CoroutineScope(Dispatchers.IO)
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
        findViewById<CardView>(R.id.add_track_to_project_button).setOnClickListener(
            newTrackFormProjectDetail(projectData)
        )
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
        playMultiTrackButton = findViewById<CardView>(R.id.play_selected_button)
        playMultiTrackButton.setOnClickListener(playSelected)


    }

    private val playSelected = View.OnClickListener {
        val multiTrackText = findViewById<TextView>(R.id.multi_track_text)
        val multiTrackIcon = findViewById<ImageView>(R.id.multi_track_icon)
        val playSelectedChannels = presetState.map {
            MIDIPlayerChannel(
                DraftTrackData(
                    key = projectData.key,
                    tempo = projectData.tempo,
                    trackType = it.trackType,
                    trackSequence = it.trackSequence
                )
            ).apply {
                nativeLoadSoundfont("$filesDir/tmp_sndfnt.sf2")
                loadPreset(
                    channel = 0,
                    preset = it.preset
                )
                muted = it.muted
            }
        }
        GlobalScope.launch {
            var playingStatus = listOf<Job>()
            if (!isPlaying) {
                withContext(Dispatchers.Main) {
                    //multiTrackIcon.setImageResource(R.drawable.ic_baseline_stop_24)
                    multiTrackText.text = "PLAYING"
                    isPlaying = true
                }

                playingStatus = playSelectedChannels.map {
                    launch {
                        Log.i("play", Thread.currentThread().name)
                        it.playDraftTrackSequence()
                    }
                }
                playingStatus.joinAll()
                withContext(Dispatchers.Main) {
                    isPlaying = false
                    multiTrackIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    multiTrackText.text = "PLAY SELECTED"
                }

            } else {

                playSelectedChannels.forEach {
                    audioOutScope.launch {
                        it.stopMessage()
                        it.nativeRemovePlayer()
                    }
                }

//                withContext(Dispatchers.Main) {
//                    multiTrackIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
//                    multiTrackText.text = "PLAY SELECTED"
//                    isPlaying = false
//                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        presetState.clear()

        val fileList =
            File("${getExternalFilesDir(null)}/${projectData.name}/").walk().toList().filter {
                it.extension == "json"
            }.sortedByDescending {
                it.name
            }
        val jsonDraftTracksList = fileList.map {
            it.readText()
        }
        Log.i(
            "555", "${
                fileList.map {
                    it.name
                }
            }"
        )
        GlobalScope.launch(Dispatchers.Main) {
            val draftTracksDeferred = jsonDraftTracksList.map {
                CoroutineScope(Dispatchers.Default).async {

                    DraftTrackJsonParser.draftTrackJSONParse(it)

                }
            }

            val draftTrackListDeferred = CoroutineScope(Dispatchers.IO).async {
                projectDatabase.drafttracksDAO().loadTracksFromProject(
                    projectData.projectId!!
                )

            }
            draftTracksData = draftTrackListDeferred.await()
            Log.i("load Finished", draftTracksData.toString())
            val draftTracks = draftTracksDeferred.awaitAll()
            Log.i("parse Finished", draftTracks.toString())
            adapter = PreviewTrackAdapter(
                draftTracksData,
                projectData.name,
                this@ProjectDetailActivity
            )
            recyclerView.adapter = adapter

            for (i in draftTracks.indices) {
                val data = TrackChannelStatus(
                    trackId = draftTracksData[i].tracksId!!,
                    muted = draftTracksData[i].muted,
                    preset = draftTracksData[i].preset,
                    trackType = draftTracksData[i].type,
                    trackSequence = draftTracks[i]
                )
                presetState.add(data)
            }
            adapter.notifyDataSetChanged()
            Log.d("load state", presetState.toString())
        }
    }

    override fun whenSwitchUpdate(trackId: Int, muted: Boolean) {
        val index = presetState.indexOfFirst {
            it.trackId == trackId
        }
        presetState[index].muted = muted
        Log.d("state", presetState.toString())
    }

    override fun whenPresetUpdate(trackId: Int, preset: GeneralMidiPreset) {
        val index = presetState.indexOfFirst {
            it.trackId == trackId
        }
        presetState[index].preset = preset
        Log.d("state", presetState.toString())
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

        val projectDir = File("${getExternalFilesDir(null)}/${projectData.name}/")
        if (projectDir.deleteRecursively())
            Toast.makeText(this, "Project \"${projectData.name}\" deleted!", Toast.LENGTH_SHORT)
                .show()
        runBlocking {
            projectDatabase.projectDAO().deleteProject(projectData)
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
                    projectDatabase.drafttracksDAO().loadTrackNames(projectData.projectId!!)
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
        CoroutineScope(Dispatchers.IO).launch {
            projectDatabase.drafttracksDAO().changeData(trackData)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun spawnDialogDeleteTrack(trackViewId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->

        })
        if (adapter.itemCount == 1) {
            builder.apply {
                setMessage("There is only one track left.\nAre you sure want to delete this project?")
                setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    deleteProject()
                })
            }
        } else {
            builder.apply {
                setMessage("Are you sure want to delete a track?")
                setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    deleteTrack(trackViewId)
                })
            }
        }
        builder.create().show()
    }


    private fun deleteTrack(trackViewId: Int) {
        val trackData = adapter.getDatasetPosition(trackViewId)

        val index = presetState.indexOfFirst {
            it.trackId == trackData.tracksId
        }
        val jsonFile =
            File("${getExternalFilesDir(null)}/${projectData.name}/${trackData.tracksId}.json")
        if (jsonFile.exists() && jsonFile.delete()) {
            Toast.makeText(this, "Track \"${projectData.name}\" deleted!", Toast.LENGTH_SHORT)
                .show()
        }
        runBlocking {
            projectDatabase.drafttracksDAO().deleteTrack(trackData)
        }
        adapter.removeAt(trackViewId)
        presetState.removeAt(index)
    }


    override fun onPause() {
        super.onPause()

        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()
        recyclerView.adapter = null
        CoroutineScope(Dispatchers.IO).launch {
            presetState.forEach {
                val index = draftTracksData.indexOfFirst { it1 ->
                    it1.tracksId == it.trackId
                }
                draftTracksData[index].apply {
                    preset = it.preset
                    muted = it.muted
                    projectDatabase.drafttracksDAO().changeData(this)
                }
            }
        }


    }


}