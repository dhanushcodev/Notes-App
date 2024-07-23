package com.minimal.notes.utils

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Patterns
import android.widget.EditText
import java.util.regex.Pattern


class LinkTextWatcher(private val context: Context, private val editText: EditText) : TextWatcher {

    init {
        // Apply the spans to initial text if any
        applyLinkSpans(editText.text)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // No implementation needed
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // No implementation needed
    }

    override fun afterTextChanged(s: Editable) {
        applyLinkSpans(s)
    }

    fun applyLinkSpans(s: Editable) {
        removeSpans(s, CustomClickableSpan::class.java) // Remove existing CustomClickableSpans
        val urlPattern = Patterns.WEB_URL
        val emailPattern = Patterns.EMAIL_ADDRESS
        val combinedPattern = Pattern.compile(
            "${urlPattern.pattern()}|${emailPattern.pattern()}",
            Pattern.CASE_INSENSITIVE
        )

        val matcher = combinedPattern.matcher(s)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val url = matcher.group()
            s.setSpan(CustomClickableSpan(context, url), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
//        editText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun removeSpans(s: Editable, type: Class<*>) {
        val spans = s.getSpans(0, s.length, type)
        for (span in spans) {
            s.removeSpan(span)
        }
    }
}