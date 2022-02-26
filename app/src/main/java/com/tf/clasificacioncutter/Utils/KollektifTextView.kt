package com.tf.clasificacioncutter.Utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.tf.clasificacioncutter.R

class KollektifTextView(context: Context?, attrs: AttributeSet?) : AppCompatTextView(context!!, attrs) {
    init {
        val typeface: Typeface? = ResourcesCompat.getFont(getContext(), R.font.kollektif)
        setTypeface(typeface)
    }
}