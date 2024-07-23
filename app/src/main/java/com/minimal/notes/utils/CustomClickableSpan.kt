package com.minimal.notes.utils

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast

class CustomClickableSpan(private val context: Context, private val url: String) : ClickableSpan() {

    override fun onClick(widget: View) {
        // Handle the link click, for example by showing a toast
        Toast.makeText(context, "Clicked link: $url", Toast.LENGTH_SHORT).show()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = ds.linkColor // Set link color
        ds.isUnderlineText = true // Optionally underline the text
    }
}