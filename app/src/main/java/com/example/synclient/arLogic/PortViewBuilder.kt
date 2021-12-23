package com.example.synclient.arLogic

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


/**
 * Класс PortViewBuilder
 *
 * Класс для создания и отображения обьекта порта устройства.
 */
class PortViewBuilder {
    lateinit var view: View
    lateinit var context: Context
    lateinit var infoView: View
    var isChecked = false

    /**
     * Метод для создания и отображения порта устройства.
     *
     * Метод создает обьект viewRenderable с заранее заданным port_layout.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param portNumber Номер порта.
     * @param context Контекст активити работы с камерой.
     * @param x Координата оси x.
     * @param y Координата оси y.
     * @param z Координата оси z.
     *
     */
    fun createPort(
        arFragment: ArFragment,
        anchor: Anchor,
        portNumber: Int,
        context: Context,
        vector: Vector3,
        quaternion: Quaternion
    ) {
        this.context = context
        ViewRenderable.builder().setView(context, R.layout.port_layout)
            .build()
            .thenAccept {
                displayPort(arFragment, anchor, it, portNumber, vector, quaternion)
            }
    }

    /**
     * Метод для отображения и сдвига обьекта порта устройства.
     *
     * Метод принимает viewRenderable и передвигает виджет по отношению к anchor.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param viewRenderable Экземпляр ViewRenderable для отображения виджета из ARCore.
     * @param portNumber Номер порта.
     * @param x Координата оси x.
     * @param y Координата оси y.
     * @param z Координата оси z.
     *
     */
    private fun displayPort(
        arFragment: ArFragment,
        anchor: Anchor,
        viewRenderable: ViewRenderable,
        portNumber: Int,
        vector: Vector3,
        quaternion: Quaternion
    ) {
        var anchorNode = AnchorNode(anchor)
        var node = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.025f
        node.scaleController.maxScale = 0.03f

        //Корректирует расположение виджета в зависимости от найденного anchor.
        node.worldPosition = vector
        node.worldRotation = quaternion
        node.parent = anchorNode
        node.renderable = viewRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        //Находишь необходимый TextView для отоброжения номера порта.
        view = viewRenderable.view
        val portRadio: RadioButton = view.findViewById<RadioButton>(R.id.portChecked)
        var portText = view.findViewById<TextView>(R.id.portNumberText)
        portText.text = "Port $portNumber"
        val viewPort = view.findViewById<View>(R.id.portView)
        portRadio.setOnClickListener {
            isChecked = !isChecked
            portRadio.isChecked = isChecked
        }
    }

    fun changePortColor(color: Int) {
        val portRadio: RadioButton = view.findViewById<RadioButton>(R.id.portChecked)
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                Color.RED,  // disabled
                color // enabled
            )
        )
        portRadio.buttonTintList = colorStateList
    }

    fun changePortText(text: String) {
        var portText = view.findViewById<TextView>(R.id.portNumberText)
        portText.text = text
    }

    fun showPortInfo(text: String) {

    }

    private fun displayPortInfo(
        arFragment: ArFragment,
        anchor: Anchor,
        viewRenderable: ViewRenderable,
        vector: Vector3,
        quaternion: Quaternion
    ) {
        var anchorNode = AnchorNode(anchor)
        var node = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.025f
        node.scaleController.maxScale = 0.03f
        vector.y += 0.01f
        node.worldPosition = vector
        node.worldRotation = quaternion
        node.parent = anchorNode
        node.renderable = viewRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)

        infoView = viewRenderable.view
        val infoEditText = infoView.findViewById<TextView>(R.id.portInfoEditText)
        infoEditText.setText(
            "1. Вкл выкл\n" +
                    "2. Стартовая и конечная\n" +
                    "3. Кол-во точек\n" +
                    "4. Тип стимула\n" +
                    "5. Частота пол. Фильтра\n" +
                    "6. Мощность \n" +
                    "7. Калиброван или нет"
        )
        infoView.setOnClickListener {
            node.parent = null
        }

    }
}