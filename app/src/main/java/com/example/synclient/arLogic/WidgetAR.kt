package com.example.synclient.arLogic

import android.content.Context
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class WidgetAR {

    public fun createWidget(arFragment: ArFragment, anchor: Anchor, context: Context)
    {
        ViewRenderable.builder().setView(context, R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable -> displayWidget(arFragment,anchor,viewRenderable)
            }

    }

    private fun displayWidget(arFragment: ArFragment,anchor: Anchor,viewRenderable: ViewRenderable)
    {
        var anchorNode: AnchorNode = AnchorNode(anchor)

        var node: TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f

        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}