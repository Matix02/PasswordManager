package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.KeyboardViewBinding
import com.example.passwordmanager.extension.inflater

class KeyboardView(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context, attrs) {

    var onKeyPress: (KeyboardKey) -> Unit = {}

    private val binding = KeyboardViewBinding.inflate(inflater(), this)
    private val keys: List<KeyboardKey> = listOf(
        KeyboardKey("1"),
        KeyboardKey("2"),
        KeyboardKey("3"),
        KeyboardKey("4"),
        KeyboardKey("5"),
        KeyboardKey("6"),
        KeyboardKey("7"),
        KeyboardKey("8"),
        KeyboardKey("9"),
        KeyboardKey("", KeyType.EMPTY),
        KeyboardKey("0"),
        KeyboardKey("DEL", KeyType.DELETE)
    )

    init {
        orientation = VERTICAL
        val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply { weight = 1.0f }
        keys.forEachIndexed { index, key ->
            val keyView = createKeyView(key)
            when (index) {
                in 0..2 -> binding.firstRowLayout.addView(keyView, layoutParams)
                in 3..5 -> binding.secondRowLayout.addView(keyView, layoutParams)
                in 6..8 -> binding.thirdRowLayout.addView(keyView, layoutParams)
                in 9..11 -> binding.fourthRowLayout.addView(keyView, layoutParams)
            }
        }
    }

    private fun createKeyView(key: KeyboardKey): View {
        return if (key.keyType == KeyType.EMPTY) {
            createBlankButton()
        } else {
            createNumberButton(key)
        }
    }

    private fun createBlankButton(): View {
        return Button(context).apply {
            gravity = Gravity.CENTER
            isClickable = false
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    private fun createNumberButton(key: KeyboardKey): View {
        return Button(context).apply {
            text = key.keyValue
            textSize = 24f
            gravity = Gravity.CENTER
            setOnClickListener { onKeyPress(key) }
        }
    }

}

data class KeyboardKey(
    val keyValue: String,
    val keyType: KeyType = KeyType.NUMBER
)

enum class KeyType {
    NUMBER,
    DELETE,
    EMPTY;

    fun isDeleteKeyType(): Boolean = this == DELETE
}