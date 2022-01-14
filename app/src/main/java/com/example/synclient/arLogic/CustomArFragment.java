package com.example.synclient.arLogic;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.gorisse.thomas.sceneform.ArSceneViewKt;
import com.gorisse.thomas.sceneform.light.LightEstimationConfig;


public class CustomArFragment extends ArFragment {
    @Override
    protected Config onCreateSessionConfig(Session session) {
        Config config = session.getConfig();
        LightEstimationConfig lightEstimationConfig = getArSceneView() != null ?
                ArSceneViewKt.getLightEstimationConfig(getArSceneView()) : null;
        if (lightEstimationConfig != null) {
            config.setLightEstimationMode(lightEstimationConfig.getMode());
        }
        config.setAugmentedImageDatabase(null);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
        config.setFocusMode(Config.FocusMode.AUTO);
        return config;
    }
}
