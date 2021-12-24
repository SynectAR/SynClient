package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.LayoutViewBuilder
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.calibrationHelper.CalibrationHelper
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.*


/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    var listCalibration:MutableList<Int> = mutableListOf<Int>()
    var managerAR: ManagerAR = ManagerAR(this, this)
    var selectedPort: Int = -1
    var portArray: Array<Boolean> = arrayOf(false, false, false)


    override fun onAttachedToWindow() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)
        // Создание CustomArFragment и инициализация работы с
        // распознованием картинок через AugmentedImages.
        val listOfVectors = mutableListOf<Vector3>()
        val listOfQuaternion = mutableListOf<Quaternion>()
        for (i in 0..7) {
            listOfVectors.add(Vector3(0.04f + 0.04f * i, 0.006f, 0.004f))
            listOfQuaternion.add(Quaternion.axisAngle(Vector3(-90f, 0f, 0f), 1f))
        }

        for (i in 0..7) {
            listOfVectors.add(Vector3(0.02f + 0.04f * i, -0.0055f, -0.03f))
            listOfQuaternion.add(Quaternion.axisAngle(Vector3(-60f, 0f, 0f), 1f))
        }

        managerAR.setAugmentedImagesOnUpdateListener(listOfVectors, listOfQuaternion)

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> updateConfig()
                    1 -> menuBind()
                    2 -> infoBind()
                    3 -> calibrationBind()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            waitForConfig()
            waitForLayout(1)
        }
    }

    private fun waitForConfig() {
        while (managerAR.arFragment.arSceneView.session == null);
        handler.sendEmptyMessage(0)
    }

    private fun waitForLayout(layoutKind: Int) {
        while (managerAR.layoutView.view == null);
        handler.sendEmptyMessage(layoutKind)
    }

    private fun updateConfig() {
        var config = managerAR.arFragment.arSceneView.session?.config
        if (config != null) {
            config.focusMode = Config.FocusMode.AUTO
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            managerAR.arFragment.arSceneView.session?.configure(config)
        }
    }

    private fun menuBind() {
        val view = managerAR.layoutView.view
        val buttonAbout = view?.findViewById<Button>(R.id.buttonAbout)
        val calibrationButton = view?.findViewById<Button>(R.id.buttonCalibration)
        buttonAbout?.setOnClickListener {
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.about_channel_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(2)
            }
        }
        calibrationButton?.setOnClickListener {
            val portList = managerAR.portList
            listCalibration.clear()
            portList.forEachIndexed { index, portViewBuilder ->
                if(portViewBuilder.isChecked){
                    portViewBuilder.changePortStatus()
                    //portViewBuilder.changePortColor(Color.rgb(255,255,255))
                    listCalibration.add(index)
                }
                else{
                    portViewBuilder.view.visibility = View.INVISIBLE
                }
            }
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.calibration_menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(3)
            }
        }
    }

    private fun infoBind() {
        val view = managerAR.layoutView.view
        val buttonReturn = view?.findViewById<Button>(R.id.buttonInfoReturn)
        buttonReturn?.setOnClickListener {
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }
    }

    private fun calibrationBind() {
        val view = managerAR.layoutView.view
        val buttonReturn = view?.findViewById<Button>(R.id.buttonReturn)
        val buttonOpen = view?.findViewById<Button>(R.id.buttonOpen)
        val buttonShort = view?.findViewById<Button>(R.id.buttonShort)
        val buttonLoad = view?.findViewById<Button>(R.id.buttonLoad)
        val buttonThru = view?.findViewById<Button>(R.id.buttonThru)
        buttonReturn?.setOnClickListener {
            val portList = managerAR.portList
            portList.forEach {
                 it.view.visibility = View.VISIBLE
            }
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }

        buttonOpen?.setOnClickListener {
            findCheckedPort()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "O") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }

        }
        buttonShort?.setOnClickListener {
            findCheckedPort()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "S") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
        buttonLoad?.setOnClickListener {
            findCheckedPort()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "L") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
        buttonThru?.setOnClickListener {
            findCheckedPort()
            val index = listCalibration.indexOf(listCalibration.indexOf(selectedPort-1))
            runBlocking { CalibrationHelper.getPortMeasureThru(selectedPort, (listCalibration[index+1] +1)) }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
    }

    fun findCheckedPort()
    {
        managerAR.portList.forEachIndexed { index, portViewBuilder ->
            if (portViewBuilder.isChecked)
                selectedPort = index+1
        }
    }

    fun statusOnClick(v: View) {
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        var view = findViewById<TextView>(R.id.textViewOPEN)
        view.text = portArray[0].toString()
        view = findViewById(R.id.textViewSHORT)
        view.text = portArray[1].toString()
        view = findViewById(R.id.textViewLOAD)
        view.text = portArray[2].toString()
    }

}