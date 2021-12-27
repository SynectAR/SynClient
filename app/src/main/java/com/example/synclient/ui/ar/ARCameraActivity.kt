package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.calibrationHelper.CalibrationHelper
import com.example.synclient.entities.PortCalibrationStatus
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    var channelNumber = 1
    var channelsCount = 1
    var mapOfPortsStatuses: MutableMap<Int, PortCalibrationStatus> =
        mutableMapOf<Int, PortCalibrationStatus>()
    var listCalibration: MutableList<Int> = mutableListOf<Int>()
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
        channelsCount = getChannelCount()
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
        while (managerAR.layoutView.view == null) {
            Thread.sleep(5)
        }
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
        changeChannelPorts()
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
        val info = view?.findViewById<TextView>(R.id.aboutText)
        buttonReturn?.setOnClickListener {
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }

        var receivedText: String? = null
        runBlocking {
            receivedText = "Кол-во Портов: " + CalibrationHelper.getPortCount().toString() + "\n"
        }
        runBlocking {
            receivedText += "Ready Status: " + CalibrationHelper.getReadyStatus() + "\n"
        }
        runBlocking { receivedText += "SweepType: " + CalibrationHelper.getSweepType() + "\n" }
        runBlocking { receivedText += "PointsCount: " + CalibrationHelper.getPointsCount() + "\n" }
        runBlocking { receivedText += "TriggerMode: " + CalibrationHelper.getTriggerMode() + "\n" }
        runBlocking { receivedText += "Span: NOT IMPLEMENTED" + "\n" }
        runBlocking { receivedText += "RfOut: " + CalibrationHelper.getRfOut() + "\n" }
        info?.text = receivedText

    }

    private fun calibrationBind() {
        fillPortStatus()
        // Делаем так чтобы только 1 порт был активен и тут же обновляем UI
        val portList = managerAR.portList
        portList.forEach {
            val port = it
            val radioButton = it.view.findViewById<RadioButton>(R.id.portChecked)
            radioButton.setOnClickListener {
                port.isChecked = !port.isChecked
                radioButton.isChecked = port.isChecked
                if (port.isChecked) {
                    portList.forEach {
                        it.statustTo(false)
                    }
                    port.statustTo(true)
                }
                updateMenuUI()
            }
        }


        // Заполняем UI функционалом
        val view = managerAR.layoutView.view
        val buttonReturn = view?.findViewById<Button>(R.id.buttonReturn)
        val buttonOpen = view?.findViewById<Button>(R.id.buttonOpen)
        val buttonShort = view?.findViewById<Button>(R.id.buttonShort)
        val buttonLoad = view?.findViewById<Button>(R.id.buttonLoad)
        val buttonThru = view?.findViewById<Button>(R.id.buttonThru)
        val buttonApply = view?.findViewById<Button>(R.id.buttonApply)
        buttonReturn?.setOnClickListener {
            runBlocking { CalibrationHelper.getReset() }
            managerAR.layoutView.destroyView()
            managerAR.showLayout(R.layout.menu_ar)
            CoroutineScope(Dispatchers.IO).launch {
                waitForLayout(1)
            }
        }

        buttonOpen?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            portStatus?.open = true
            updateMenuUI()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "O") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }

        }
        buttonShort?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            portStatus?.short = true
            updateMenuUI()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "S") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
        buttonLoad?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            portStatus?.load = true
            updateMenuUI()
            runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "L") }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
        buttonThru?.setOnClickListener {
            val index = listCalibration.indexOf(listCalibration.indexOf(selectedPort - 1))
            val nextPort = listCalibration[index + 1] + 1

            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            val portStatusSecond = mapOfPortsStatuses.get(nextPort - 1)
            portStatus?.thru = true
            portStatusSecond?.thru = true

            updateMenuUI()

            runBlocking { CalibrationHelper.getPortMeasureThru(selectedPort, nextPort) }
            runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        }
        buttonApply?.setOnClickListener {
            if (checkApply())
                runBlocking { CalibrationHelper.getApply() }
            else
                Toast.makeText(
                    this, "Калибровка не завершена",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    // Возвращает выбранный порт
    fun findCheckedPort() {
        managerAR.portList.forEachIndexed { index, portViewBuilder ->
            if (portViewBuilder.isChecked)
                selectedPort = index + 1
        }
    }

    fun statusOnClick(v: View) {
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
    }


    // Запрашивает у сервера число активных портов в текущем канале
    fun getChannelPorts() {
        listCalibration.clear()
        listCalibration = mutableListOf(0, 1)
    }

    // Перед калибровкой заполняет карту значениями для каждого используемого порта
    fun fillPortStatus() {
        mapOfPortsStatuses.clear()
        listCalibration.forEach {
            mapOfPortsStatuses[it] =
                PortCalibrationStatus(open = false, short = false, load = false, thru = false)
        }
    }

    // Меняет цвета портов и обновляет их
    fun changeChannelPorts() {
        getChannelPorts()
        val listPort = managerAR.portList
        listPort.forEach {
            it.statustTo(false)
            it.changePortColor(Color.GRAY)
            it.changeActive(false)
        }
        listCalibration.forEach {
            listPort[it].changePortColor(Color.WHITE)
            listPort[it].changeActive(true)
        }
        managerAR.refreshPorts()
    }


    // Обновляет UI во время калибровки и смены порта
    fun updateMenuUI() {
        findCheckedPort()
        val checked = selectedPort - 1
        val portStatus = mapOfPortsStatuses[checked]
        val view = managerAR.layoutView.view

        val buttonOpen = view?.findViewById<Button>(R.id.buttonOpen)
        val buttonShort = view?.findViewById<Button>(R.id.buttonShort)
        val buttonLoad = view?.findViewById<Button>(R.id.buttonLoad)
        val buttonThru = view?.findViewById<Button>(R.id.buttonThru)
        // OPEN
        var isReady = portStatus?.open
        if (isReady == true)
            buttonOpen?.setBackgroundColor(Color.GREEN)
        else
            buttonOpen?.setBackgroundColor(Color.WHITE)
        // SHORT
        isReady = portStatus?.short
        if (isReady == true)
            buttonShort?.setBackgroundColor(Color.GREEN)
        else
            buttonShort?.setBackgroundColor(Color.WHITE)
        // LOAD
        isReady = portStatus?.load
        if (isReady == true)
            buttonLoad?.setBackgroundColor(Color.GREEN)
        else
            buttonLoad?.setBackgroundColor(Color.WHITE)
        // THRU
        isReady = portStatus?.thru
        if (isReady == true)
            buttonThru?.setBackgroundColor(Color.GREEN)
        else
            buttonThru?.setBackgroundColor(Color.WHITE)
    }

    // Проверяет используемые порты на состояние проверки
    fun checkApply(): Boolean {
        listCalibration.forEach {
            val portStatus = mapOfPortsStatuses[it]
            if (portStatus != null) {
                if (!(portStatus.open && portStatus.load && portStatus.short && portStatus.thru))
                    return false
            }
        }
        return true
    }

    // Запрашивает у сервера число каналов
    fun getChannelCount(): Int {
        return 4
    }
}