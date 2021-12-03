package com.example.synclient.arWidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import java.lang.Integer.min
import kotlin.properties.Delegates

private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    textSize = 55.0f
    typeface = Typeface.create("", Typeface.BOLD)
    color = Color.RED
}

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var radius by Delegates.notNull<Float>()

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRGB(255, 255, 255)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
        canvas.drawText(
            "textValueMine",
            0.0F, 0.0F, paint
        )
    }


}