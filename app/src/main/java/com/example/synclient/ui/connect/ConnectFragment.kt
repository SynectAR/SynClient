package com.example.synclient.ui.connect

import android.app.AppOpsManager
import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.example.synclient.R
import com.example.synclient.adapter.ChannelAdapter
import com.example.synclient.adapter.GyroscopeAdapter
import com.example.synclient.connection.SocketConnect
import com.google.android.material.textfield.TextInputEditText
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class ConnectFragment : Fragment() {
    var vector3 = Vector3()
    private var isConnected: Boolean = false
    private lateinit var handler: Handler
    private lateinit var bitmap: Bitmap
    private lateinit var deviceInfo: String
    private val address = "192.168.1.85"
    private var port = 8000
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Отслеживание изменений гироскопа
        var context = this.context as Context
       /* val sensorManager: SensorManager? = getSystemService(context, SensorManager::class.java)
        val mAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var sensorListener = object: GyroscopeAdapter() {
            override fun vectorController(vector3: Vector3){
                this@ConnectFragment.vector3 = vector3
                Log.e("TAG", vector3.toString())
            }
        }
        sensorManager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)*/


        val button = view?.findViewById(R.id.button_connect) as Button
        val addressText = view?.findViewById(R.id.ip_adress_view) as TextInputEditText
        val tvManufacturer = view?.findViewById(R.id.tvManufacturer) as TextView
        val tvModel = view?.findViewById(R.id.tvModel) as TextView
        val tvSerialNumber = view?.findViewById(R.id.tvSerialNumber) as TextView
        val tvSoftwareVersion = view?.findViewById(R.id.tvSoftwareVersion) as TextView
        button.setOnClickListener {
            getDeviceInfo()
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var infoArray = deviceInfo.split(",")
                tvManufacturer.text = "Производитель: " + infoArray[0]
                tvModel.text = "Модель: " + infoArray[1]
                tvSerialNumber.text = "Серийный номер: " + infoArray[2]
                tvSoftwareVersion.text = "Версия приложения: " + infoArray[3]
            }
        }
    }

    private fun getDeviceInfo() {
        Log.e("TAG", vector3.toString())
    }

}
