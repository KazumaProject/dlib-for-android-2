package com.kazumaproject.eyetracker.sensor

class SensorListener {
    private var sensorValueListener : SensorValueListener? = null
    interface Listener {}
    fun setListener(listener: Listener?) {
        if (listener is SensorValueListener) {
            this.sensorValueListener = listener
        }
    }
    fun getAllValues(x: Float, y: Float, z: Float){
        sensorValueListener?.getSensorValues(x,y,z)
    }
}