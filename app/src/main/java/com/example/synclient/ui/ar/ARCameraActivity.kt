package com.example.synclient.ui.ar

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.mikhaellopez.circleview.CircleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ARCameraActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    var isFound = false
    lateinit var circle1: RadioButton
    lateinit var circle2: RadioButton
    lateinit var anchor: Anchor
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)
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

    private fun updateConfig() {
        while(arFragment.arSceneView.session == null){
            continue
        }
        handler.sendEmptyMessage(0)
    }

    fun finishActivity(v:View)
    {
        this.finish()
    }

    private fun displayWidget(arFragment: ArFragment,anchor: Anchor) {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable -> addWidgetToScene(arFragment,anchor,viewRenderable)
            }
    }

    private fun addWidgetToScene(arFragment: ArFragment,anchor: Anchor,viewRenderable: ViewRenderable)
    {
        var anchorNode:AnchorNode = AnchorNode(anchor)

        var node:TransformableNode = TransformableNode(arFragment.transformationSystem)

        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f
        node.worldPosition = Vector3(0.15f,0f,0.10f)
        node.worldRotation = Quaternion.axisAngle(Vector3(0f, 0f, 0f), -10f)
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable = viewRenderable
        node.parent = anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        val view = viewRenderable.view as View
        val circle1 = view.findViewById<RadioButton>(R.id.port1circleView)
        val circle2 = view.findViewById<RadioButton>(R.id.port2circleView)
    }

    private fun changeButton1(){
        if(circle1.isChecked)
            circle1.isChecked = false
        else
            circle1.isChecked = true
    }

    private fun changeButton2(){
        if(circle2.isChecked)
            circle2.isChecked = false
        else
            circle2.isChecked = true
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
                    break
                }
            }
        }
    }
}