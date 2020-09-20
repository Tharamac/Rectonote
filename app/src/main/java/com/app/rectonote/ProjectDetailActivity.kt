package com.app.rectonote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.app.rectonote.adapter.ProjectDetailTabAdapter
import com.app.rectonote.database.ProjectEntity
import com.app.rectonote.database.ProjectsDatabase
import com.app.rectonote.fragment.ProjectDataFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking


class ProjectDetailActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private val titles = arrayOf("DETAIL", "PREVIEW")
    lateinit var fragment: ProjectDataFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_project_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


//        fragment = ProjectDataFragment()
//        transaction = supportFragmentManager.beginTransaction()
//        transaction.add(R.id.project_detail,fragment)
//        transaction.commit()

    }

    override fun onResume() {
        super.onResume()
        val projectData = intent.getSerializableExtra("project") as ProjectEntity?
        viewPager2 = findViewById(R.id.detail_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        if (projectData != null) {
            val toolbarTitle = findViewById<TextView>(R.id.project_detail_title)
            toolbarTitle.text = projectData.name
            val adapter = ProjectDetailTabAdapter(this, projectData)
            viewPager2.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        TabLayoutMediator(
            tabLayout, viewPager2
        ) { tab, position -> tab.text = titles[position] }.attach()
        for (i: Int in 0..tabLayout.tabCount) {
            val tv = LayoutInflater.from(this).inflate(R.layout.custom_text, null)
            tabLayout.getTabAt(i)?.customView = tv
        }

    }

    //toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_project_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_change_project -> {
            true
        }
        R.id.action_delete_project -> {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("Are you want delete this project?")
                setPositiveButton("Yes") { _, _ ->
                    deleteProject()
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            val dialog = builder.create()
            dialog.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteProject() {
        val projectDatabase = ProjectsDatabase.getInstance(applicationContext)
        val projectData = intent.getSerializableExtra("project") as? ProjectEntity
        if (projectData != null) {
            runBlocking {
                projectDatabase.projectDAO().deleteProject(projectData)
            }
        }
        finish()
    }

}