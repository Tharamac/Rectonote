package com.app.rectonote.listAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.ProjectDetailActivity
import com.app.rectonote.R
import com.app.rectonote.database.DraftTrackEntity

class DraftTracksAdapter (
    private val draftTrackDataSet: List<DraftTrackEntity>
) : RecyclerView.Adapter<DraftTracksAdapter.TracksViewHolder>(){

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val trackName = itemView.findViewById<TextView>(R.id.track_name)!!
        val trackType = itemView.findViewById<ImageView>(R.id.track_type)!!
        val trackCard = itemView.findViewById<CardView>(R.id.track_card)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TracksViewHolder(view)
        //grab layout
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        var track = draftTrackDataSet[position]
        holder.trackName.text = track.name
        holder.trackType.setImageResource(
            if (track.type == "Melody" || track.type == "melody"){
                R.drawable.ic_baseline_music_note_24
            }else if (track.type == "Melody" || track.type == "melody"){
                R.drawable.ic_baseline_queue_music_24
            }else{
                R.drawable.ic_baseline_priority_high_24
            }
        )
        // holder.projectKey.text = project.key
        holder.trackCard.setOnClickListener{ _ ->

        }
    }

    override fun getItemCount() = draftTrackDataSet.size
}
