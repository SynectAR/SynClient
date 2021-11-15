package com.example.arcorelab.common.helpers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


public object CameraPermissionHelper {
    private val CAMERA_PERMISSION_CODE: Int = 0
    private val CAMERA_PERMISSION: String =  Manifest.permission.CAMERA

    public fun hasCameraPermission(activity: Activity?) : Boolean
    {
        return (ContextCompat.checkSelfPermission(activity!!, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)
    }
    fun requestCameraPermission(activity: Activity?)
    {
        ActivityCompat.requestPermissions(activity!!, arrayOf(CAMERA_PERMISSION),CAMERA_PERMISSION_CODE)
    }

    fun shouldShowRequestPermissionRationale(activity: Activity?): Boolean
    {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity!!,CAMERA_PERMISSION)
    }

    fun launchPermissionSettings(activity: Activity)
    {
        val intent = Intent()
        intent.action=Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data=Uri.fromParts("package",activity.packageName,null)
        /*
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        //Возможно косячная штука
        intent.setData(Uri.fromParts("package",activity!!.packageName,null))
         */
        activity.startActivity(intent)
    }
}
