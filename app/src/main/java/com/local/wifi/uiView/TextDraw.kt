package com.local.wifi.uiView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class TextDraw {

    private fun setTextSizeForWidth(
        paint: Paint, desiredWidth: Float,
        text: String
    ) {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 48f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize = testTextSize * desiredWidth / bounds.width()
        // val desiredTextSize = testTextSize * desiredWidth / bounds.width()

        // Set the paint for that size.
        paint.textSize = desiredTextSize
    }

    fun drawCenter(canvas: Canvas, startX : Float, startY : Float, rect : Rect, paint: Paint, text: String) {
        paint.textAlign = Paint.Align.CENTER
        setTextSizeForWidth(paint,rect.width().toFloat(), text)
        //val xPos = rect.width() / 2
        //val yPos = (rect.height() / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        val xPos = startX
        val yPos = (startY- (paint.descent() + paint.ascent()) / 2)
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        canvas.drawText(text, xPos, yPos, paint)
    }
}