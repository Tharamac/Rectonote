package com.app.rectonote.listAdapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.EditText
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

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener{
        val context: Context = itemView.context
        val trackName = itemView.findViewById<TextView>(R.id.track_name)!!
        val trackType = itemView.findViewById<ImageView>(R.id.track_type)!!
        private val trackCard = itemView.findViewById<CardView>(R.id.track_card)!!
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
        }

//        private val onTrackMenu = MenuItem.OnMenuItemClickListener{
//            when (itemId){
//                1 as Long -> {
//                    val builder = AlertDialog.Builder(context)
//                    val input = EditText(context)
//                    builder.apply {
//                        setTitle("Edit Name")
//                        setView(input)
//                        setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//                            val changedName =  input.text
//                        })
//                        setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
//
//                        })
//                    }
//                    builder.create().show()
//                    true
//                }
//                2 as Long -> {
//                    val builder = AlertDialog.Builder(context)
//                    builder.apply {
//                        setMessage("Are you sure want to delete a track?")
//                        setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
//
//                        })
//                        setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->
//
//                        })
//                    }
//                    builder.create().show()
//                    true
//                }
//                else -> false
//            }
//        }
    }
    fun changeName(position: Int){
        Log.d("WHERE", "my position =  $position ; data = ${draftTrackDataSet[position]}")
    }
    fun deleteTrack(position: Int){
        Log.d("WHERE", "my position =  $position ; delete data = ${draftTrackDataSet[position]}")
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
    }



    override fun getItemCount() = draftTrackDataSet.size
}
