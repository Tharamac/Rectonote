package com.app.rectonote.adapter

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.R
import com.app.rectonote.database.DraftTrackEntity
import java.util.*

class PreviewTrackAdapter(
    private val draftTracksList: List<DraftTrackEntity>
) : RecyclerView.Adapter<PreviewTrackAdapter.TracksViewHolder>() {
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
        holder.trackName.text = track.name
        holder.trackType.setImageResource(
            when (track.type.toLowerCase(Locale.ROOT)) {
                "melody" -> R.drawable.ic_baseline_music_note_24
                "chord" -> R.drawable.ic_baseline_queue_music_24
                else -> R.drawable.ic_baseline_priority_high_24
            }
        )
    }

    override fun getItemCount(): Int = draftTracksList.size

}