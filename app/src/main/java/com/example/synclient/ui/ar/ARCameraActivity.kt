package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.arLogic.CustomArFragment
import com.example.synclient.arLogic.PortViewBuilder
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    var portsList: MutableList<PortViewBuilder> = mutableListOf()
    //Инициализация менеджера для работы с виджетами.
    var managerAR: ManagerAR = ManagerAR(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)

        // Создание CustomArFragment и инициализация работы с
        // распознованием картинок через AugmentedImages.
        val listOfVectors = mutableListOf<Vector3>()
        val listOfQuaternion = mutableListOf<Quaternion>()
        for(i in 0..7){
            listOfVectors.add(Vector3(0.04f+0.04f*i, 0.006f, 0.004f))
            listOfQuaternion.add(Quaternion.axisAngle(Vector3(-90f, 0f, 0f), 1f))
        }

        for(i in 0..7){
            listOfVectors.add(Vector3(0.02f+0.04f*i, -0.0055f, -0.03f))
            listOfQuaternion.add(Quaternion.axisAngle(Vector3(-60f, 0f, 0f), 1f))
        }

        managerAR.setAugmentedImagesOnUpdateListener(listOfVectors,listOfQuaternion )

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                var config = managerAR.arFragment.arSceneView.session?.config
                if (config != null) {
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    managerAR.arFragment.arSceneView.session?.configure(config)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateConfig()
        }
    }

    private fun updateConfig() {
        while (managerAR.arFragment.arSceneView.session == null) {
            continue
        }
        handler.sendEmptyMessage(0)
    }
}