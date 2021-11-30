package com.example.synclient.ui.ar

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.synclient.R
import com.example.synclient.arWidget.CircleView
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ARCameraActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    var isFound = false
    lateinit var anchor: Anchor
    private lateinit var handler: Handler
    lateinit var arView:View
    var portsCoords = Array(3,{Array(3,{0.0f})})
    val yaxisBase:Float=0.006f //0.01f
    lateinit var ButtonOpen:Button
    lateinit var ButtonShort:Button
    lateinit var ButtonLoad:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)
        portsCoords[0]= arrayOf(-0.02f,yaxisBase,-0.005f)
        portsCoords[1]=arrayOf(0.02f,yaxisBase,-0.005f)
        portsCoords[2]= arrayOf(0f,yaxisBase,0f)

        arFragment= (supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as CustomArFragment).apply{
            setOnAugmentedImageUpdateListener {
                if(isFound){
                    if(anchor.trackingState == TrackingState.PAUSED){
                        anchor.detach()
                        setOnAugmentedImageUpdateListener(null)
                    }
                }
                else{
                    createAnchor()
                }
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var config = arFragment.arSceneView.session?.config
                if(config!= null){
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode =Config.UpdateMode.LATEST_CAMERA_IMAGE
                    arFragment.arSceneView.session?.configure(config)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateConfig()
        }
    }

    private fun updateConfig()
    {
        while(arFragment.arSceneView.session == null)
        {
            continue
        }
        handler.sendEmptyMessage(0)
    }

    fun finishActivity(v:View)
    {
        this.finish()
    }

    private fun displayWidget(arFragment: ArFragment,anchor: Anchor)
    {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable -> addWidgetToScene(arFragment,anchor,viewRenderable)
            }
    }

    private fun displayPort(arFragment: ArFragment,anchor: Anchor,portNumber:Int)
    {
        ViewRenderable.builder().setView(this,R.layout.port_layout)
            .build()
            .thenAccept { viewRenderable -> addPortToScene(arFragment,anchor,viewRenderable,portNumber)
            }
    }

    private fun displayCalibrationMenu(arFragment: ArFragment,anchor: Anchor)
    {
        ViewRenderable.builder().setView(this,R.layout.calibration_layout)
            .build()
            .thenAccept { viewRenderable -> addCalibrationMenuToScene(arFragment,anchor,viewRenderable)
            }
    }

    private fun addWidgetToScene(arFragment: ArFragment,anchor: Anchor,viewRenderable: ViewRenderable)
    {
        var anchorNode:AnchorNode = AnchorNode(anchor)

        var node:TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f
        //node.worldRotation = Quaternion.axisAngle(Vector3(0f, 0f, 0f), -10f)
        //node.worldPosition= Vector3(0.05f,0f,0.05f)

        //node.worldPosition = Vector3(0f,0f,0f)
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun addPortToScene(arFragment: ArFragment,anchor: Anchor,viewRenderable: ViewRenderable,portNumber: Int)
    {
        var anchorNode:AnchorNode = AnchorNode(anchor)

        var node:TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f

        node.worldPosition = Vector3(portsCoords[portNumber][0],portsCoords[portNumber][1],portsCoords[portNumber][2])
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        val view:View= viewRenderable.view
        var PortText= view.findViewById<TextView>(R.id.portNumberText)
        PortText.text="Порт " + portNumber
    }

    private fun addCalibrationMenuToScene(arFragment: ArFragment, anchor: Anchor, viewRenderable: ViewRenderable?) {
        var anchorNode:AnchorNode = AnchorNode(anchor)

        var node:TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f
        node.worldPosition = Vector3(-0.008f,yaxisBase,-0.061f)
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        val view:View= viewRenderable!!.view
        ButtonLoad=view.findViewById(R.id.buttonLoad)
        ButtonShort=view.findViewById(R.id.buttonShort)
        ButtonOpen=view.findViewById(R.id.buttonOpen)
    }

    private fun createAnchor(){
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for(image in images){
            if(image.trackingState == TrackingState.TRACKING && image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING){
                if(image.name.equals("qrCode")){
                    isFound = true
                    anchor = image.createAnchor(image.centerPose)
                    displayWidget(arFragment,anchor)
                    displayPort(arFragment,anchor,0)
                    displayPort(arFragment,anchor,1)
                    displayCalibrationMenu(arFragment,anchor)
                    break
                }
            }
        }
    }


}