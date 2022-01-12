package com.example.synclient.ui.ar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.synclient.R
import com.example.synclient.adapter.ChannelAdapter
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.calibrationHelper.CalibrationHelper
import com.example.synclient.entities.ItemChannel
import com.example.synclient.entities.PortCalibrationStatus
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vnarpc.sweep_type


/**
 * Класс, обрабатывающий взаимодействие и работу с камерой телефона в AR пространстве.
 */
class ARCameraActivity : AppCompatActivity() {
    var itemsChannel = ArrayList<ItemChannel>()
    private var adapter = object : ChannelAdapter() {
        override fun onClickBuilder(view: View, index: Int) {
            currentChannelNumber = index + 1
            onChannelChanged()
        }
    }

    var listOfVectors = mutableListOf<Vector3>()
    private lateinit var handler: Handler
    var currentChannelNumber = 1
    var first = true
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

        val channelNumberView = this.findViewById<TextView>(R.id.channelNumber)
        channelNumberView.visibility = View.INVISIBLE
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
            val bitmapQR = BitmapFactory.decodeResource(resources, R.drawable.demo_img2)
            val aid = AugmentedImageDatabase(managerAR.arFragment.arSceneView.session)
            aid.addImage("qrCode", bitmapQR, 0.015f)
            config.augmentedImageDatabase = aid
            config.planeFindingMode = Config.PlaneFindingMode.DISABLED
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            managerAR.arFragment.arSceneView.session?.configure(config)
        }
    }

    private fun menuBind() {
        uploadRV()
        if (!first)
            changeChannelPorts(Color.WHITE)
        else {
            first = false
        }

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
            }
        }

        hideRV(false)
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
        hideRV(true)
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
        runBlocking {
            receivedText += "SweepType: " + CalibrationHelper.getSweepType(
                currentChannelNumber
            ) + "\n"
        }
        runBlocking {
            receivedText += "PointsCount: " + CalibrationHelper.getPointsCount(
                currentChannelNumber
            ) + "\n"
        }
        runBlocking { receivedText += "TriggerMode: " + CalibrationHelper.getTriggerMode() + "\n" }
        var receivedMode: sweep_type
        runBlocking { receivedMode = CalibrationHelper.getSweepType(currentChannelNumber) }
        var receivedSpan: Array<Double>?
        runBlocking { receivedSpan = CalibrationHelper.getSpan(receivedMode, currentChannelNumber) }
        runBlocking { receivedText += "Span: " + "min: " + receivedSpan!![0] + " max: " + receivedSpan!![1] + " result: " + (receivedSpan!![1] - receivedSpan!![0]) + "\n" }
        runBlocking { receivedText += "RfOut: " + CalibrationHelper.getRfOut() + "\n" }
        info?.text = receivedText
    }

    private fun calibrationBind() {
        hideRV(true)
        managerAR.indexLastChangedPort = -1
        changeChannelPorts(Color.rgb(244, 0, 0))
        fillPortStatus()
        // Делаем так чтобы только 1 порт был активен и тут же обновляем UI
        val portList = managerAR.portList
        portList.forEachIndexed { index, it ->
            val port = it
            val radioButton = it.view.findViewById<RadioButton>(R.id.portChecked)
            radioButton.setOnClickListener {
                try {
                    val indexInList = listCalibration.indexOf(index)
                    val nextPort = listCalibration[indexInList + 1]
                    if (managerAR.indexLastChangedPort != -1) {
                        managerAR.returnColor()
                    }
                    managerAR.indexLastChangedPort = nextPort
                    managerAR.createLine(listOfVectors[index],listOfVectors[nextPort])
                    managerAR.portList[nextPort].changePortColor(Color.YELLOW, false)
                } catch (exception: Throwable) {
                    if (managerAR.indexLastChangedPort != -1) {
                        managerAR.returnColor()
                        managerAR.indexLastChangedPort = -1
                    }
                }
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
            runBlocking { CalibrationHelper.getReset(currentChannelNumber) }
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
            runBlocking {
                CalibrationHelper.getPortMeasure(
                    selectedPort,
                    "O",
                    currentChannelNumber
                )
            }
            updatePortColor(checked)
        }
        buttonShort?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            portStatus?.short = true
            updateMenuUI()
            runBlocking {
                CalibrationHelper.getPortMeasure(
                    selectedPort,
                    "S",
                    currentChannelNumber
                )
            }
            updatePortColor(checked)
        }
        buttonLoad?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val portStatus = mapOfPortsStatuses.get(checked)
            portStatus?.load = true
            updateMenuUI()
            runBlocking {
                CalibrationHelper.getPortMeasure(
                    selectedPort,
                    "L",
                    currentChannelNumber
                )
            }
            updatePortColor(checked)
        }
        buttonThru?.setOnClickListener {
            findCheckedPort()
            val checked = selectedPort - 1
            val index = listCalibration.indexOf(checked)
            val nextPort = listCalibration[index + 1]
            val portStatus = mapOfPortsStatuses[checked]
            portStatus?.thru = true
            updateMenuUI()
            runBlocking {
                CalibrationHelper.getPortMeasureThru(
                    selectedPort,
                    nextPort,
                    currentChannelNumber
                )
            }
            updatePortColor(checked)
        }
        buttonApply?.setOnClickListener {
            if (checkApply()) {
                runBlocking { CalibrationHelper.getApply(currentChannelNumber) }
                managerAR.layoutView.destroyView()
                managerAR.showLayout(R.layout.menu_ar)
                CoroutineScope(Dispatchers.IO).launch {
                    waitForLayout(1)
                }
            } else
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
        runBlocking {
            portArray = CalibrationHelper.getPortStatus(selectedPort, currentChannelNumber)!!
        }
    }


    // Запрашивает у сервера число активных портов в текущем канале
    fun getChannelPorts() {
        runBlocking { listCalibration = CalibrationHelper.getPortList(currentChannelNumber)!! }
        listCalibration = try {
            listCalibration.sorted() as MutableList<Int>
        } catch (exception: Throwable) {
            mutableListOf<Int>()
        }

        Log.e("TAG", listCalibration.toString())
    }

    // Перед калибровкой заполняет карту значениями для каждого используемого порта
    fun fillPortStatus() {
        mapOfPortsStatuses.clear()
        listCalibration.forEach {
            mapOfPortsStatuses[it] =
                PortCalibrationStatus(open = false, short = false, load = false, thru = false)
        }
        mapOfPortsStatuses[listCalibration[listCalibration.size - 1]]!!.thru = true
    }

    // Меняет цвета портов на заданный цвет и обновляет их
    fun changeChannelPorts(color: Int) {
        getChannelPorts()
        val listPort = managerAR.portList
        listPort.forEach {
            it.statustTo(false)
            it.changePortColor(Color.GRAY, true)
            it.changeActive(false)
        }
        listCalibration.forEach {
            listPort[it].changePortColor(color, true)
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

    fun updatePortColor(id: Int) {
        val portStatus = mapOfPortsStatuses[id]
        if (portStatus != null) {
            if (portStatus.short && portStatus.load && portStatus.load && portStatus.thru) {
                managerAR.portList[id].changePortColor(Color.rgb(0, 244, 0), true)
            }
        }
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
        var channelAmount: Int = 8
        runBlocking { channelAmount = CalibrationHelper.getChannelCount() }
        Log.e("TAG", "Channel_amount: $channelAmount")
        return channelAmount
    }

    // Заполняет список каналов элементами
    fun uploadRV() {
        itemsChannel.clear()
        if (!first)
            channelsCount = getChannelCount()
        for (i in 1..channelsCount)
            itemsChannel.add(ItemChannel(id = i, name = "Ch $i", isActive = false))
        adapter.setData(itemsChannel)
        val channelRV = this.findViewById<RecyclerView>(R.id.channelRV)
        channelRV.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        channelRV.adapter = adapter
    }

    // Спрятать или показать список (false/true)
    fun hideRV(hide: Boolean) {
        val channelRV = this.findViewById<RecyclerView>(R.id.channelRV)
        if (hide)
            channelRV.visibility = View.GONE
        else
            channelRV.visibility = View.VISIBLE
    }

    private fun onChannelChanged() {
        val channelNumberView = this.findViewById<TextView>(R.id.channelNumber)
        channelNumberView.visibility = View.VISIBLE
        channelNumberView.text = "Channel $currentChannelNumber"
        menuBind()
    }

}