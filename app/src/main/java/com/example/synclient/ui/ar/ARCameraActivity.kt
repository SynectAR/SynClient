package com.example.synclient.ui.ar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.synclient.MainActivity
import com.example.synclient.R
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ARCameraActivity : AppCompatActivity(), View.OnClickListener {
    var userARSetupRequest: Boolean = true
    var mSession: Session? = null
    var mConfig: Config? = null
    val TAG: String = MainActivity::class.java.simpleName


    lateinit var arrayView: Array<ImageView>
    lateinit var bearRenderable: ModelRenderable
    lateinit var catRenderable: ModelRenderable
    lateinit var cowRenderable: ModelRenderable

    internal var selected= 1 //bear text

    lateinit var arFragment: ArFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)


        arFragment= supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as ArFragment

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

}