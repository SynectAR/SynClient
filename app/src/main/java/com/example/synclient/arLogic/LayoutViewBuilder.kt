package com.example.synclient.arLogic

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

/**
 * Класс CalibrationMenuViewBuilder
 *
 * Класс для создания и отображения обьекта меню помощи в калибровке.
 */
class LayoutViewBuilder {
    var view:View? = null
    private lateinit var anchorNode: AnchorNode
    lateinit var node: TransformableNode
    lateinit var scene: Scene
    var isCreated = false
    /**
     * Метод для создания и отображения  меню помощи в калибровке.
     *
     * Метод создает обьект viewRenderable с заранее заданным calibration_layout.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param context Контекст активити работы с камерой.
     * @param x Координата оси x.
     * @param y Координата оси y.
     * @param z Координата оси z.
     *
     */
    fun createView(
        arFragment: ArFragment,
        anchor: Anchor,
        context: Context,
        layout: Int,
        vector: Vector3,
        quaternion: Quaternion
    ) {
        ViewRenderable.builder().setView(context, layout)
            .build()
            .thenAccept { viewRenderable ->
                displayMenu(arFragment, anchor, viewRenderable, vector, quaternion)
            }

    }

    /**
     * Метод для отображения и сдвига обьекта меню помощи в калибровке.
     *
     * Метод принимает viewRenderable и передвигает виджет по отношению к anchor.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param viewRenderable Экземпляр ViewRenderable для отображения виджета из ARCore.
     * @param x Координата оси x.
     * @param y Координата оси y.
     * @param z Координата оси z.
     *
     */
    private fun displayMenu(
        arFragment: ArFragment,
        anchor: Anchor,
        viewRenderable: ViewRenderable,
        vector: Vector3,
        quaternion: Quaternion
    ) {
        anchorNode = AnchorNode(anchor)
        node = TransformableNode(arFragment.transformationSystem)
        scene =  arFragment.arSceneView.scene
        node.scaleController.minScale = 0.05f
        node.scaleController.maxScale = 0.051f
        //Корректирует расположение виджета в зависимости от найденного anchor.

        node.worldPosition = vector
        node.worldRotation = quaternion
        node.parent = anchorNode
        node.renderable = viewRenderable
        scene.addChild(anchorNode)
        node.select()
        view = viewRenderable.view
        isCreated = true
    }

    fun destroyView(){
       scene.removeChild(anchorNode)
       node.parent = null
    }
}