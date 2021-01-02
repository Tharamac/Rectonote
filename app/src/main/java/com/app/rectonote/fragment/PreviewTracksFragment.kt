package com.app.rectonote.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.R
import com.app.rectonote.adapter.PreviewTrackAdapter
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.Key
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch.*
import com.app.rectonote.soundbank.MidiTest
import com.leff.midi.MidiFile
import kotlinx.coroutines.*
import java.io.File


class PreviewTracksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var projectData: ProjectEntity
    private var recyclerView: RecyclerView? = null
    lateinit var projectDatabase: ProjectsDatabase
    lateinit var adapter: PreviewTrackAdapter
    private var isPlaying: Boolean = false
    private external fun fluidSynthTest(
        tempSoundFontPath: String,
        note1: Int,
        note2: Int,
        note3: Int
    )

    private external fun play(
        tempSoundFontPath: String,
        bankNumber: Int,
        presetNumber: Int,
        midiPath: String

    )

    private external fun playSingleTrack(
        tempSoundFontPath: String,
        bankNumber: Int,
        presetNumber: Int,
        channel: Int,
        noteNumber: Int,
        velocity: Int,
        duration: Long

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectData = it.getSerializable("THIS_PROJECT") as ProjectEntity
        }
        projectDatabase = activity?.applicationContext?.let { ProjectsDatabase.getInstance(it) }!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_tracks, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<RecyclerView>(R.id.preview_track_list)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
        }
        val audioOutScope = CoroutineScope(Dispatchers.IO)
        val midiTest = MidiTest()
        val midiPath = "${activity?.filesDir}"
        val midiFile = File("$midiPath/tmp_Araiwa.mid")
        val midi = MidiFile(midiFile)
        val brightMelodyChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = Key.Gb,
                tempo = 45,
                trackType = "melody",
                trackSequence = arrayListOf(
                    Note(Gb, 4).apply { duration = 4 },
                    Note(Gb, 4).apply { duration = 2 },
                    Note(Ab, 4).apply { duration = 2 },
                    Note(Bb, 4).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(Bb, 4).apply { duration = 2 },
                    Note(Db, 5).apply { duration = 2 },
                    Note(Eb, 5).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(Eb, 5).apply { duration = 2 },
                    Note(Db, 5).apply { duration = 2 },
                    Note(Bb, 4).apply { duration = 2 },
                    Note.restNote().apply { duration = 2 },
                    Note(Bb, 4).apply { duration = 1 },
                    Note(Db, 5).apply { duration = 3 },
                )
            )
        )
        val brightChordChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = Key.Gb,
                tempo = 45,
                trackType = "chord",
                trackSequence = arrayListOf(
                    Chord(Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(Gb, 4).apply { chordType = "major"; duration = 8 },
                    Chord(Gb, 4).apply { chordType = "major"; duration = 8 },


                    )
            )
        )
        val tempSoundfontPath: String = "${activity?.filesDir}/tmp_sndfnt.sf2"
        brightChordChannel.nativeLoadSoundfont(tempSoundfontPath)
        brightMelodyChannel.nativeLoadSoundfont(tempSoundfontPath)
        brightChordChannel.nativeLoadPreset(0, 0, 0)
        brightMelodyChannel.nativeLoadPreset(0, 0, 35)
        midiTest.midi.writeToFile(midiFile)
        val playMultiTrackButton = view.findViewById<Button>(R.id.play_selected_button)
        playMultiTrackButton.setOnClickListener {
            if (!isPlaying) {
                playMultiTrackButton.text = "Stop"
                audioOutScope.launch {
                    brightChordChannel.playDraftTrackSequence()
                }
                audioOutScope.launch {
                    brightMelodyChannel.playDraftTrackSequence()
                    withContext(Dispatchers.Main) {
                        isPlaying = false
                        playMultiTrackButton.text = "Play Selected"
                    }
                }
                isPlaying = true


            } else {
                playMultiTrackButton.text = "Play Selected"
                audioOutScope.launch {
                    brightChordChannel.stopMessage()
                }
                audioOutScope.launch {
                    brightMelodyChannel.stopMessage()
                }

                isPlaying = false
            }


        }


        /*midiProcessor.registerEventListener(object: MidiEventListener{
            override fun onStart(fromBeginning: Boolean) {

            }

            override fun onEvent(event: MidiEvent?, ms: Long) {
                if(event is NoteOn ){
                    playSingleTrack(tempSoundfontPath,0,42,0,event.noteValue,event.velocity,1)
                }
                if(eve)

            }

            override fun onStop(finished: Boolean) {

            }

        }

        , MidiEvent::class.java)
        midiProcessor.start()*/


        //audioOutScope.launch {
        //     play(tempSoundfontPath,0,42,"$midiPath/tmp_Araiwa.mid")

        //  }
        //    audioOutScope.launch {
        //         play(tempSoundfontPath,0,42,"$midiPath/test.mid")
        // }
        /*
        for (i in 4..6 ){
            audioOutScope.launch {

                fluidSynthTest(tempSoundfontPath, 12*i, 12*i+2,12*i+4)
            }
        }*/


    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            projectData.projectId.let {
                if (it != null) {
                    adapter = PreviewTrackAdapter(
                        projectDatabase.drafttracksDAO().loadTracksFromProject(it)
                    )
                }
            }
            recyclerView?.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(projectData: ProjectEntity) =
            PreviewTracksFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("THIS_PROJECT", projectData)
                }
            }
    }

}
