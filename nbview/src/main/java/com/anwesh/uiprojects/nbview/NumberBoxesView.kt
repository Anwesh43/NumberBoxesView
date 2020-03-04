package com.anwesh.uiprojects.nbview

/**
 * Created by anweshmishra on 04/03/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.content.Context
import android.app.Activity

val boxes : Int = 6
val scGap : Float = 0.02f
val boxColor : Int = Color.parseColor("#4CAF50")
val textColor : Int = Color.parseColor("#FFFFFF")
val sizeFactor : Float = 5.8f
val fontSizeFactor : Float = 2.3f

fun Canvas.drawNumberBox(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = Math.min(w, h) / sizeFactor
    val text : String = "$i"
    paint.color = boxColor
    paint.textSize = size / fontSizeFactor
    val tw : Float = paint.measureText(text)
    save()
    translate(size / 2, size / 2 + (h - size) * scale)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    paint.color = textColor
    drawText(text, -tw / 2, -paint.textSize / 4, paint)
    restore()
}

class NumberBoxesView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}