package com.example.synclient.ui.ar

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.ManagerAR
import com.example.synclient.customAR.CustomArFragment
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ARCameraActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    var managerAR:ManagerAR= ManagerAR(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_camera)

        managerAR.arFragment= (supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as CustomArFragment).apply{
            setOnAugmentedImageUpdateListener {
                if(managerAR.isFound){
                    if(managerAR.anchor.trackingState == TrackingState.PAUSED){
                        managerAR.anchor.detach()
                        setOnAugmentedImageUpdateListener(null)
                    }
                }
                else{
                    managerAR.createAnchor()
                }
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var config = managerAR.arFragment.arSceneView.session?.config
                if(config!= null){
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode =Config.UpdateMode.LATEST_CAMERA_IMAGE
                    managerAR.arFragment.arSceneView.session?.configure(config)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            updateConfig()
        }
    }

    private fun updateConfig()
    {
        while(managerAR.arFragment.arSceneView.session == null)
        {
            continue
        }
        handler.sendEmptyMessage(0)
    }

    fun finishActivity(v:View)
    {
        this.finish()
    }


}