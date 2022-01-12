package com.example.synclient.arLogic

import android.content.Context
import android.util.Log
import com.example.synclient.R
import com.example.synclient.ui.ar.ARCameraActivity
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import kotlin.collections.MutableList as MutableList1


/**
 * Класс ManagerAR
 *
 * Класс для работы и хранения всех виджетов и обьектов для работы с ARCore.
 * @param context Конекст основной активити.
 * @constructor Создает экземпляр класса с передаваемым в него контекстом.
 */
class ManagerAR constructor(context: Context, activity: ARCameraActivity) {
    //Созданием пустого массива координат, хранящихся в типе float.
    var portsTransport = Array(3) { Array(3) { 0.0f } }
    var menuVector = Vector3(-0.1f, 0f, 0f)
    val menuQuaternion: Quaternion = Quaternion.axisAngle(Vector3(-90f, 0f, 0f), 1f)

    //Переменная, содержащая стандартный отступ для всех виджетов от найденной поверхности.
    private val yaxisBase: Float = 0.006f //0.01f

    //Переменная, использующая для подтверждения нахождения изображения.
    var isFound = false
    var isCreated = false
    lateinit var anchor: Anchor
    lateinit var arFragment: ArFragment
    var myContext: Context = context
    var indexLastChangedPort = 0
    private var activity: ARCameraActivity = activity


    //Лист, содержащий в себе все PortAR обьекты.
    var portList = mutableListOf<PortViewBuilder>()
    var layoutView: LayoutViewBuilder = LayoutViewBuilder()
    var lineList = mutableListOf<LineViewBuilder>()

    /**
     * Метод для поиска изображения и создания anchor в ее центре.
     * Также отвечает за создание виджетов в AR.
     */
    private fun createAnchor(
        listOfVectors: MutableList1<Vector3>,
        listOfQuaternion: MutableList1<Quaternion>
    ) {
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for (image in images) {
            if (image.trackingState == TrackingState.TRACKING
                && image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
            ) {
                if (image.name.equals("qrCode")) {
                    anchor = image.createAnchor(image.centerPose)
                    isFound = true
                    showLayout(R.layout.menu_ar)
                    listOfVectors.forEachIndexed { index, vector ->
                        var port: PortViewBuilder = PortViewBuilder()
                        port.createPort(
                            arFragment,
                            anchor,
                            index + 1,
                            myContext,
                            vector,
                            listOfQuaternion[index]
                        )
                        portList.add(port)
                    }
                    isCreated = true
                    break
                }
            }
        }
    }

    fun setAugmentedImagesOnUpdateListener(
        listOfVectors: MutableList1<Vector3>,
        listOfQuaternion: MutableList1<Quaternion>
    ) {
        arFragment =
            (activity.supportFragmentManager.findFragmentById(R.id.scene_form_fragment)
                    as CustomArFragment).apply {
                setOnAugmentedImageUpdateListener {
                    if (isFound) {
                        if (anchor.trackingState == TrackingState.PAUSED) {
                            anchor.detach()
                            setOnAugmentedImageUpdateListener(null)
                        }
                    } else {
                        createAnchor(listOfVectors, listOfQuaternion)
                    }
                }
            }
    }

    fun changePortsColor(color: Int) {
        portList.forEach {
            it.changePortColor(color, true)
        }
    }

    fun showLayout(layoutType: Int) {
        layoutView = LayoutViewBuilder().apply {
            createView(arFragment, anchor, myContext, layoutType, menuVector, menuQuaternion)
        }
    }

    fun refreshPorts() {
        portList.forEach {
            it.changePortStatus()
            it.changePortStatus()
        }
    }

    fun getSessionConfigurated(): Config {
        var session = arFragment.arSceneView.session
        var config = Config(session)
        config.augmentedImageDatabase = null
        return config
    }

    fun returnColor() {
        portList[indexLastChangedPort].changePortColor(
            portList[indexLastChangedPort].portColor,
            false
        )
    }


    fun createLine(point1: Vector3, point2: Vector3) {
        if(lineList.size > 0)
            lineList[lineList.size-1].destroyView()
        var line = LineViewBuilder()
        line.createLine(
            arFragment,
            anchor,
            myContext,
            point1,
            point2
        )
        lineList.add(line)
    }


}