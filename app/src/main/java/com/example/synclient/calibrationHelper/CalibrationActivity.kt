package com.example.synclient.calibrationHelper

import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.synclient.R
import com.example.synclient.arLogic.PortViewBuilder

import com.example.synclient.databinding.ActivityMainBinding
import com.example.synclient.calibrationHelper.CalibrationHelper
import kotlinx.coroutines.*
import javax.annotation.meta.When
import vnarpc.PortStatus


public class CalibrationActivity : AppCompatActivity() {
    var selectedPort: Int = -1
    var portArray: Array<Boolean> = arrayOf(false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        var radioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected
            if (checkedId == R.id.radioButtonPort1) selectedPort = 1
            if (checkedId == R.id.radioButtonPort2) selectedPort = 2
            if (checkedId == R.id.radioButtonPort3) selectedPort = 3
        }
    }

    public fun openOnClick(v: View) {
        runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "O") }
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        var view = findViewById<TextView>(R.id.textViewOPEN)
        view.text = portArray[0].toString()
        view = findViewById(R.id.textViewSHORT)
        view.text = portArray[1].toString()
        view = findViewById(R.id.textViewLOAD)
        view.text = portArray[2].toString()
        if (portArray[0] == true) {
            var view = findViewById<Button>(R.id.buttonOpen)
            view.isEnabled = false
        }
    }

    public fun shortOnClick(v: View) {
        runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "S") }
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        var view = findViewById<TextView>(R.id.textViewOPEN)
        view.text = portArray[0].toString()
        view = findViewById(R.id.textViewSHORT)
        view.text = portArray[1].toString()
        view = findViewById(R.id.textViewLOAD)
        view.text = portArray[2].toString()
        if (portArray[1] == true) {
            var view = findViewById<Button>(R.id.buttonShort)
            view.isEnabled = false
        }
    }

    public fun loadOnClick(v: View) {
        runBlocking { CalibrationHelper.getPortMeasure(selectedPort, "L") }
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        var view = findViewById<TextView>(R.id.textViewOPEN)
        view.text = portArray[0].toString()
        view = findViewById(R.id.textViewSHORT)
        view.text = portArray[1].toString()
        view = findViewById(R.id.textViewLOAD)
        view.text = portArray[2].toString()
        if (portArray[2] == true) {
            var view = findViewById<Button>(R.id.buttonLoad)
            view.isEnabled = false
        }
    }

    public fun thruOnClick(v: View) {
        runBlocking { CalibrationHelper.getPortMeasureThru(selectedPort, selectedPort + 1) }
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
    }

    public fun statusOnClick(v: View) {
        runBlocking { portArray = CalibrationHelper.getPortStatus(selectedPort)!! }
        var view = findViewById<TextView>(R.id.textViewOPEN)
        view.text = portArray[0].toString()
        view = findViewById(R.id.textViewSHORT)
        view.text = portArray[1].toString()
        view = findViewById(R.id.textViewLOAD)
        view.text = portArray[2].toString()
    }

}