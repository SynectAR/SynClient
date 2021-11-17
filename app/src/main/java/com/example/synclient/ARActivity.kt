package com.example.synclient

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.arcorelab.common.helpers.CameraPermissionHelper
import com.example.arcorelab.common.helpers.SessionManagerHelper
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ARActivity : AppCompatActivity(), View.OnClickListener {

    var userARSetupRequest: Boolean = true
    var mSession: Session? = null
    var mConfig: Config? = null
    val TAG: String = MainActivity::class.java.simpleName


    lateinit var arrayView: Array<ImageView>
    lateinit var bearRenderable: ModelRenderable
    lateinit var catRenderable: ModelRenderable
    lateinit var cowRenderable: ModelRenderable

    internal var selected= 1 //bear text

    lateinit var ArFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ar)


        setupArray()
        setupClickListener()
        setupModel()
        ArFragment = supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as ArFragment
        ArFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(ArFragment.arSceneView.scene)

            createModel(anchorNode, selected)


        }
    }

    private fun createModel(anchorNode: AnchorNode, selected: Int) {
        if(selected==1)
        {
            val bear= TransformableNode(ArFragment.transformationSystem)
            bear.setParent(anchorNode)
            bear.renderable= null
            bear.select()
            AddName(anchorNode,bear,"Текст1")
        }
        if(selected==2)
        {
            val cat= TransformableNode(ArFragment.transformationSystem)
            cat.setParent(anchorNode)
            cat.renderable= catRenderable
            cat.select()
            AddName(anchorNode,cat,"Текст2")
        }
        if(selected==3)
        {
            val cow= TransformableNode(ArFragment.transformationSystem)
            cow.setParent(anchorNode)
            cow.renderable= cowRenderable
            cow.select()
            AddName(anchorNode,cow,"Текст3")
        }

    }

    private fun AddName(anchorNode: AnchorNode, node: TransformableNode, name: String) {
        ViewRenderable.builder().setView(this,R.layout.ar_info_display_widget)
            .build()
            .thenAccept { viewRenderable ->
                val nameView= TransformableNode(ArFragment.transformationSystem)
                nameView.localPosition = Vector3(0f,node.localPosition.y+0.5f,0f) //0;0.5;0
                nameView.setParent(anchorNode)
                nameView.renderable=viewRenderable
                nameView.select()

                val textView=viewRenderable.view as View
                var text= textView.findViewById<TextView>(R.id.exampleText_id)
                text.text = name
                text.setOnClickListener{
                    anchorNode.setParent(null)
                }
            }
    }

    private fun setupModel() {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("models/sphere-gltf-example.glb"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept{modelRenderable -> bearRenderable=modelRenderable}
            .exceptionally { throwable ->
                Toast.makeText(this,"Unable to load bear model.",
                    Toast.LENGTH_SHORT).show()
                null
            }
        ModelRenderable.builder()
            .setSource(this, Uri.parse("models/pencil.glb"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept{modelRenderable -> catRenderable=modelRenderable}
            .exceptionally { throwable ->
                Toast.makeText(this,"Unable to load cat model.",
                    Toast.LENGTH_SHORT).show()
                null
            }
        ModelRenderable.builder()
            .setSource(this, Uri.parse("models/halloween.glb"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept{modelRenderable -> cowRenderable=modelRenderable}
            .exceptionally { throwable ->
                Toast.makeText(this,"Unable to load cow model.",
                    Toast.LENGTH_SHORT).show()
                null
            }


    }

    private fun setupClickListener() {
        for(i in arrayView.indices)
        {
            arrayView[i].setOnClickListener(this)
        }
    }



    private fun setupArray() {
        var bear=findViewById<ImageView>(R.id.bear)
        var cat=findViewById<ImageView>(R.id.cat)
        var cow=findViewById<ImageView>(R.id.cow)

        arrayView= arrayOf(bear,cat,cow)


    }

    override fun onResume() {
        super.onResume()
        //

        SessionCameraChecker()
    }

    override fun onPause() {
        super.onPause()
        SessionOnPause()


    }

    fun SessionCameraChecker()
    {
        if(!CameraPermissionHelper.hasCameraPermission(this))
        {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
        //
        try
        {
            if(mSession == null)
            {
                when(ArCoreApk.getInstance().requestInstall(this,userARSetupRequest))
                {
                    ArCoreApk.InstallStatus.INSTALLED ->
                    {
                        mSession= SessionManagerHelper.createSession(this)
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED ->
                    {
                        userARSetupRequest= false
                        return
                    }
                }
            }
        }
        catch(e: UnavailableUserDeclinedInstallationException)
        {
            Toast.makeText(this,"TODO: handle exception" + e, Toast.LENGTH_LONG).show()
            return
        }
    }

    fun SessionOnPause()
    {
        if (mSession != null) {
            /*
            displayRotationHelper.onPause()
            surfaceView.onPause()
             */
            mSession!!.pause()
        }
    }

    override fun onClick(view: View?) {
        if(view!!.id == R.id.bear)
        {
            selected=1
            mySetBackground(view!!.id)
        }
        else if(view!!.id == R.id.cat)
        {
            selected=2
            mySetBackground(view!!.id)
        }
        else if(view!!.id == R.id.cow)
        {
            selected=3
            mySetBackground(view!!.id)
        }


    }

    private fun mySetBackground(id: Int) {
        for(i in arrayView.indices)
        {
            if(arrayView[i].id==id)
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"))
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT)
        }

    }




}