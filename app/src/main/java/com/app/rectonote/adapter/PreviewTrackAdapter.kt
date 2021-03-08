package com.app.rectonote.adapter

import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.R
import com.app.rectonote.database.DraftTrackEntity
import com.app.rectonote.midiplayback.DraftTrackJsonParser.Companion.draftTrackChordJsonConverter
import com.app.rectonote.midiplayback.DraftTrackJsonParser.Companion.draftTrackNoteJsonConverter
import com.app.rectonote.midiplayback.GeneralMidiPreset
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.musictheory.Chord
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.Note
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PreviewTrackAdapter(
    private val draftTracksList: MutableList<DraftTrackEntity>,
    private val projectName: String,
    private val statusCallback: ChannelStatusCallback
) : RecyclerView.Adapter<PreviewTrackAdapter.TracksViewHolder>() {
    private val audioOutScope = CoroutineScope(Dispatchers.IO)

    companion object

    var startPlaymulti = false

    interface ChannelStatusCallback {
        fun whenSwitchUpdate(trackId: Int, muted: Boolean)
        fun whenPresetUpdate(trackId: Int, preset: GeneralMidiPreset)
    }




    inner class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        val context: Context = itemView.context
        val trackName = itemView.findViewById<TextView>(R.id.track_name)
        val trackType = itemView.findViewById<ImageView>(R.id.track_type)
        val soloTrack = itemView.findViewById<ImageView>(R.id.preview_solo_button)
        val muteButton = itemView.findViewById<SwitchCompat>(R.id.mute_switch)
        val presetSelect = itemView.findViewById<ImageButton>(R.id.preset_button)
        private val trackCard = itemView.findViewById<CardView>(R.id.track_card)
        lateinit var testChannel: MIDIPlayerChannel
        init {
            trackCard.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(this.adapterPosition, 1, 0, "Edit name")
            menu?.add(this.adapterPosition, 2, 1, "Delete")
            //return this.adapterPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track_playback, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = draftTracksList[position]
        var isPlaying = false
        val draftTrackString =
            File("${holder.context.getExternalFilesDir(null)}/$projectName/${track.tracksId}.json").readText()
        val trackSequence = ArrayList<Note>()
        holder.muteButton.isChecked = !track.muted
        CoroutineScope(Dispatchers.Default).launch {
            val trackList = when (track.type.toLowerCase(Locale.ROOT)) {
                "melody" -> Klaxon().converter(draftTrackNoteJsonConverter)
                    .parseArray<Note>(draftTrackString)
                "chord" -> Klaxon().converter(draftTrackChordJsonConverter)
                    .parseArray<Chord>(draftTrackString)
                else -> listOf()
            }
            withContext(Dispatchers.Main) {
                if (trackList != null)
                    trackSequence.addAll(trackList)
            }
        }
        holder.testChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = track.key,
                tempo = track.tempo,
                trackType = track.type.toLowerCase(Locale.ROOT),
                trackSequence = trackSequence
            )
        ).apply {
            nativeLoadSoundfont("${holder.context.filesDir}/tmp_sndfnt.sf2")
        }
        val popupMenu = PopupMenu(holder.context, holder.presetSelect)
        popupMenu.menuInflater.inflate(R.menu.menu_preset, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            Log.d("PresetSelect", "${it.itemId}")
            when (it.title.toString().toLowerCase(Locale.ROOT)) {
                "piano" -> {
                    holder.presetSelect.setImageResource(R.drawable.ic_baseline_piano_28)
                    holder.testChannel.nativeLoadPreset(0, 0, 0)
                    track.tracksId?.let { it1 ->
                        statusCallback.whenPresetUpdate(
                            it1,
                            GeneralMidiPreset.piano
                        )
                    }
                }
                "guitar" -> {
                    holder.presetSelect.setImageResource(R.drawable.ic_guitar)
                    holder.testChannel.nativeLoadPreset(0, 0, 24)
                    track.tracksId?.let { it1 ->
                        statusCallback.whenPresetUpdate(
                            it1,
                            GeneralMidiPreset.guitar
                        )
                    }
                }
                "violin" -> {
                    holder.presetSelect.setImageResource(R.drawable.ic_violin)
                    holder.testChannel.nativeLoadPreset(0, 0, 40)
                    track.tracksId?.let { it1 ->
                        statusCallback.whenPresetUpdate(
                            it1,
                            GeneralMidiPreset.violin
                        )
                    }
                }
                "bass" -> {
                    holder.presetSelect.setImageResource(R.drawable.ic_bass__1_)
                    holder.testChannel.nativeLoadPreset(0, 0, 32)
                    track.tracksId?.let { it1 ->
                        statusCallback.whenPresetUpdate(
                            it1,
                            GeneralMidiPreset.bass
                        )
                    }
                }
                else -> holder.presetSelect.setImageResource(R.drawable.ic_baseline_piano_28)
            }
            true
        }
        holder.presetSelect.setOnClickListener {
            popupMenu.show()
        }
        when (track.preset) {

            GeneralMidiPreset.piano -> {
                //track.tracksId?.let { statusCallback.whenPresetUpdate(it, "piano") }
                holder.presetSelect.setImageResource(R.drawable.ic_baseline_piano_28)
                holder.testChannel.nativeLoadPreset(0, 0, 0)
            }
            GeneralMidiPreset.guitar -> {
                //track.tracksId?.let { statusCallback.whenPresetUpdate(it, "guitar") }
                holder.presetSelect.setImageResource(R.drawable.ic_guitar)
                holder.testChannel.nativeLoadPreset(0, 0, 24)
            }
            GeneralMidiPreset.violin -> {
                //track.tracksId?.let { statusCallback.whenPresetUpdate(it, "violin") }
                holder.presetSelect.setImageResource(R.drawable.ic_violin)
                holder.testChannel.nativeLoadPreset(0, 0, 40)
            }
            GeneralMidiPreset.bass -> {
                //track.tracksId?.let { statusCallback.whenPresetUpdate(it, "bass") }
                holder.presetSelect.setImageResource(R.drawable.ic_bass__1_)
                holder.testChannel.nativeLoadPreset(0, 0, 32)
            }

        }

        holder.muteButton.setOnCheckedChangeListener { _, isChecked ->
            track.tracksId?.let { statusCallback.whenSwitchUpdate(it, !isChecked) }
        }
        holder.trackName.text = track.name
        holder.trackType.setImageResource(
            when (track.type.toLowerCase(Locale.ROOT)) {
                "melody" -> R.drawable.ic_baseline_music_note_24
                "chord" -> R.drawable.ic_baseline_queue_music_24
                else -> R.drawable.ic_baseline_priority_high_24
            }
        )



        holder.soloTrack.setOnClickListener {
            //init

            if (!isPlaying) {
                holder.soloTrack.setImageResource(R.drawable.ic_outline_stop_circle_24)
                audioOutScope.launch {
                    holder.testChannel.playDraftTrackSequence()
                    withContext(Dispatchers.Main) {

                        isPlaying = false
                        holder.soloTrack.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                }
                isPlaying = true


            } else {
                audioOutScope.launch {
                    holder.testChannel.stopMessage()
                    Log.i("stop", Thread.currentThread().name)
                }
                isPlaying = false
                holder.soloTrack.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
            }
        }
    }


    override fun getItemCount(): Int = draftTracksList.size

    fun getDatasetPosition(position: Int) = draftTracksList[position]

    fun removeAt(position: Int) {
        draftTracksList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onViewDetachedFromWindow(holder: TracksViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("isDetached", "555")
        holder.testChannel.nativeRemovePlayer()
    }


}