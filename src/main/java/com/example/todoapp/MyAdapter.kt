package com.example.todoapp

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.TaskDesignBinding

class MyAdapter(private val activity: MainActivity) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(), View.OnCreateContextMenuListener {
    private lateinit var db: MyDatabase
    private lateinit var todoList: MutableList<Task>
    // Initializes position in recycler view
    var mPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Creates value to inflate design for tasks in recycler view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_design, parent, false)
        // On long click, context menu is created
        itemView.setOnCreateContextMenuListener(this)
        // Saves position of task clicked when user long clicks a task
        itemView.setOnLongClickListener { view ->
            mPosition = getPosition(view)
            false
        }
        // Returns inflated view
        return MyViewHolder(itemView)
    }

    // Retrieves size of recycler view
    override fun getItemCount(): Int {
        return todoList.size
    }

    // Updates view of tasks in recycler view to display correct information
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        db = MyDatabase(activity)
        db.openDatabase()

        // Retrieves task object at given position
        val item = todoList[position]

        // Makes text and checked status match item
        holder.box.text = item.taskInput
        holder.box.isChecked = item.status != 0
        holder.box.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
        }
    }

    // Updates adapter with correct task lists
    fun setTask(list: MutableList<Task>) {
        this.todoList = list
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        // Retrieves task
        val item: Task = todoList[position]
        // Deletes task from database by find ID
        db.deleteTask(item.id)
        // Removes it from list
        todoList.removeAt(position)
        // Notifies adapter that data has changed and update is needed
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        // Retrieves task
        val item: Task = todoList[position]
        // Creates instance of fragment
        val fragment: AddNewTask = AddNewTask()
        // Creates bundle to pass info to fragment
        val args: Bundle = Bundle()
        args.putInt("id", item.id)
        args.putString("task", item.taskInput)
        // Passes info to fragment
        fragment.arguments = args
        // Shows fragment to user as dialog
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflater = activity.menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        // Sets mPosition to position of task that was clicked
        mPosition = this@MyAdapter.getPosition(v!!)
    }

    // Returns position of task in recycler view
    private fun getPosition(v: View): Int {
        val holder = v.tag as RecyclerView.ViewHolder
        return holder.bindingAdapterPosition
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        private val binding = TaskDesignBinding.bind(view)
        val box = binding.todoBox

        // Initializes view tag with instance
        init {
            view.tag = this
        }

        // Here to implement all members. Empty because no menu needs to be created
        override fun onCreateContextMenu(
            p0: ContextMenu?,
            p1: View?,
            p2: ContextMenu.ContextMenuInfo?
        ) {}
    }
}