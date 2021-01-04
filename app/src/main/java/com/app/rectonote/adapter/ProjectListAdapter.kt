package com.app.rectonote.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.rectonote.ProjectDetailActivity
import com.app.rectonote.R
import com.app.rectonote.database.ProjectEntity

open class ProjectListAdapter(
    private var projectDataset: MutableList<ProjectEntity>
) : RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder>(), Filterable {
    internal var filterListResult: List<ProjectEntity> = projectDataset

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context!!
        val projectName = itemView.findViewById<TextView>(R.id.project_name)!!
        val projectData = itemView.findViewById<TextView>(R.id.project_data)!!
        val projectCard = itemView.findViewById<CardView>(R.id.project_card)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
        //grab layout
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        var project = projectDataset[position]
        holder.projectName.text = project.name
        holder.projectData.text = "${project.tempo} bpm\n${project.key.label}"
        // holder.projectKey.text = project.key
        holder.projectCard.setCardBackgroundColor(Color.parseColor(project.color))
        holder.projectCard.setOnClickListener { _ ->
            val intent = Intent(holder.context, ProjectDetailActivity::class.java)
            intent.putExtra("project", project)
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount() = projectDataset.size
    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            filterListResult = if (charSearch.isEmpty()) {
                projectDataset
            } else {
                val resultList = ArrayList<ProjectEntity>()
                projectDataset.forEach {
                    if (it.name.toLowerCase().contains(charSearch.toLowerCase())) resultList.add(it)
                }
                resultList
            }
            val filterResult = FilterResults()
            filterResult.values = filterListResult
            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            projectDataset = results!!.values as MutableList<ProjectEntity>

        }

    }


}