package com.example.synclient.ui.ar



import android.app.Activity
import com.example.synclient.MainActivity
import com.google.ar.core.Config
import com.google.ar.core.Session

import com.google.ar.sceneform.ux.ArFragment

import java.lang.Exception


class CustomARFragment:ArFragment() {
    override fun onSessionConfigChanged(config: Config?) {
        super.onSessionConfigChanged(config)
        config?.focusMode=Config.FocusMode.AUTO
        config?.planeFindingMode=Config.PlaneFindingMode.DISABLED
        config?.updateMode=Config.UpdateMode.LATEST_CAMERA_IMAGE

        /*
        try {
            activity as MainActivity
            activity.setupAugmentedImagesDB()
        }
        catch (e:Exception)
        {

        }

         */


    }

    override fun onCreateSessionConfig(session: Session?): Config {
        return super.onCreateSessionConfig(session)
        session?.config?.focusMode=Config.FocusMode.AUTO
        session?.config?.planeFindingMode=Config.PlaneFindingMode.DISABLED
        session?.config?.updateMode= Config.UpdateMode.LATEST_CAMERA_IMAGE


    }

}

