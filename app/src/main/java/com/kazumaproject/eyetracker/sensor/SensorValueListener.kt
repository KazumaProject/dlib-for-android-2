package com.kazumaproject.eyetracker.sensor

interface SensorValueListener: SensorListener.Listener {
    fun getSensorValues(x: Float,y: Float,z: Float)
}