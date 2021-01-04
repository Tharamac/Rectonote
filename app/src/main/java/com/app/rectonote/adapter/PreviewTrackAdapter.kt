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
import com.app.rectonote.midiplayback.MIDIPlayerChannel
import com.app.rectonote.musictheory.DraftTrackData
import com.app.rectonote.musictheory.Key
import com.app.rectonote.musictheory.Note
import com.app.rectonote.musictheory.NotePitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PreviewTrackAdapter(
    private val draftTracksList: MutableList<DraftTrackEntity>
) : RecyclerView.Adapter<PreviewTrackAdapter.TracksViewHolder>() {
    private val audioOutScope = CoroutineScope(Dispatchers.IO)
    private var isPlaying = false

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        val context: Context = itemView.context
        val trackName = itemView.findViewById<TextView>(R.id.track_name)
        val trackType = itemView.findViewById<ImageView>(R.id.track_type)
        val soloTrack = itemView.findViewById<ImageView>(R.id.preview_solo_button)
        val muteButton = itemView.findViewById<SwitchCompat>(R.id.mute_switch)
        val presetSelect = itemView.findViewById<ImageButton>(R.id.preset_button)
        private val trackCard = itemView.findViewById<CardView>(R.id.track_card)

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
        val testChannel = MIDIPlayerChannel(
            DraftTrackData(
                key = Key.Gb,
                tempo = 128,
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
        ).apply {
            nativeLoadSoundfont("${holder.context.filesDir}/tmp_sndfnt.sf2")
        }
        holder.trackName.text = track.name
        holder.trackType.setImageResource(
            when (track.type.toLowerCase(Locale.ROOT)) {
                "melody" -> R.drawable.ic_baseline_music_note_24
                "chord" -> R.drawable.ic_baseline_queue_music_24
                else -> R.drawable.ic_baseline_priority_high_24
            }
        )
        holder.presetSelect.setOnClickListener {

            val popupMenu = PopupMenu(holder.context, holder.presetSelect)
            popupMenu.menuInflater.inflate(R.menu.menu_preset, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                Log.d("PresetSelect", "${it.title}")
                when (it.title) {
                    "Piano" -> {
                        holder.presetSelect.setImageResource(R.drawable.ic_baseline_piano_28)
                        testChannel.nativeLoadPreset(0, 0, 0)
                    }
                    "Guitar" -> {
                        holder.presetSelect.setImageResource(R.drawable.ic_guitar)
                        testChannel.nativeLoadPreset(0, 0, 24)
                    }
                    "Violin" -> {
                        holder.presetSelect.setImageResource(R.drawable.ic_violin)
                        testChannel.nativeLoadPreset(0, 0, 40)
                    }
                    "Bass" -> {
                        holder.presetSelect.setImageResource(R.drawable.ic_bass__1_)
                        testChannel.nativeLoadPreset(0, 0, 32)
                    }
                    else -> holder.presetSelect.setImageResource(R.drawable.ic_baseline_piano_28)
                }
                true
            }
            popupMenu.show()
        }
        holder.soloTrack.setOnClickListener {
            if (!isPlaying) {
                holder.soloTrack.setImageResource(R.drawable.ic_outline_stop_circle_24)
                audioOutScope.launch {
                    testChannel.playDraftTrackSequence()
                    withContext(Dispatchers.Main) {
                        isPlaying = false
                        holder.soloTrack.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                    }
                }
                isPlaying = true


            } else {
                audioOutScope.launch {
                    testChannel.stopMessage()
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

}