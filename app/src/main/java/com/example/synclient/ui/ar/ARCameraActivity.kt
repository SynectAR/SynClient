package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.LayoutViewBuilder
import com.example.synclient.arLogic.ManagerAR
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.*


/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    var managerAR: ManagerAR = ManagerAR(this, this)


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
                when (msg.what){
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
        while(managerAR.layoutViewList.size -1 < 0);
        val layout: LayoutViewBuilder= managerAR.layoutViewList[managerAR.layoutViewList.size -1]
        while(layout.view == null);
        handler.sendEmptyMessage(layoutKind)
    }
    private fun updateConfig(){
        var config = managerAR.arFragment.arSceneView.session?.config
        if (config != null) {
            config.focusMode = Config.FocusMode.AUTO
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            managerAR.arFragment.arSceneView.session?.configure(config)
        }
    }
    private fun menuBind(){
        val view = managerAR.layoutViewList[managerAR.layoutViewList.size-1].view
        val buttonAbout = view?.findViewById<Button>(R.id.buttonAbout)
        val calibrationButton = view?.findViewById<Button>(R.id.buttonCalibration)
        buttonAbout?.setOnClickListener {
            val list = managerAR.layoutViewList
            list[list.size-1].destroyView()
            managerAR.showLayout(R.layout.about_channel_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(2)
            }
        }
        calibrationButton?.setOnClickListener {
            val list = managerAR.layoutViewList
            list[list.size-1].destroyView()
            managerAR.showLayout(R.layout.calibration_menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(3)
            }
        }
    }

    private fun infoBind(){
        val view = managerAR.layoutViewList[managerAR.layoutViewList.size-1].view
        val buttonReturn = view?.findViewById<Button>(R.id.buttonInfoReturn)
        buttonReturn?.setOnClickListener {
            val list = managerAR.layoutViewList
            list[list.size-1].destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }
    }

    private fun calibrationBind(){
        val view = managerAR.layoutViewList[managerAR.layoutViewList.size-1].view
        val buttonReturn = view?.findViewById<Button>(R.id.buttonReturn)
        buttonReturn?.setOnClickListener {
            val list = managerAR.layoutViewList
            list[list.size-1].destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }
    }

}