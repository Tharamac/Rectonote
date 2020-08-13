package com.app.rectonote.listAdapter

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.ProjectDetailActivity
import com.app.rectonote.R
import com.app.rectonote.database.ProjectEntity

class ProjectListAdapter(
    private val projectDataset : List<ProjectEntity>
) : RecyclerView.Adapter<ProjectListAdapter.projectViewHolder>(){

    class projectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        val projectName = itemView.findViewById<TextView>(R.id.project_name)
        val projectData = itemView.findViewById<TextView>(R.id.project_data)
        val projectCard = itemView.findViewById<CardView>(R.id.project_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): projectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item,parent,false)
        return projectViewHolder(view)
        //grab layout
    }

    override fun onBindViewHolder(holder: projectViewHolder, position: Int) {
        var project = projectDataset[position]
        holder.projectName.text = project.name
        holder.projectData.text = "${project.tempo} bpm\n${project.key}"
       // holder.projectKey.text = project.key
        holder.projectCard.setOnClickListener{ _ ->
           val intent = Intent(holder.context, ProjectDetailActivity::class.java)
            intent.putExtra("project", project)
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount() = projectDataset.size
}