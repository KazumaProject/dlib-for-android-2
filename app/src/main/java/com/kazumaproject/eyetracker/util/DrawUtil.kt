package com.kazumaproject.eyetracker.util

import android.graphics.*
import android.view.SurfaceHolder
import com.kazumaproject.eyetracker.detection.DetectionObject

class DrawUtil (private val surfaceHolder: SurfaceHolder) {
    private val paint = Paint()

    fun clearView(){
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        //Reset
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

    fun drawLandmarks(
        detectedObjectList: List<DetectionObject>,
        cameraSelectorState: Boolean,
        colorMode: Int,
        orientation: Int
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

        /**
         * Portrait
         */
        if (orientation == 1){
            val resizeRatioXP = if (!cameraSelectorState) 3.25f else 2.25f
            val resizeRatioYP = if (!cameraSelectorState) 3.25f else 2.50f
            val offsetXP = if (!cameraSelectorState) -500 else -175
            val offsetYP = if (!cameraSelectorState) 250 else 425

            for (detectedObject in detectedObjectList){

                for (point in detectedObject.landMarks){
                    val pointX = (point.x * resizeRatioXP + offsetXP)
                    val pointY = (point.y * resizeRatioYP + offsetYP)
                    canvas?.drawCircle(pointX, pointY, 4f, paint)
                }
            }
        }

        /**
         * Landscape
         */
        if (orientation == 0){
            val resizeRatioXL = if (!cameraSelectorState) 1.65f else 1.25f
            val resizeRatioYL = if (!cameraSelectorState) 1.58f else 1.20f
            val offsetXL = if (!cameraSelectorState) 0 else 275
            val offsetYL = if (!cameraSelectorState) -50 else 75
            for (detectedObject in detectedObjectList){
                for (point in detectedObject.landMarks){
                    val pointX = (point.x * resizeRatioXL + offsetXL)
                    val pointY = (point.y * resizeRatioYL + offsetYL)
                    canvas?.drawCircle(pointX, pointY, 4f, paint)
                }
            }
        }

        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

    fun drawEyes(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean, colorMode: Int,orientation: Int){
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

        if (orientation == 1){
            val resizeRatioXP = if (!cameraSelectorState) 3.25f else 2.25f
            val resizeRatioYP = if (!cameraSelectorState) 3.25f else 2.50f
            val offsetXP = if (!cameraSelectorState) -500 else -175
            val offsetYP = if (!cameraSelectorState) 250 else 425

            val leftEye = arrayOfNulls<Point>(6)
            val rightEye = arrayOfNulls<Point>(6)
            var i = 1

            for (detectedObject in detectedObjectList){

                for (point in detectedObject.landMarks){
                    if (i in 37..42){
                        val pointX = (point.x * resizeRatioXP + offsetXP)
                        val pointY = (point.y * resizeRatioYP + offsetYP)
                        leftEye[i - 37] = Point(pointX.toInt(), pointY.toInt())
                    } else if (i in 43..48){
                        val pointX = (point.x * resizeRatioXP + offsetXP)
                        val pointY = (point.y * resizeRatioYP + offsetYP)
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
        }

        if (orientation == 0){
            val resizeRatioXL = if (!cameraSelectorState) 1.65f else 1.25f
            val resizeRatioYL = if (!cameraSelectorState) 1.58f else 1.20f
            val offsetXL = if (!cameraSelectorState) 0 else 275
            val offsetYL = if (!cameraSelectorState) -50 else 75

            val leftEye = arrayOfNulls<Point>(6)
            val rightEye = arrayOfNulls<Point>(6)
            var i = 1

            for (detectedObject in detectedObjectList){

                for (point in detectedObject.landMarks){
                    if (i in 37..42){
                        val pointX = (point.x * resizeRatioXL + offsetXL)
                        val pointY = (point.y * resizeRatioYL + offsetYL)
                        leftEye[i - 37] = Point(pointX.toInt(), pointY.toInt())
                    } else if (i in 43..48){
                        val pointX = (point.x * resizeRatioXL + offsetXL)
                        val pointY = (point.y * resizeRatioYL + offsetYL)
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

    fun drawRectAroundEyes(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean,colorMode: Int, orientation: Int){
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

        if (orientation == 1){

            val leftEye = arrayOfNulls<Point>(4)
            val rightEye = arrayOfNulls<Point>(4)
            val resizeRatioXP = if (!cameraSelectorState) 3.25f else 2.25f
            val resizeRatioYP = if (!cameraSelectorState) 3.25f else 2.50f
            val offsetXP = if (!cameraSelectorState) -500 else -175
            val offsetYP = if (!cameraSelectorState) 250 else 425

            for (detectedObject in detectedObjectList){
                var i = 1
                for (point in detectedObject.landMarks){
                    when(i){
                        // Left eye
                        37 -> {
                            val pointX = (point.x * resizeRatioXP + offsetXP) - 30
                            val pointY = (point.y * resizeRatioYP + offsetYP)
                            leftEye[0] = Point(pointX.toInt(), pointY.toInt())
                        }
                        38 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP)
                            val pointY = (point.y * resizeRatioYP + offsetYP) - 30
                            leftEye[1] = Point(pointX.toInt(), pointY.toInt())
                        }
                        40 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP) + 30
                            val pointY = (point.y * resizeRatioYP + offsetYP)
                            leftEye[2] = Point(pointX.toInt(), pointY.toInt())
                        }
                        42 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP)
                            val pointY = (point.y * resizeRatioYP + offsetYP) + 30
                            leftEye[3] = Point(pointX.toInt(), pointY.toInt())
                        }
                        // Right eye
                        43 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP) - 30
                            val pointY = (point.y * resizeRatioYP + offsetYP)
                            rightEye[0] = Point(pointX.toInt(), pointY.toInt())
                        }
                        44 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP)
                            val pointY = (point.y * resizeRatioYP + offsetYP) - 30
                            rightEye[1] = Point(pointX.toInt(), pointY.toInt())
                        }
                        46 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP) + 30
                            val pointY = (point.y * resizeRatioYP + offsetYP)
                            rightEye[2] = Point(pointX.toInt(), pointY.toInt())
                        }
                        48 ->{
                            val pointX = (point.x * resizeRatioXP + offsetXP)
                            val pointY = (point.y * resizeRatioYP + offsetYP) + 30
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

        }

        if (orientation == 0){

            val leftEye = arrayOfNulls<Point>(4)
            val rightEye = arrayOfNulls<Point>(4)
            val resizeRatioXL = if (!cameraSelectorState) 1.65f else 1.25f
            val resizeRatioYL = if (!cameraSelectorState) 1.58f else 1.20f
            val offsetXL = if (!cameraSelectorState) 0 else 275
            val offsetYL = if (!cameraSelectorState) -50 else 75

            for (detectedObject in detectedObjectList){
                var i = 1
                for (point in detectedObject.landMarks){
                    when(i){
                        // Left eye
                        37 -> {
                            val pointX = (point.x * resizeRatioXL + offsetXL) - 30
                            val pointY = (point.y * resizeRatioYL + offsetYL)
                            leftEye[0] = Point(pointX.toInt(), pointY.toInt())
                        }
                        38 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL)
                            val pointY = (point.y * resizeRatioYL + offsetYL) - 30
                            leftEye[1] = Point(pointX.toInt(), pointY.toInt())
                        }
                        40 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL) + 30
                            val pointY = (point.y * resizeRatioYL + offsetYL)
                            leftEye[2] = Point(pointX.toInt(), pointY.toInt())
                        }
                        42 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL)
                            val pointY = (point.y * resizeRatioYL + offsetYL) + 30
                            leftEye[3] = Point(pointX.toInt(), pointY.toInt())
                        }
                        // Right eye
                        43 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL) - 30
                            val pointY = (point.y * resizeRatioYL + offsetYL)
                            rightEye[0] = Point(pointX.toInt(), pointY.toInt())
                        }
                        44 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL)
                            val pointY = (point.y * resizeRatioYL + offsetYL) - 30
                            rightEye[1] = Point(pointX.toInt(), pointY.toInt())
                        }
                        46 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL) + 30
                            val pointY = (point.y * resizeRatioYL + offsetYL)
                            rightEye[2] = Point(pointX.toInt(), pointY.toInt())
                        }
                        48 ->{
                            val pointX = (point.x * resizeRatioXL + offsetXL)
                            val pointY = (point.y * resizeRatioYL + offsetYL) + 30
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

        }


        surfaceHolder.unlockCanvasAndPost(canvas ?: return)
    }

}