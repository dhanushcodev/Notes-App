package com.minimal.notes.utils

import android.content.Context
import android.content.Intent

object ShareNote {
    fun share(context: Context, shareTitle:String, shareContent:String){
        val shareText = shareTitle + "\n\n" + shareContent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        val chooserIntent = Intent.createChooser(shareIntent, "Share using...")
        context.startActivity(chooserIntent)
    }
}