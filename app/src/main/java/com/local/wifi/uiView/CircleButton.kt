package com.local.wifi.uiView

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.local.wifi.R
import kotlin.math.asin

class CircleButton(context: Context, attributeSet: AttributeSet) : View(context, attributeSet),
    View.OnTouchListener {

    interface OnButtonClickListener {
        fun leftButtonPressed()
        fun centerButtonPressed()
        fun rightButtonPressed()
    }

    var listener : OnButtonClickListener? = null
    private val paint = Paint()
    private var buttonLeftText: String = DEFAULT_TEXT
    private var buttonCenterText: String = DEFAULT_TEXT
    private var buttonRightText: String = DEFAULT_TEXT
    private var color = DEFAULT_COLOR
    private var pressedColor = DEFAULT_PRESSED_COLOR
    private var borderColor = DEFAULT_BORDER_COLOR
    private var size = 0
    private var radius: Float = 0f
    private var centerY: Float = 0f
    private var currentCenterButtonColor = DEFAULT_COLOR
    private var currentLeftButtonColor = DEFAULT_COLOR
    private var currentRightButtonColor = DEFAULT_COLOR
    private var textDraw = TextDraw()

    init {
        paint.isAntiAlias = true
        setOnTouchListener(this)
        setupAttributes(attributeSet)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerY = w / 2f
    }

    companion object {
        private var DEFAULT_TEXT: String = ""
        private var DEFAULT_COLOR = Color.parseColor("#272727")
        private var DEFAULT_PRESSED_COLOR = Color.parseColor("#00bfa5")
        private var DEFAULT_BORDER_COLOR = Color.parseColor("#00bfa5")

    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CenterButton, 0, 0)

        buttonLeftText = typedArray.getString(R.styleable.CenterButton_leftText) ?: DEFAULT_TEXT
        buttonCenterText = typedArray.getString(R.styleable.CenterButton_centerText) ?: DEFAULT_TEXT
        buttonRightText = typedArray.getString(R.styleable.CenterButton_rightText) ?: DEFAULT_TEXT

        color = typedArray.getColor(R.styleable.CenterButton_color, DEFAULT_COLOR)
        currentLeftButtonColor = color
        currentCenterButtonColor = color
        currentRightButtonColor = color
        pressedColor =
            typedArray.getColor(R.styleable.CenterButton_pressedColor, DEFAULT_PRESSED_COLOR)
        borderColor =
            typedArray.getColor(R.styleable.CenterButton_borderColor, DEFAULT_BORDER_COLOR)

        typedArray.recycle()

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        radius = Math.min(width, height) / 2f

        paint.style = Paint.Style.FILL

        val borderWidth = 4.0f * resources.displayMetrics.density
        paint.color = borderColor
        canvas.drawCircle(width / 2f, size / 2f, radius, paint)


        paint.color = currentCenterButtonColor
        canvas.drawCircle(width / 2f, size / 2f, radius - borderWidth, paint)


        paint.color = Color.WHITE
        var textAreaRadius = (radius.toInt() * 2 * 0.9).toInt()
        var rect = Rect(0, 0, textAreaRadius, textAreaRadius)
        textDraw.drawCenter(canvas, width / 2f, height / 2f, rect, paint, buttonCenterText)


        paint.strokeWidth = borderWidth
        paint.style = Paint.Style.FILL

        var requiredHeight = height / 2f * 0.7f
        drawLeftButton(canvas, 4f, borderColor, requiredHeight)
        requiredHeight = height / 2f * 0.65f
        drawLeftButton(canvas, 3.3f, currentLeftButtonColor, requiredHeight)

        textAreaRadius = ((width / 2 - (radius + radius / 4).toInt()) * 0.9).toInt()
        rect = Rect(0, 0, textAreaRadius, textAreaRadius)

        paint.color = Color.WHITE
        textDraw.drawCenter(
            canvas,
            textAreaRadius / 2f + textAreaRadius * 0.05f,
            height / 2f,
            rect,
            paint,
            buttonLeftText
        )


        requiredHeight = height / 2f * 0.7f
        drawRightButton(canvas, 4f, borderColor, requiredHeight)
        requiredHeight = height / 2f * 0.65f
        drawRightButton(canvas, 3.3f, currentRightButtonColor, requiredHeight)

        paint.color = Color.WHITE
        textDraw.drawCenter(
            canvas,
            width - (textAreaRadius / 2f + textAreaRadius * 0.05f),
            height / 2f,
            rect,
            paint,
            buttonRightText
        )

    }

    private fun drawLeftButton(
        canvas: Canvas,
        circlesOffset: Float,
        color: Int,
        requiredHeight: Float
    ) {
        val arcRadius = radius + radius / circlesOffset
        var arcHeight: Float = requiredHeight //height / 2f * 0.5f
        if (arcHeight > arcRadius) arcHeight = arcRadius
        val aSin = asin(arcHeight / arcRadius)
        val startD = Math.toDegrees(aSin.toDouble())
        val arc = RectF(
            width / 2 - arcRadius,
            height / 2f - arcRadius,
            width / 2 + arcRadius,
            height / 2f + arcRadius
        )
        val path = Path();

        path.reset()
        path.addArc(arc, 180f - startD.toFloat(), startD.toFloat() * 2)
        path.lineTo(0f, height / 2f - arcHeight.toFloat())
        path.lineTo(0f, height / 2f + arcHeight.toFloat())

        paint.style = Paint.Style.FILL
        paint.color = color
        canvas.drawPath(path, paint)
    }

    private fun drawRightButton(
        canvas: Canvas,
        circlesOffset: Float,
        color: Int,
        requaredHeight: Float
    ) {
        val arcRadius = radius + radius / circlesOffset
        var arcHeight: Float = requaredHeight //height / 2f * 0.5f
        if (arcHeight > arcRadius) arcHeight = arcRadius
        val aSin = asin(arcHeight / arcRadius)
        val startD = Math.toDegrees(aSin.toDouble())
        val arc = RectF(
            width / 2 - arcRadius,
            height / 2f - arcRadius,
            width / 2 + arcRadius,
            height / 2f + arcRadius
        )
        val path = Path();

        path.reset()
        path.addArc(arc, -startD.toFloat(), startD.toFloat() * 2)
        path.lineTo(width.toFloat(), height / 2f + arcHeight.toFloat())
        path.lineTo(width.toFloat(), height / 2f - arcHeight.toFloat())

        paint.style = Paint.Style.FILL
        paint.color = color
        canvas.drawPath(path, paint)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x > width / 2 - radius && event.x < width / 2 + radius) {
                    ValueAnimator.ofArgb(pressedColor, color).apply {
                        duration = 300
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            currentCenterButtonColor = animatedValue as Int
                            invalidate()
                        }
                    }.start()
                    listener?.centerButtonPressed()
                }
                if (event.x < width / 2 - radius) {
                    ValueAnimator.ofArgb(pressedColor, color).apply {
                        duration = 300
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            currentLeftButtonColor = animatedValue as Int
                            invalidate()
                        }
                    }.start()
                    listener?.leftButtonPressed()
                }
                if (event.x > width / 2 + radius) {
                    ValueAnimator.ofArgb(pressedColor, color).apply {
                        duration = 300
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            currentRightButtonColor = animatedValue as Int
                            invalidate()
                        }
                    }.start()
                    listener?.rightButtonPressed()
                }
                return true
            }
        }
        return false
    }
}