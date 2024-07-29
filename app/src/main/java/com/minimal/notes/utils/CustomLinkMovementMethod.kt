package com.minimal.notes.utils


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.minimal.notes.R


class CustomLinkMovementMethod(private val context: Context) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(
                off, off,
                ClickableSpan::class.java
            )
            if (link.isNotEmpty()) {
                // Show popup menu
                showPopupMenu(widget, link[0], x, y)
                return true
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    private fun showPopupMenu(view: View, clickableSpan: ClickableSpan, x: Int, y: Int) {
        // Inflate the popup layout
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_menu, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, true
        )

        // Set up the menu item click listeners
        popupView.findViewById<View>(R.id.action_open).setOnClickListener { v: View? ->
            clickableSpan.onClick(view)
            popupWindow.dismiss()
        }

        popupView.findViewById<View>(R.id.action_copy).setOnClickListener { v: View? ->
            copyLinkToClipboard(
                (view as TextView).text
                    .toString()
            )
            popupWindow.dismiss()
        }

        // Calculate the position
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val screenX = location[0] + x
        val screenY = location[1] + y

        // Show the popup window
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, screenX, screenY - popupWindow.height)
    }

    private fun copyLinkToClipboard(link: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Link", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}