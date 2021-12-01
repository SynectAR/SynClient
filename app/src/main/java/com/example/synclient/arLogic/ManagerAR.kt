package com.example.synclient.arLogic

import android.content.Context
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment

class ManagerAR constructor(context: Context) {
    var portsCoords = Array(3,{Array(3,{0.0f})})
    val yaxisBase:Float=0.006f //0.01f
    var isFound = false
    lateinit var anchor: Anchor
    lateinit var arFragment: ArFragment
    var myContext: Context = context


    var portList:List<PortAR> = listOf()
    var port:PortAR= PortAR()
    var widget:WidgetAR = WidgetAR()
    var menu:CalibrationMenuAR = CalibrationMenuAR()



    public fun createAnchor()
    {
        val frame = arFragment.arSceneView.arFrame
        portsCoords[0]= arrayOf(-0.02f,yaxisBase,-0.005f)
        portsCoords[1]=arrayOf(0.02f,yaxisBase,-0.005f)
        portsCoords[2]= arrayOf(0f,yaxisBase,0f)

        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for(image in images){
            if(image.trackingState == TrackingState.TRACKING && image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING){
                if(image.name.equals("qrCode")){
                    isFound = true
                    anchor = image.createAnchor(image.centerPose)
                    widget.createWidget(arFragment,anchor,myContext)
                    menu.createMenu(arFragment,anchor,myContext,-0.008f,yaxisBase,-0.061f)
                    port.createPort(arFragment,anchor,0,myContext,portsCoords[0][0],portsCoords[0][1],portsCoords[0][2])
                    port.createPort(arFragment,anchor,1,myContext,portsCoords[1][0],portsCoords[1][1],portsCoords[1][2])
                    break
                }
            }
        }
    }

}