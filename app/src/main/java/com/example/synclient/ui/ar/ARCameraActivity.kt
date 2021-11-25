package com.example.synclient.ui.ar

import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.synclient.R
import com.example.synclient.arWidget.CircleView
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.RotationController
import com.google.ar.sceneform.ux.TransformableNode

class ARCameraActivity : AppCompatActivity(), Scene.OnUpdateListener {
    private lateinit var arFragment: ArFragment
    var isTrue = false
    var isFound = false
    var isAdded= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)
        arFragment= (supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as CustomArFragment).apply{
            setOnSessionConfigurationListener { session, config ->
//                val bitmapQR = BitmapFactory.decodeResource(resources, R.drawable.demo_img2)
//                val aid = AugmentedImageDatabase(session)
//                aid.addImage("qrCode", bitmapQR,0.02f)
//                config.augmentedImageDatabase = aid
//                config.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE
//                config.planeFindingMode = Config.PlaneFindingMode.DISABLED
//                config.focusMode = Config.FocusMode.AUTO
            }
            setOnAugmentedImageUpdateListener {
                if(isFound==false) {
                    var config = arSceneView.session?.config
                    if (config != null) {
                        config.focusMode = Config.FocusMode.AUTO
                        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                        arSceneView.session?.configure(config)
                    }
                    if (isFound)
                        setOnAugmentedImageUpdateListener(null)
                    else
                        createAnchor()
                }
            }
        }
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
        var anchorNode:AnchorNode= AnchorNode(anchor)
        var node:TransformableNode= TransformableNode(arFragment.transformationSystem)
        node.scaleController.minScale = 0.01f
        node.scaleController.maxScale = 0.02f
        node.worldRotation = Quaternion.axisAngle(Vector3(0f, 0f, 0f), 0f)
        var anchorUp: Vector3 = anchorNode.down
        node.setLookDirection(Vector3.down(),anchorUp)
        node.renderable=viewRenderable
        node.parent=anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }


    override fun onUpdate(frameTime: FrameTime?) {
    }

    private fun createAnchor(){
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        var anchor: Anchor
        for(image in images){
            if(image.trackingState == TrackingState.TRACKING){
                if(image.name.equals("qrCode") && isFound==false){
                    anchor= image.createAnchor(image.centerPose)
                    displayWidget(arFragment,anchor)
                    isFound = true
                    break
                }
            }
        }
    }
}