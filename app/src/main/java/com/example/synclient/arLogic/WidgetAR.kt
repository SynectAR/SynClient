package com.example.synclient.arLogic

import android.content.Context
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

/**
 * Класс WidgetAR
 *
 * Класс для создания и отображения основного виджета с информацией об устройстве.
 */
class WidgetAR {
    /**
     * Метод для создания и отображения основного виджета.
     *
     * Метод создает обьект viewRenderable с заранее заданным ar_info_display_widget.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param context Контекст активити работы с камерой.
     *
     */
    fun createWidget(arFragment: ArFragment, anchor: Anchor, context: Context) {
        ViewRenderable.builder().setView(context, R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable ->
                displayWidget(arFragment, anchor, viewRenderable)
            }

    }

    /**
     * Метод для отображения и сдвига основного виджета.
     *
     * Метод принимает viewRenderable и передвигает виджет по отношению к anchor.
     *
     * @param arFragment Экземпляр arFragment из ARCore.
     * @param anchor Экземпляр Anchor из ARCore.
     * @param viewRenderable Экземпляр ViewRenderable для отображения виджета из ARCore.
     *
     */
    private fun displayWidget(
        arFragment: ArFragment,
        anchor: Anchor,
        viewRenderable: ViewRenderable
    ) {
        var anchorNode = AnchorNode(anchor)

        var node = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f
        //Переворачивает виджет для корректного отображения на вертикальной поверхности.
        node.setLookDirection(Vector3.down(), anchorNode.down)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}