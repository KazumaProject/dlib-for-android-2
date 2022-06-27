package com.kazumaproject.eyetracker.ui.eye_detect

import android.annotation.SuppressLint
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.kazumaproject.eyetracker.detection.DetectionObject
import com.kazumaproject.eyetracker.util.DrawUtil

@SuppressLint("ViewConstructor")
class OverlaySurfaceView (surfaceView: SurfaceView) :
    SurfaceView(surfaceView.context), SurfaceHolder.Callback{

    init {
        surfaceView.holder.addCallback(this)
        surfaceView.setZOrderOnTop(true)
    }

    private var surfaceHolder = surfaceView.holder

    override fun surfaceCreated(p0: SurfaceHolder) {
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT)
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }

    fun clearView(){
        DrawUtil(surfaceHolder).clearView()
    }

    fun drawEyes(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean, colorMode: Int,orientation: Int){
        DrawUtil(surfaceHolder).drawEyes(detectedObjectList, cameraSelectorState, colorMode,orientation)
    }

    fun drawLandMarks(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean, colorMode: Int, orientation: Int){
        DrawUtil(surfaceHolder).drawLandmarks(detectedObjectList, cameraSelectorState, colorMode, orientation)
    }

    fun drawAroundEye(detectedObjectList: List<DetectionObject>, cameraSelectorState: Boolean, colorMode: Int,orientation: Int){
        DrawUtil(surfaceHolder).drawRectAroundEyes(detectedObjectList, cameraSelectorState, colorMode,orientation)
    }

}