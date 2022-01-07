package com.osisupermoses.quizapp

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MyFont(
    context: Context,
    attributeSet: AttributeSet
): AppCompatTextView(context, attributeSet) {

    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(
            context.assets, "GreatVibes-Regular.ttf")
        setTypeface(typeface)
    }
}