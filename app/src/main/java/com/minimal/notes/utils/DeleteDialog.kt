package com.minimal.notes.utils

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import com.minimal.notes.R
import android.content.Context

object DeleteDialog {
     fun showDialog(context:Context,onDeleteClick:()->Unit,onCancelClick:()->Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.custom_dialog_layout, null)

        builder.setView(customView)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        val cancelButton = customView.findViewById<Button>(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            onCancelClick()
            dialog.dismiss()
        }
        val deleteButton = customView.findViewById<Button>(R.id.delete_btn)
        deleteButton.setOnClickListener {
            onDeleteClick()
            dialog.dismiss()
        }
    }
}