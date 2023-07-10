package com.example.todoapp

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.todoapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity(), DialogCloseListener, MenuItem.OnMenuItemClickListener{
    private lateinit var binding: ActivityMainBinding
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var taskAdapter: MyAdapter
    private lateinit var button: FloatingActionButton
    private lateinit var taskList: MutableList<Task>
    private lateinit var db: MyDatabase
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Accesses values saved from Settings activity to set the correct mode for activity
        sharedPref = getSharedPreferences("prefs", MODE_PRIVATE)
        val nightMode = sharedPref.getBoolean("darkMode", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(binding.root)

        // Hides the default bar that is usually at the top of apps
        supportActionBar?.hide()

        // Opens and initializes database
        db = MyDatabase(this)
        db.openDatabase()


        // Initializes and sets recycler view and adapter
        newRecyclerView = binding.taskList
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = MyAdapter(this)
        newRecyclerView.adapter = taskAdapter

        // Creates instance of the AddNewTask fragment in the activity when button is clicked
        button = binding.floatingActionButton2
        button.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        // Receives tasks from database, reverses them so they show up in order, and updates the recycler view
        taskList = db.getAllTasks()
        taskList.reverse()
        taskAdapter.setTask(taskList)

        // Takes user to Settings activity when icon is clicked
        binding.settingsIcon.setOnClickListener{
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    // Called when context menu option is selected
    override fun onContextItemSelected(item: MenuItem): Boolean {
        // When delete or edit is selected, according function is called at indicated position
        when (item.itemId) {
            R.id.delete_task -> {
                taskAdapter.deleteItem(taskAdapter.mPosition)
                return true
            }
            R.id.edit_task -> {
                taskAdapter.editItem(taskAdapter.mPosition)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    // Called when AddNewTask is closed
    override fun handleDialogClose(dialog: DialogInterface) {
        // Updates adapters data when AddNewTask fragment is closed
        taskList = db.getAllTasks()
        taskList.reverse()
        taskAdapter.setTask(taskList)
        // Ensures user can see edited/deleted data correctly in recycler view after fragment is closed
        taskAdapter.notifyDataSetChanged()
    }

    // Called when menu item is clicked
    override fun onMenuItemClick(p0: MenuItem): Boolean {
        return true
    }
}