package com.aact.sat_pda.Biz

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText

class Action {
    fun doneAction(view: EditText, onClick: () -> Unit) {
        view.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onClick()
                true
            } else {
                false
            }
        }
    }
    fun slide_TransY(
        view: View,
        transY: Float,
        duration: Long,
        endAction: () -> Unit,
        startAction: () -> Unit
    ) {
        view.animate().translationY(transY).setDuration(duration).withEndAction { endAction() }
            .withStartAction { startAction() }
            .start()
    }
    fun touch_Event(
        view: View,
        actionStart: (MotionEvent, View) -> Unit,
        actionMove: (MotionEvent, View) -> Unit,
        actionEnd: (MotionEvent, View) -> Unit,
        actionCancel: (MotionEvent, View) -> Unit
    ) {

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionStart(event, v)
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    actionMove(event, v)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    actionEnd(event, v)
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    actionCancel(event, v)
                    true
                }

                else -> false
            }
        }

    }

    fun textChange_after(view: EditText,onClick: (s: Editable?) -> Unit) {

        view.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    onClick(s)
                }
            }
        )
    }
}