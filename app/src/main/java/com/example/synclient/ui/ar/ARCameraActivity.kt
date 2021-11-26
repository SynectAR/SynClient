package com.example.synclient.ui.ar

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ARCameraActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    var isFound = false
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
                    createAnchor() }
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var config = arFragment.arSceneView.session?.config
                if(config!= null){
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE
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
                var anchorUp: Vector3 = anchorNode.up
                nameView.setLookDirection(Vector3.up(),anchorUp)
                nameView.worldRotation = Quaternion.axisAngle(Vector3(0f, 0f, 0f), -10f)
                nameView.select()
                val textView=viewRenderable.view as View
                var text= textView.findViewById<TextView>(R.id.exampleText_id)
                text.text = "CMT, C1209, 21.3.4/2"
                text.setOnClickListener{
                    nameView.removeChild(anchorNode)
                }
            }
    }

    private fun createAnchor(){
            val frame = arFragment.arSceneView.arFrame
            val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
                AugmentedImage::class.java
            )
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