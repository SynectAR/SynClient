package com.example.synclient.ui.ar

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.synclient.MainActivity
import com.example.synclient.R
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.core.AugmentedImage





class ARCameraActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var arFragment: ArFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)


        arFragment= supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as CustomArFragment
        arFragment.setOnTapArPlaneListener{ hitResult, plane, motionEvent->
            val frame = arFragment.arSceneView.arFrame
            val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
                AugmentedImage::class.java
            )
            var anchor: Anchor
            images.forEach {
                if(it.trackingState == TrackingState.TRACKING){
                    if(it.name.equals("qrCode")){
                        anchor = it.createAnchor(it.centerPose)
                        val anchorNode= AnchorNode(anchor)
                        anchorNode.parent = arFragment.arSceneView.scene
                        DisplayWidget(anchorNode)
                    }
                }
            }
        }
    }

    fun finishActivity(v:View)
    {

        this.finish()
    }


    private fun DisplayWidget(anchorNode: AnchorNode) {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable ->
                val nameView= TransformableNode(arFragment.transformationSystem)
                nameView.localPosition = Vector3(0f,0f,0f)
                nameView.scaleController.minScale = 0.01f
                nameView.scaleController.maxScale = 0.02f
                nameView.setParent(anchorNode)
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

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}