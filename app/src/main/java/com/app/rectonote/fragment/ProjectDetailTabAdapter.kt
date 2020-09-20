package com.app.rectonote.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rectonote.database.ProjectEntity

class ProjectDetailTabAdapter(fragmentActivity: FragmentActivity, project: ProjectEntity) :
    FragmentStateAdapter(fragmentActivity) {
    private val project = project
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ProjectDataFragment.newInstance(project)
        1 -> PreviewTracksFragment()
        else -> ProjectDataFragment.newInstance(project)
    }
}