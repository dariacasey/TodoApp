package com.example.todoapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.fragment.app.FragmentActivity

class MyDatabase(context: FragmentActivity?) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    // Creates outline of database
    companion object {
        private const val VERSION = 1
        private const val NAME = "toDoListDatabase"
        private const val TABLE_NAME = "todo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
    }

    // Creates table
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $TASK " +
                "TEXT, $STATUS INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drops tables
        db?.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME)
        // Create new one
        onCreate(db)
    }
    // Makes DB editable when called
    fun openDatabase() {
        val db = this.writableDatabase
    }

    fun insertTask(task: Task) {
        // Stores values to be inserted
        val values = ContentValues()
        values.put(TASK, task.taskInput)
        values.put(STATUS, 0)
        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Suppresses error I kept getting that I could not fix
    @SuppressLint("Range")
    fun getAllTasks(): MutableList<Task> {
        // Creates list
        var taskList: MutableList<Task> = mutableListOf()
        val db = writableDatabase
        val cur: Cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null)
        if (cur != null) {
            // Moves cursor to first row
            if (cur.moveToFirst()) {
                do{
                    // Creates task with information found from current row
                    val task = Task()
                    task.id = cur.getInt((cur.getColumnIndex(ID)))
                    task.taskInput = cur.getString(cur.getColumnIndex(TASK))
                    task.status = cur.getInt(cur.getColumnIndex(STATUS))
                    taskList.add(task)
                    // Moves cursor to next row
                } while (cur.moveToNext())
            }
        }
        // Closes database and returns list
        db.close()
        cur.close()
        return taskList
    }


    fun updateStatus(id: Int, status: Int) {
        // Stores values to be updated
        val values = ContentValues()
        values.put(STATUS, status)
        val db = writableDatabase
        db.update(TABLE_NAME, values, "$ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun updateTask(id: Int, task: String) {
        // Stores task
        val values = ContentValues()
        values.put(TASK, task)
        val db = writableDatabase
        db.update(TABLE_NAME, values, "$ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTask(id: Int) {
        // Deletes task
        val db = writableDatabase
        db.delete(TABLE_NAME, "$ID=?", arrayOf(id.toString()))
        db.close()
    }

}