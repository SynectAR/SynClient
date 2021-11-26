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
        Config config = session.getConfig();
        LightEstimationConfig lightEstimationConfig = getArSceneView() != null ?
                ArSceneViewKt.getLightEstimationConfig(getArSceneView()) : null;
        if (lightEstimationConfig != null) {
            config.setLightEstimationMode(lightEstimationConfig.getMode());
        }
        // Конфиги для изображения
        Bitmap bitmapQR = BitmapFactory.decodeResource(getResources(), R.drawable.demo_img2);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("qrCode", bitmapQR,0.015f);
        config.setAugmentedImageDatabase(aid);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
        config.setFocusMode(Config.FocusMode.AUTO);


        return config;
    }

}