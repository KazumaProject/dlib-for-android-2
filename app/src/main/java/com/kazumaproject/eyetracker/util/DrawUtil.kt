package com.kazumaproject.eyetracker.util

import android.graphics.*
import android.view.SurfaceHolder
import com.kazumaproject.eyetracker.detection.DetectionObject

class DrawUtil (private val surfaceHolder: SurfaceHolder) {
    private val paint = Paint()

    fun drawLandmarks(
        detectedObjectList: List<DetectionObject>,
        cameraSelectorState: Boolean,
        colorMode: Int
    ){
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        //Reset
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)

        paint.apply {
            color = when(colorMode){
                0 -> Color.GREEN
                1 -> Color.BLUE
                else -> Color.BLACK
            }
            strokeWidth = 8f
            style = Paint.Style.STROKE
        }

        val resizeRatioX = if (!cameraSelectorState) 3.25f else 2.25f
        val resizeRatioY = if (!cameraSelectorState) 3.25f else 2.50f
        val offsetX = if (!cameraSelectorState) -500 else -175
        val offsetY = if (!cameraSelectorState) 250 else 425

        for (detectedObject in detectedObjectList){

            for (point in detectedObject.landMarks){
                val pointX = (point.x * resizeRatioX + offsetX)
                val pointY = (point.y * resizeRatioY + offsetY)
                canvas?.drawCircle(pointX, pointY, 4f, paint)
            }
        }
        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

    fun drawEyes(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean, colorMode: Int){
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        //Reset
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)

        paint.apply {
            color = when(colorMode){
                0 -> Color.GREEN
                1 -> Color.BLUE
                else -> Color.BLACK
            }
            strokeWidth = 8f
            style = Paint.Style.STROKE
        }

        val resizeRatioX = if (!cameraSelectorState) 3.25f else 2.25f
        val resizeRatioY = if (!cameraSelectorState) 3.25f else 2.50f
        val offsetX = if (!cameraSelectorState) -500 else -175
        val offsetY = if (!cameraSelectorState) 250 else 425

        val leftEye = arrayOfNulls<Point>(6)
        val rightEye = arrayOfNulls<Point>(6)
        var i = 1

        for (detectedObject in detectedObjectList){

            for (point in detectedObject.landMarks){
                if (i in 37..42){
                    val pointX = (point.x * resizeRatioX + offsetX)
                    val pointY = (point.y * resizeRatioY + offsetY)
                    leftEye[i - 37] = Point(pointX.toInt(), pointY.toInt())
                } else if (i in 43..48){
                    val pointX = (point.x * resizeRatioX + offsetX)
                    val pointY = (point.y * resizeRatioY + offsetY)
                    rightEye[i - 43] = Point(pointX.toInt(),pointY.toInt())
                }
                if (i > 48){
                    break
                }
                i++

            }
            canvas?.drawPath(getPath(leftEye),paint)
            canvas?.drawPath(getPath(rightEye),paint)
        }
        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

    private fun getPath(points: Array<Point?>): Path {
        val path = Path()

        path.moveTo(points[0]!!.x.toFloat(), points[0]!!.y.toFloat()) //起点

        for (i in 1 until points.size) {
            path.lineTo(points[i]!!.x.toFloat(), points[i]!!.y.toFloat())
        }
        path.close()
        return path
    }

    fun drawRectAroundEyes(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean,colorMode: Int){
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        //Reset
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)

        paint.apply {
            color = when(colorMode){
                0 -> Color.GREEN
                1 -> Color.BLUE
                else -> Color.BLACK
            }
            strokeWidth = 8f
            style = Paint.Style.STROKE
        }

        val leftEye = arrayOfNulls<Point>(4)
        val rightEye = arrayOfNulls<Point>(4)
        val resizeRatioX = if (!cameraSelectorState) 3.25f else 2.25f
        val resizeRatioY = if (!cameraSelectorState) 3.25f else 2.50f
        val offsetX = if (!cameraSelectorState) -500 else -175
        val offsetY = if (!cameraSelectorState) 250 else 425

        for (detectedObject in detectedObjectList){
            var i = 1
            for (point in detectedObject.landMarks){
                when(i){
                    // Left eye
                    37 -> {
                        val pointX = (point.x * resizeRatioX + offsetX) - 30
                        val pointY = (point.y * resizeRatioY + offsetY)
                        leftEye[0] = Point(pointX.toInt(), pointY.toInt())
                    }
                    38 ->{
                        val pointX = (point.x * resizeRatioX + offsetX)
                        val pointY = (point.y * resizeRatioY + offsetY) - 30
                        leftEye[1] = Point(pointX.toInt(), pointY.toInt())
                    }
                    40 ->{
                        val pointX = (point.x * resizeRatioX + offsetX) + 30
                        val pointY = (point.y * resizeRatioY + offsetY)
                        leftEye[2] = Point(pointX.toInt(), pointY.toInt())
                    }
                    42 ->{
                        val pointX = (point.x * resizeRatioX + offsetX)
                        val pointY = (point.y * resizeRatioY + offsetY) + 30
                        leftEye[3] = Point(pointX.toInt(), pointY.toInt())
                    }
                    // Right eye
                    43 ->{
                        val pointX = (point.x * resizeRatioX + offsetX) - 30
                        val pointY = (point.y * resizeRatioY + offsetY)
                        rightEye[0] = Point(pointX.toInt(), pointY.toInt())
                    }
                    44 ->{
                        val pointX = (point.x * resizeRatioX + offsetX)
                        val pointY = (point.y * resizeRatioY + offsetY) - 30
                        rightEye[1] = Point(pointX.toInt(), pointY.toInt())
                    }
                    46 ->{
                        val pointX = (point.x * resizeRatioX + offsetX) + 30
                        val pointY = (point.y * resizeRatioY + offsetY)
                        rightEye[2] = Point(pointX.toInt(), pointY.toInt())
                    }
                    48 ->{
                        val pointX = (point.x * resizeRatioX + offsetX)
                        val pointY = (point.y * resizeRatioY + offsetY) + 30
                        rightEye[3] = Point(pointX.toInt(), pointY.toInt())
                    }
                }
                if (i > 48){
                    break
                }
                i++

            }
            // Draw left eye
            canvas?.drawRect(
                leftEye[0]!!.x.toFloat(),
                leftEye[1]!!.y.toFloat(),
                leftEye[2]!!.x.toFloat(),
                leftEye[3]!!.y.toFloat(),
                paint
            )
            // Draw right eye
            canvas?.drawRect(
                rightEye[0]!!.x.toFloat(),
                rightEye[1]!!.y.toFloat(),
                rightEye[2]!!.x.toFloat(),
                rightEye[3]!!.y.toFloat(),
                paint
            )
        }
        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

}