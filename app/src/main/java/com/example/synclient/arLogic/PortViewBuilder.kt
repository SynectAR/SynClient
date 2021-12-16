package com.example.synclient.arLogic

import android.content.Context
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
        ViewRenderable.builder().setView(context, R.layout.port_layout)
            .build()
            .thenAccept { viewRenderable ->
                displayPort(arFragment, anchor, viewRenderable, portNumber, vector, quaternion)
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


        //node.worldScale = Vector3(1f,1f,1f)
        //node.parent = anchorNode


        //node.worldScale = Vector3(222f,222f,222f)
        node.parent = anchorNode
        node.renderable = viewRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
        //Находишь необходимый TextView для отоброжения номера порта.
        val view: View = viewRenderable.view
        val portRadio: RadioButton = view.findViewById<RadioButton>(R.id.portChecked)
        var PortText = view.findViewById<TextView>(R.id.portNumberText)
        PortText.text = "Порт " + portNumber
        view.setOnClickListener {
            portRadio.isChecked = !portRadio.isChecked
        }
    }
}