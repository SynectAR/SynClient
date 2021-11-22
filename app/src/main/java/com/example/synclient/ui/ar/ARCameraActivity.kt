package com.example.synclient.ui.ar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.synclient.MainActivity
import com.example.synclient.R
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.PlaneFactory
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.IOException
import java.io.InputStream

class ARCameraActivity : AppCompatActivity(), View.OnClickListener, Scene.OnUpdateListener {
    var userARSetupRequest: Boolean = true
    var mSession: Session? = null
    var mConfig: Config? = null
    val TAG: String = MainActivity::class.java.simpleName



    internal var selected= 1 //bear text

    lateinit var arFragment: ArFragment
    //lateinit var arFragment:CustomARFragment
    var isAdded: Boolean= false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)


        arFragment= supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as ArFragment
        //arFragment.arSceneView.scene.addOnUpdateListener(this)
        arFragment.setOnTapArPlaneListener{ hitResult, plane, motionEvent->
            var config = arFragment.arSceneView.session?.config
            if (config != null) {
                config.focusMode = Config.FocusMode.AUTO
            }
            arFragment.arSceneView.session?.configure(config)
            arFragment.arSceneView.scene.addOnUpdateListener(this)

        }


//        if(savedInstanceState == null)
//        {
//            if(Sceneform.isSupported(this)){
//                supportFragmentManager.beginTransaction().
//                        add(R.id.scene_form_fragment,arFragment,null)
//                    .commit()
//            }
//        }
//
//        if(Sceneform.isSupported(this)){
//
//        }

        /*
        arFragment.setOnTapArPlaneListener{ hitResult, plane, motionEvent->
            var config = arFragment.arSceneView.session?.config
            if (config != null) {
                config.focusMode = Config.FocusMode.AUTO
            }
            arFragment.arSceneView.session?.configure(config)

            val anchor= hitResult.createAnchor()
            val anchorNode= AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            DisplayWidget(anchorNode,selected)
        }

         */
    }

    fun finishActivity(v:View)
    {
        this.finish()
    }


    private fun DisplayWidget(anchorNode: AnchorNode, selected: Int) {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable ->
                val nameView= TransformableNode(arFragment.transformationSystem)
                nameView.localPosition = Vector3(0f,0.5f,0f)
                nameView.setParent(anchorNode)
                nameView.renderable=viewRenderable
                nameView.select()

                val textView=viewRenderable.view as View
                var text= textView.findViewById<TextView>(R.id.exampleText_id)
                text.text = "ДОБАВЛЕННЫЙ ВИДЖЕТ"
                text.setOnClickListener{
                    anchorNode.setParent(null)
                }
            }

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onUpdate(frameTime: FrameTime?) {
        var frame: Frame? =arFragment.arSceneView.arFrame
        var augmentedImages: Collection<AugmentedImage> = frame!!.
        getUpdatedTrackables(AugmentedImage::class.java)
        augmentedImages.forEach{
            if(it.trackingState ==TrackingState.TRACKING)
            {
                if(it.name.equals("marker") && !isAdded)
                {
                    placeObject(arFragment,it.createAnchor(it.centerPose),R.raw.cube)
                    isAdded=true
                }
            }

        }

    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor?, uri:Int) {
        ModelRenderable.builder().setSource(arFragment.context,uri)
            .build()
            .thenAccept { modelRenderable -> addNoteToScene(arFragment,anchor,modelRenderable) }
            .exceptionally {
                    throwable -> Toast.makeText(arFragment.context,"Error:"+ throwable.message, Toast.LENGTH_LONG).show()
            null
            }

    }

    private fun addNoteToScene(arFragment: ArFragment, anchor: Anchor?, renderable: ModelRenderable?) {
        var anchorNode: AnchorNode = AnchorNode(anchor)
        var transformNode:TransformableNode = TransformableNode(arFragment.transformationSystem)
        transformNode.renderable=renderable
        transformNode.parent=anchorNode
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformNode.select()
    }

    public fun setupAugmentedImagesBD(arFragment: ArFragment): Boolean
    {
        var augmentedImageDB:AugmentedImageDatabase
        var bitmap: Bitmap? = loadAugmentedImage()
        if(bitmap ==null) return false
        augmentedImageDB= AugmentedImageDatabase(arFragment.arSceneView.session)
        augmentedImageDB.addImage("marker",bitmap)
        arFragment.arSceneView.session?.config?.setAugmentedImageDatabase(augmentedImageDB)
        return true
    }

    private fun loadAugmentedImage(): Bitmap? {
        var input:InputStream=assets.open("marker.img")
        try {
            var input:InputStream=assets.open("marker.img")
            return BitmapFactory.decodeStream(input)
        }
        catch (e:IOException)
        {
            Toast.makeText(this,"Unable to load Marker.img file",
                Toast.LENGTH_SHORT).show()
            return null
        }
    }


}