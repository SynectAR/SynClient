package com.example.synclient.adapter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.google.ar.sceneform.math.Vector3

open class GyroscopeAdapter: SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        var vector = Vector3(event!!.values[0],event.values[1],event.values[2])
        vectorController(vector)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    open fun vectorController(vector3: Vector3){
    }
}