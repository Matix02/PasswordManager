package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER_HORIZONTAL
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.PinViewBinding
import com.example.passwordmanager.extension.inflater

const val MAX_PIN_CIRCLE_COUNT = 4

class PinView(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context, attrs) {

    init {
        orientation = HORIZONTAL
        gravity = CENTER_HORIZONTAL
        addPinCircles()
    }

    private fun addPinCircles() {
        repeat(MAX_PIN_CIRCLE_COUNT) {
            addView(PinCircle(context).apply {
                setPadding(10, 0, 10, 0)
            })
        }
    }

    fun setPinCircle(pinsCount: Int) {
        clearPinCircles()
        repeat(pinsCount) {
            modifyPinCircle(pinIndex = it, operation = { fillPinCircle() })
        }
    }

    private fun clearPinCircles() {
        repeat(MAX_PIN_CIRCLE_COUNT) {
            modifyPinCircle(pinIndex = it, operation = { emptyPinCircle() })
        }
    }

    private inline fun modifyPinCircle(pinIndex: Int, operation: PinCircle.() -> Unit) {
        val pinCircle = getChildAt(pinIndex) as? PinCircle
        pinCircle?.operation()
    }

}

class PinCircle(context: Context) : FrameLayout(context) {

    private val binding = PinViewBinding.inflate(inflater(), this)

    init {
        emptyPinCircle()
    }

    fun emptyPinCircle() {
        binding.pinCircle.setImageResource(R.drawable.ic_border_circle)
    }

    fun fillPinCircle() {
        binding.pinCircle.setImageResource(R.drawable.ic_fill_circle)
    }
}