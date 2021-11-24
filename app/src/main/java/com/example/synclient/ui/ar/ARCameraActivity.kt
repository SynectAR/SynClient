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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)

        arFragment= (supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as CustomArFragment).apply{
            setOnSessionConfigurationListener { session, config ->
                if (config != null) {
                    val bitmapQR = BitmapFactory.decodeResource(resources, R.drawable.demo_img2)
                    val aid = AugmentedImageDatabase(session)
                    aid.addImage("qrCode", bitmapQR,0.02f)
                    config.augmentedImageDatabase = aid
                    config.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE
                }
                arSceneView.session?.configure(config)
                isTrue = true
            }
            setOnAugmentedImageUpdateListener {
                createAnchor()
            }

            setOnTapArPlaneListener{ hitResult, plane, motionEvent->
                isTrue = true
            }

        }





    }

    fun finishActivity(v:View)
    {
        this.finish()
    }

    private fun displayWidget(anchorNode: AnchorNode) {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable ->
                val nameView= TransformableNode(arFragment.transformationSystem)
                nameView.scaleController.minScale = 0.01f
                nameView.scaleController.maxScale = 0.02f
                var anchorUp: Vector3 = anchorNode.down
                nameView.setLookDirection(Vector3.down(),anchorUp)
                nameView.parent = anchorNode
                nameView.renderable=viewRenderable
                nameView.select()
            }
    }


    override fun onUpdate(frameTime: FrameTime?) {
    }

    private fun createAnchor(){
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        var anchor: Anchor
        images.forEach {
            if(it.trackingState == TrackingState.TRACKING){
                if(it.name.equals("qrCode")){
                    isFound = true
                    anchor = it.createAnchor(it.centerPose)
                    val anchorNode= AnchorNode(anchor)
                    anchorNode.parent = arFragment.arSceneView.scene
                    displayWidget(anchorNode)
                }
            }
        }
    }
}