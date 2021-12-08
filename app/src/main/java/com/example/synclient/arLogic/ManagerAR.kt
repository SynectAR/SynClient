package com.example.synclient.arLogic

import android.content.Context
import com.example.synclient.R
import com.example.synclient.ui.ar.ARCameraActivity
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment


/**
 * Класс ManagerAR
 *
 * Класс для работы и хранения всех виджетов и обьектов для работы с ARCore.
 * @param context Конекст основной активити.
 * @constructor Создает экземпляр класса с передаваемым в него контекстом.
 */
class ManagerAR constructor(context: Context, activity: ARCameraActivity) {
    //Созданием пустого массива координат, хранящихся в типе float.
    var portsCoords = Array(3) { Array(3) { 0.0f } }

    //Переменная, содержащая стандартный отступ для всех виджетов от найденной поверхности.
    val yaxisBase: Float = 0.006f //0.01f

    //Переменная, использующая для подтверждения нахождения изображения.
    var isFound = false
    lateinit var anchor: Anchor
    lateinit var arFragment: ArFragment
    var myContext: Context = context
    var activity: ARCameraActivity = activity


    //Лист, содержащий в себе все PortAR обьекты.
    var portList: MutableList<PortViewBuilder> = mutableListOf()
    var port: PortViewBuilder = PortViewBuilder()
    var widget: DeviceInfoViewBuilder = DeviceInfoViewBuilder()
    var menu: CalibrationMenuViewBuilder = CalibrationMenuViewBuilder()


    /**
     * Метод для поиска изображения и создания anchor в ее центре.
     * Также отвечает за создание виджетов в AR.
     */
    fun createAnchor() {
        val frame = arFragment.arSceneView.arFrame
        portsCoords[0] = arrayOf(-0.02f, yaxisBase, -0.005f)
        portsCoords[1] = arrayOf(0.02f, yaxisBase, -0.005f)
        portsCoords[2] = arrayOf(0f, yaxisBase, 0f)

        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for (image in images) {
            if (image.trackingState == TrackingState.TRACKING
                && image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
            ) {
                if (image.name.equals("qrCode")) {
                    isFound = true
                    anchor = image.createAnchor(image.centerPose)
                    widget.createWidget(arFragment, anchor, myContext)
                    menu.createMenu(arFragment, anchor, myContext, -0.008f, yaxisBase, -0.061f)
                    portsCoords.forEachIndexed { index, element ->
                        port.createPort(
                            arFragment,
                            anchor,
                            index + 1,
                            myContext,
                            element[0],
                            element[1],
                            element[3]
                        )
                        portList.add(port)
                    }
                    break
                }
            }
        }
    }

    fun setAugmentedImagesOnUpdateListener() {
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
                        createAnchor()
                    }
                }
            }
    }


}