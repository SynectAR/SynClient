package com.example.synclient.arLogic

import android.content.Context
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
class ManagerAR constructor(context: Context) {
    //Созданием пустого массива координат, хранящихся в типе float.
    var portsCoords = Array(3) { Array(3) { 0.0f } }

    //Переменная, содержащая стандартный отступ для всех виджетов от найденной поверхности.
    val yaxisBase: Float = 0.006f //0.01f

    //Переменная, использующая для подтверждения нахождения изображения.
    var isFound = false
    lateinit var anchor: Anchor
    lateinit var arFragment: ArFragment
    var myContext: Context = context


    //Лист, содержащий в себе все PortAR обьекты.
    var portList: MutableList<PortAR> = mutableListOf()
    var port: PortAR = PortAR()
    var widget: WidgetAR = WidgetAR()
    var menu: CalibrationMenuAR = CalibrationMenuAR()


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
                    port.createPort(
                        arFragment,
                        anchor,
                        0,
                        myContext,
                        portsCoords[0][0],
                        portsCoords[0][1],
                        portsCoords[0][2]
                    )
                    portList.toMutableList().add(port)
                    port.createPort(
                        arFragment,
                        anchor,
                        1,
                        myContext,
                        portsCoords[1][0],
                        portsCoords[1][1],
                        portsCoords[1][2]
                    )
                    portList.toMutableList().add(port)
                    break
                }
            }
        }
    }

}