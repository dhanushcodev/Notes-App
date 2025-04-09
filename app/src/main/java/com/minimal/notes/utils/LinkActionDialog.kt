package com.minimal.notes.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.minimal.notes.R

object LinkActionDialog {

    @SuppressLint("MissingInflatedId", "CutPasteId", "SuspiciousIndentation")
    fun showDialog(context: Context, linkTxt: String, onBtn1Click:()->Unit, onBtn2Click:()->Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.custom_link_actions_layout, null)

        builder.setView(customView)
        val dialog = builder.create()

        customView.findViewById<TextView>(R.id.dialog_title).text = linkTxt.replace("mailto:","").replace("tel:","")

            if(linkTxt.contains("mailto:")) {
                customView.findViewById<Button>(R.id.btn1).text = "Send Message"
                customView.findViewById<Button>(R.id.btn2).text = "Copy email"
            }else if(linkTxt.contains("tel:")){
                customView.findViewById<Button>(R.id.btn1).text = "Make Phone Call"
                customView.findViewById<Button>(R.id.btn2).text = "Copy"
            }else{
                customView.findViewById<Button>(R.id.btn1).text = "Open link"
                customView.findViewById<Button>(R.id.btn2).text = "Copy link"
            }



        val btn1 = customView.findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            onBtn1Click()
            dialog.dismiss()
        }
        val btn2 = customView.findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            onBtn2Click()
            dialog.dismiss()
        }
        val btn3 = customView.findViewById<Button>(R.id.btn3)
        btn3.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }
}