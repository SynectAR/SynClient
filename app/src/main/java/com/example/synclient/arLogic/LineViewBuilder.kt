package com.example.synclient.arLogic

import android.content.Context
import android.util.Log
import android.view.View
import com.example.synclient.R
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.TransformableNode


/**
 * Класс PortViewBuilder
 *
 * Класс для создания и отображения обьекта порта устройства.
 */
class LineViewBuilder {
    lateinit var view: View
    lateinit var context: Context

    fun createLine(
        arFragment: ArFragment,
        anchor: Anchor,
        context: Context,
        node1: TransformableNode,
        node2: TransformableNode
    ) {
        this.context = context

        val point1: Vector3 = node1.worldPosition
        val point2: Vector3 = node2.worldPosition

        val difference = Vector3.subtract(point1, point2)
        val directionFromTopToBottom = difference.normalized()
        val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())

        MaterialFactory.makeOpaqueWithColor(context, Color(0f,0f,0f)).thenAccept {
            val model = ShapeFactory.makeCube(
                Vector3(.005f, .005f, difference.length()),
                Vector3.zero(), it
            )

            var anchorNode = AnchorNode(anchor)
            var node = Node()

            //Корректирует расположение виджета в зависимости от найденного anchor.
            node.worldPosition = Vector3.add(Vector3.add(point1, point2),Vector3(0f,0.0f,0.0f))
            node.worldRotation = rotationFromAToB
            node.parent = anchorNode
            node.renderable = model
            arFragment.arSceneView.scene.addChild(anchorNode)
        }

    }
}


