package com.example.synclient.arLogic

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class PortAR {

    public fun createPort(arFragment: ArFragment, anchor: Anchor, portNumber:Int,context: Context,x:Float,y:Float,z:Float)
    {
        ViewRenderable.builder().setView(context, R.layout.port_layout)
            .build()
            .thenAccept { viewRenderable -> displayPort(arFragment,anchor,viewRenderable,portNumber,x,y,z)
            }
    }

    private fun displayPort(arFragment: ArFragment,anchor: Anchor,viewRenderable: ViewRenderable,portNumber: Int,x:Float,y:Float,z:Float)
    {
        var anchorNode: AnchorNode = AnchorNode(anchor)

        var node: TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f

        node.worldPosition = Vector3(x,y,z)
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        val view: View = viewRenderable.view
        var PortText= view.findViewById<TextView>(R.id.portNumberText)
        PortText.text="Порт " + portNumber
    }
}