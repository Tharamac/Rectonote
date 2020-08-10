package com.app.rectonote.listAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.R
import com.app.rectonote.database.ProjectEntity

class projectListAdapter(
    private val projectDataset : List<ProjectEntity>
) : RecyclerView.Adapter<projectListAdapter.projectViewHolder>(){

    class projectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val projectName = itemView.findViewById<TextView>(R.id.project_name)
        val projectKey = itemView.findViewById<TextView>(R.id.project_key)
        val projectTempo = itemView.findViewById<TextView>(R.id.project_tempo)
        val projectCard = itemView.findViewById<CardView>(R.id.project_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): projectViewHolder {
        val projectCard = LayoutInflater.from(parent.context).inflate(R.layout.project_item,parent,false) as CardView
        return projectViewHolder(projectCard)
        //grab layout
    }

    override fun onBindViewHolder(holder: projectViewHolder, position: Int) {
        var project = projectDataset[position]
        holder.projectName.text = project.name
        holder.projectTempo.text = "${project.tempo} bpm"
        holder.projectKey.text = project.key
        holder.projectCard.setOnClickListener{ view ->
           Log.d("projectCardResult", project.toString())
        }
    }

    override fun getItemCount() = projectDataset.size
}