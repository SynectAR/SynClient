package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.synclient.R
import com.example.synclient.adapter.ChannelAdapter
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.entities.ItemChannel
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {
    var listOfChannelItems = ArrayList<ItemChannel>()
    private lateinit var handler: Handler
    var managerAR: ManagerAR = ManagerAR(this, this)

    private var adapter = object : ChannelAdapter() {
        override fun onClickBuilder(layout: LinearLayout, index: Int) {
            layout.setOnClickListener {
                if (managerAR.isCreated) {
                    when (index) {
                        0 -> managerAR.changePortsColor(Color.RED)
                        1 -> managerAR.changePortsColor(Color.GREEN)
                        2 -> managerAR.changePortsColor(Color.BLUE)
                        3 -> managerAR.changePortsColor(Color.BLACK)
                        4 -> managerAR.portList.forEachIndexed { index, portViewBuilder ->
                            if (index % 2 == 0)
                                portViewBuilder.changePortColor(Color.GREEN)
                            else
                                portViewBuilder.changePortColor(Color.RED)
                        }
                        5 -> managerAR.portList.forEachIndexed { index, portViewBuilder ->
                            val rnd = Random()
                            portViewBuilder.changePortColor(
                                Color.argb(
                                    255,
                                    rnd.nextInt(256),
                                    rnd.nextInt(256),
                                    rnd.nextInt(256)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

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

        uploadRV()
        managerAR.setAugmentedImagesOnUpdateListener(listOfVectors, listOfQuaternion)

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

    fun uploadRV() {
        listOfChannelItems.clear()
        listOfChannelItems.add(ItemChannel("Ch1", true))
        listOfChannelItems.add(ItemChannel("Ch2", true))
        listOfChannelItems.add(ItemChannel("Ch3", true))
        listOfChannelItems.add(ItemChannel("Ch4", false))
        listOfChannelItems.add(ItemChannel("Ch5", false))
        listOfChannelItems.add(ItemChannel("Ch6", false))
        listOfChannelItems.add(ItemChannel("Ch7", true))
        listOfChannelItems.add(ItemChannel("Ch8", false))
        listOfChannelItems.add(ItemChannel("Ch9", false))
        listOfChannelItems.add(ItemChannel("Ch10", true))
        listOfChannelItems.add(ItemChannel("Ch11", true))
        listOfChannelItems.add(ItemChannel("Ch12", false))
        listOfChannelItems.add(ItemChannel("Ch13", false))
        listOfChannelItems.add(ItemChannel("Ch14", true))
        listOfChannelItems.add(ItemChannel("Ch15", true))

        adapter.setData(listOfChannelItems)
        val channelRV: RecyclerView = findViewById<RecyclerView>(R.id.channelRV)
        channelRV.layoutManager = LinearLayoutManager(
            this.baseContext,
            LinearLayoutManager.VERTICAL, false
        )
        channelRV.adapter = adapter
    }
}