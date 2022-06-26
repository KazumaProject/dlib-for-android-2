package com.kazumaproject.eyetracker.detection

import android.annotation.SuppressLint
import android.graphics.Point
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.tzutalin.dlib.FaceDet
import com.tzutalin.dlibtest.ImageUtils
import timber.log.Timber

typealias ObjectDetectorCallback = (image: List<DetectionObject>) -> Unit

class FaceDetection (
    private val mFaceDet: FaceDet,
    private val cameraSelectorState: Boolean,
    private val listener: ObjectDetectorCallback
)  : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        if (image.image == null) return
        Timber.d("image info rotation: ${image.imageInfo.rotationDegrees}\nwidth: ${image.width}")
        val detectedObjectList = detect(image.image!!, image.imageInfo.rotationDegrees, cameraSelectorState)
        listener(detectedObjectList)
        image.close()
    }

    private fun detect(targetImage: Image, orientation: Int, cameraSelectorState: Boolean): List<DetectionObject>{
        val targetBitmap = ImageUtils().get888BitMap(targetImage,orientation, cameraSelectorState)
        targetBitmap.let { bitmap ->
            val result = mFaceDet.detect(bitmap)
            result?.let { detectedObjects ->
                Timber.d("result size ${result.size}")
                if (detectedObjects.size != 0){
                    val detectionObjects = mutableListOf<DetectionObject>()
                    for (ret in detectedObjects){
                        val landMarks = ret.faceLandmarks
                        val detectionObject = DetectionObject(landMarks)
                        detectionObjects.add(detectionObject)
                    }
                    return detectionObjects
                }

            }
        }
        return emptyList()
    }



}