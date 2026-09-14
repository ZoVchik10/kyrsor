package com.example.kyrsor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ThermometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    private var progress = 0f // от 0 до 100

    fun setProgress(value: Float) {
        progress = min(100f, maxOf(0f, value))
        invalidate() // перерисовываем
    }

    init {
        backgroundPaint.color = Color.parseColor("#333333")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = height / 2

        // Фон термометра
        rectF.set(0f, 0f, width, height)
        canvas.drawRoundRect(rectF, radius, radius, backgroundPaint)

        // Градиентное заполнение
        val fillWidth = width * (progress / 100f)
        if (fillWidth > 0) {
            val gradient = LinearGradient(
                0f, 0f, fillWidth, 0f,
                intArrayOf(
                    Color.parseColor("#2196F3"), // синий - холодно
                    Color.parseColor("#FFEB3B"), // жёлтый - тепло
                    Color.parseColor("#FF9800"), // оранжевый
                    Color.parseColor("#F44336")  // красный - горячо
                ),
                floatArrayOf(0f, 0.33f, 0.66f, 1f),
                Shader.TileMode.CLAMP
            )
            gradientPaint.shader = gradient

            rectF.set(0f, 0f, fillWidth, height)
            canvas.drawRoundRect(rectF, radius, radius, gradientPaint)
        }

        // Рисуем метки (деления)
        paint.color = Color.WHITE
        paint.textSize = height * 0.4f
        paint.textAlign = Paint.Align.CENTER

        val text = "${progress.toInt()}%"
        canvas.drawText(text, width / 2f, height / 2f + paint.textSize / 3f, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 80 // высота термометра в пикселях
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, height)
    }
}