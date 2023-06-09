package com.example.storyapp.customview

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class InputConfirmPass: AppCompatEditText, View.OnTouchListener {

    var isConfirmPassValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                checkConfirmPass()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                checkConfirmPass()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkConfirmPass()
            }
        })
    }

    private fun checkConfirmPass() {
        val cPass = text?.trim()
        when {
            cPass.isNullOrEmpty() -> {
                isConfirmPassValid = false
                error = resources.getString(R.string.required_pass)
            }
            cPass.length < 8 -> {
                isConfirmPassValid = false
                error = resources.getString(R.string.pass_length)
            }
            else -> {
                isConfirmPassValid = true
            }
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) checkConfirmPass()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}