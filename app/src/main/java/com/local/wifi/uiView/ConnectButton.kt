package com.local.wifi.uiView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.CycleInterpolator
import androidx.core.content.ContextCompat
import com.local.wifi.R

sealed class ConnectButtonStates
{
    object DISCONNECTED : ConnectButtonStates()
    object CONNECTED : ConnectButtonStates()
    object CONNECTING : ConnectButtonStates()
}

class ConnectButton(context: Context, attributeSet: AttributeSet) : View(context, attributeSet),
    View.OnTouchListener {
    var size = 0
    var radius = 0f
    var mWidth: Float = 0f
    var mHeight: Float = 0f
    var animator: ValueAnimator? = null
    var currenColor = DEFAULT_DISCONNECTED_COLOR
    var connectedColor = DEFAULT_CONNECTED_COLOR
    var connectingColor = DEFAULT_CONNECTING_COLOR
    var disconnectedColor = DEFAULT_DISCONNECTED_COLOR

    private val paint = Paint()

    object DISCONNECTED
    object CONNECTED
    object CONNECTING


//    enum class STATES {
//        DISCONNECTED,
//        CONNECTED,
//        CONNECTING
//    }

    companion object {
        private var DEFAULT_CONNECTED_COLOR = Color.parseColor("#00FF00")
        private var DEFAULT_CONNECTING_COLOR = Color.parseColor("#00bfa5")
        private var DEFAULT_DISCONNECTED_COLOR = Color.parseColor("#FF0000")


    }

    init {
        paint.isAntiAlias = true
        setOnTouchListener(this)
        setupAttributes(attributeSet)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL
        paint.color = currenColor
        canvas.drawCircle(width / 2f, size / 2f, radius, paint)


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            BitmapFactory.decodeResource(context.resources, drawableId)
        } else if (drawable is VectorDrawable) {
            getBitmap(drawable)
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ConnectButton, 0, 0)
        connectedColor =
            typedArray.getColor(R.styleable.ConnectButton_connectedColor, DEFAULT_CONNECTED_COLOR)
        connectingColor = typedArray.getColor(
            R.styleable.ConnectButton_connectingColor,
            DEFAULT_CONNECTING_COLOR
        )
        disconnectedColor =
            typedArray.getColor(
                R.styleable.ConnectButton_disconnectedColor,
                DEFAULT_DISCONNECTED_COLOR
            )
        radius = typedArray.getFloat(
            R.styleable.ConnectButton_radius,
            width.coerceAtMost(height).toFloat()
        ) * resources.displayMetrics.density

        typedArray.recycle()

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//
//                if (state == STATES.DISCONNECTED) {
//
//                    animator = ValueAnimator.ofArgb(connectedColor, connectingColor).apply {
//                        duration = 400
//                        repeatCount = ValueAnimator.INFINITE
//                        interpolator = CycleInterpolator(0.5f)
//
//                        addUpdateListener {
//                            currenColor = it.animatedValue as Int
//                            invalidate()
//                        }
//                        addListener(object : AnimatorListenerAdapter() {
//                            override fun onAnimationEnd(animation: Animator) {
//
//                                // done
//                                //your stuff goes here
//                            }
//                        })
//                        start()
//                    }
//                    state = STATES.CONNECTED
//                } else if (state == STATES.CONNECTED) {
//                    animator?.cancel()
//                    state = STATES.DISCONNECTED
//                    currenColor = DEFAULT_DISCONNECTED_COLOR
//                    invalidate()
//                }
//                return true
//            }
//        }
        return false
    }

    fun setStatus(state: ConnectButtonStates) {
        if (state == ConnectButtonStates.DISCONNECTED) {
            Log.e(this.toString(),"try to start DISCONNECTED animation")
            animator?.cancel()
            currenColor = disconnectedColor
            invalidate()
        }
        if (state == ConnectButtonStates.CONNECTING) {
            Log.e(this.toString(),"try to start CONNECTING animation")
            animator?.cancel()
            animator = ValueAnimator.ofArgb(connectedColor, disconnectedColor).apply {
                duration = 400
                repeatCount = ValueAnimator.INFINITE
                interpolator = CycleInterpolator(0.5f)

                addUpdateListener {
                    currenColor = it.animatedValue as Int
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                    }
                })
                start()
            }
        }
        if (state == ConnectButtonStates.CONNECTED) {
            Log.e(this.toString(),"try to start CONNECTED animation")
            animator?.cancel()
            currenColor = connectedColor
            invalidate()
        }
    }
}