package com.example.synclient.ui.ar

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.synclient.R
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
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
//                val bitmapQR = BitmapFactory.decodeResource(resources, R.drawable.demo_img2)
//                val aid = AugmentedImageDatabase(session)
//                aid.addImage("qrCode", bitmapQR,0.02f)
//                config.augmentedImageDatabase = aid
//                config.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE
//                config.planeFindingMode = Config.PlaneFindingMode.DISABLED
//                config.focusMode = Config.FocusMode.AUTO
            }
            setOnAugmentedImageUpdateListener {
                var config = arSceneView.session?.config
                if(config!= null){
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE
                    arSceneView.session?.configure(config)
                }
                if(isFound)
                    setOnAugmentedImageUpdateListener(null)
                else
                    createAnchor()
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
                nameView.localPosition = Vector3(0f,0f,0f)
                nameView.scaleController.minScale = 0.01f
                nameView.scaleController.maxScale = 0.02f
                nameView.parent = anchorNode
                nameView.renderable=viewRenderable
                nameView.select()
                val textView=viewRenderable.view as View
                var text= textView.findViewById<TextView>(R.id.exampleText_id)
                text.text = "CMT, C1209, 21.3.4/2"
                text.setOnClickListener{
                    text.text =
                        "Pressed"
                }
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
            for(image in images){
                if(image.trackingState == TrackingState.TRACKING){
                    if(image.name.equals("qrCode")){
                        isFound = true
                        anchor = image.createAnchor(image.centerPose)
                        val anchorNode= AnchorNode(anchor)
                        anchorNode.parent = arFragment.arSceneView.scene
                        displayWidget(anchorNode)
                        break
                    }
                }
            }
    }
}