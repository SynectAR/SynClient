package com.example.synclient.customAR;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.synclient.R;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.gorisse.thomas.sceneform.ArSceneViewKt;
import com.gorisse.thomas.sceneform.light.LightEstimationConfig;

public class CustomArFragment extends ArFragment {
    @Override
    protected Config onCreateSessionConfig(Session session) {
        Config config = new Config(session);
        LightEstimationConfig lightEstimationConfig = getArSceneView() != null ?
                ArSceneViewKt.getLightEstimationConfig(getArSceneView()) : null;
        if (lightEstimationConfig != null) {
            config.setLightEstimationMode(lightEstimationConfig.getMode());
        }
        config.setDepthMode(Config.DepthMode.DISABLED);
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
        config.setFocusMode(Config.FocusMode.AUTO);
        // Force the non-blocking mode for the session.
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        return config;
    }
}
