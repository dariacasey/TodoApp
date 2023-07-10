package com.example.todoapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import com.example.todoapp.databinding.AddTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddNewTask : BottomSheetDialogFragment() {
    private lateinit var binding: AddTaskBinding
    private lateinit var db: MyDatabase
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reads if app is in dark mode or not from preferences file
        sharedPref = requireActivity().getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        val nightMode = sharedPref.getBoolean("darkMode", false)
        // Puts app in correct mode
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // inflates layout
        return inflater.inflate(R.layout.add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initializes binding and objects in fragment layout
        binding = AddTaskBinding.bind(view)
        val taskText = binding.newTaskText
        val saveButton = binding.newTaskButton
        var update = false

        db = MyDatabase(activity)
        db.openDatabase()

        // used to check if a task is being updated or created. new task -> bundle is null
        val args: Bundle? = arguments
        // If task is being updated, input is updated in task and text is set
        if (args != null) {
            update = true
            val task: String = args.getString("task").toString()
            taskText.setText(task)
        }

        // When save button is clicked, new task is created with information input into fragment
        // and added to database
        saveButton.setOnClickListener {
            val text= taskText.text.toString()
            if (update) {
                db.updateTask(args!!.getInt("id"), text)
            } else {
                val task = Task()
                task.taskInput = text
                task.status = 0
                db.insertTask(task)
            }
            // Makes fragment go away
            dismiss()
        }
    }

    // Called when fragment is dismissed
    // Notifies activity that fragment has been dismissed
    override fun onDismiss(dialog: DialogInterface) {
        val activity: Activity? = activity
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }

    // Defines tag and newInstance method (creates instance of fragment)
    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }
}