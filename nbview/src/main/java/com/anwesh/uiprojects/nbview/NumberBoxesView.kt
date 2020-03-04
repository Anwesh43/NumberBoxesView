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

import kotlin.collections.ArrayList

val boxes : Int = 6
val scGap : Float = 0.02f
val boxColor : Int = Color.parseColor("#4CAF50")
val textColor : Int = Color.parseColor("#FFFFFF")
val sizeFactor : Float = 5.8f
val fontSizeFactor : Float = 2.3f
val delay : Long = 15
val backColor : Int = Color.parseColor("#BDBDBD")

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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb :(Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : (Float) -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb(scale)
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class Box(var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawNumberBox(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : (Float) -> Unit) {
            state.startUpdating(cb)
        }
    }

    data class Boxes(private var upBoxes : ArrayList<Box?> = ArrayList(), private val downBoxes : ArrayList<Box?> = ArrayList(boxes)) {

        private var box : Box? = null
        private var dir : Int = 1
        init {
            for (i in 0..(boxes - 1)) {
                upBoxes.add(Box(i))
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            upBoxes.forEach {
                it?.draw(canvas, paint)
            }

            downBoxes.forEach {
                it?.draw(canvas, paint)
            }
            box?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            box?.update {

                when(it) {
                    0f -> {
                        upBoxes.add(box)
                        if (downBoxes.size == 0) {
                            dir = 1
                        }
                    }
                    1f -> {
                        downBoxes.add(box)
                        if (upBoxes.size  == 0) {
                            dir = -1
                        }
                    }
                }
                box = null
                cb(it)
            }
        }

        fun startUpdating(cb : (Float) -> Unit) {
            if (dir == 1 && upBoxes.size > 0) {
                box = upBoxes.removeAt(upBoxes.size - 1)
            }
            if (dir == -1 && downBoxes.size > 0) {
                box = downBoxes.removeAt(downBoxes.size - 1)
            }
            box?.startUpdating(cb)
        }
    }

    data class Renderer(var view : NumberBoxesView) {

        private val boxes : Boxes = Boxes()
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            boxes.draw(canvas, paint)
            animator.animate {
                boxes.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            boxes.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : NumberBoxesView {
            val view : NumberBoxesView = NumberBoxesView(activity)
            activity.setContentView(view)
            return view
        }
    }
}