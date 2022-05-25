package com.bangkit.storyapp

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEmailEditText : AppCompatEditText{
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    if (isValidEmail(p0)) showError(false)
                    else showError(true)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun isValidEmail(text: CharSequence) = android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()

    private fun showError(isError: Boolean) {
        error = if (isError) resources.getString(R.string.email_error)
        else null
    }
}