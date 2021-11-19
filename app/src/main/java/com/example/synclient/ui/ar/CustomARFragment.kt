package com.example.synclient.ui.ar


import com.google.ar.core.Config

import com.google.ar.sceneform.ux.ArFragment

class CustomARFragment:ArFragment() {
    override fun onSessionConfigChanged(config: Config?) {
        super.onSessionConfigChanged(config)
        config?.focusMode=Config.FocusMode.AUTO
        config?.planeFindingMode=Config.PlaneFindingMode.DISABLED
    }

}

