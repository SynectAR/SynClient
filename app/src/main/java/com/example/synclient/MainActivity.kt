package com.example.synclient


import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.synclient.databinding.ActivityMainBinding
import com.example.synclient.calibrationHelper.CalibrationHelper
import kotlinx.coroutines.*



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)
    }

    public fun buttonGRPCClicked(v: View) {
        val view = findViewById<Button>(R.id.buttonGRPC)
        var buttonText: String? = "Не получил"
        runBlocking { buttonText = CalibrationHelper.sayHello("Получил") }
        view.text = buttonText
    }

}