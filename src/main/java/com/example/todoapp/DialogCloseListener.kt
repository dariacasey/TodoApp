package com.example.todoapp

import android.content.DialogInterface
// Defines method that is called when AddNewTask fragment is closed
interface DialogCloseListener {

    fun handleDialogClose(dialog: DialogInterface)
}