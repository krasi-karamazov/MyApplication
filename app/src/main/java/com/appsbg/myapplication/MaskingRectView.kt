package com.appsbg.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.animation.Animation


class MaskingRectView constructor(ctx: Context, attr: AttributeSet) : View(ctx, attr) {

    /*
        Public properties
     */
    var frameWidth: Float = 0f
        get() = cardRectWidth
    var frameheight: Float = 0f
        get() = cardRectHeight

    var frameTop: Float = 0f
        get() = puncherTop

    var frameLeft: Float = 0f
        get() = puncherLeft

    var orientation: Int = ORIENTATION_PORTRAIT
        set(value){
            field = value
            animateRelayoutProcedure()
        }

    var animationEndListener: AnimationEndListener? = null

    companion object {
        private const val CREDIT_CARD_ASPECT_RATIO = 1.586f
        const val ORIENTATION_LANDSCAPE = 1
        const val ORIENTATION_PORTRAIT = 0
    }

    private lateinit var semiTransparentBmp: Bitmap
    private val bmpPaint: Paint = Paint().apply {
        color = Color.BLACK
        alpha = 180
    }
    private val puncherPaint = Paint().apply {
        color = Color.WHITE
    }

    private lateinit var bmpCanvas: Canvas

    private var cardRectWidth: Float = 0f
    private var cardRectHeight: Float = 0f
    private var puncherLeft: Float = 0f
    private var puncherTop: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initFrameDimensions()
        initMaskedBitmap()
    }

    private fun initFrameDimensions(){
        val screenPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f,
            context.resources.displayMetrics
        )
        if(orientation == ORIENTATION_LANDSCAPE) { //landscape
            cardRectHeight = cardRectWidth * CREDIT_CARD_ASPECT_RATIO
            cardRectWidth = measuredWidth - (screenPadding * 2)
        }else{ //portrait
            cardRectWidth = measuredWidth - (screenPadding * 2)
            cardRectHeight = cardRectWidth / CREDIT_CARD_ASPECT_RATIO
        }
    }

    private val animationListener: AnimatorListenerAdapter = object: AnimatorListenerAdapter(){
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            requestLayout()
            invalidate()
        }
    }

    private fun animateRelayoutProcedure() {
        apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(animationListener)
        }
    }

    private fun initMaskedBitmap() {
        semiTransparentBmp = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        bmpCanvas = Canvas(semiTransparentBmp)

        bmpCanvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), bmpPaint)

        puncherLeft = measuredWidth.toFloat() / 2f - (cardRectWidth / 2f)
        puncherTop = measuredHeight.toFloat() / 2f - (cardRectHeight / 2f)
        puncherPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        bmpCanvas.drawRoundRect(RectF(puncherLeft, puncherTop, puncherLeft + cardRectWidth, puncherTop + cardRectHeight), 10f, 10f,  puncherPaint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(semiTransparentBmp, 0f,0f, bmpPaint)
    }
}