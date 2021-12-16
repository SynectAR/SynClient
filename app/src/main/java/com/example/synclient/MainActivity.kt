package com.example.synclient


import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.synclient.databinding.ActivityMainBinding
import com.example.synclient.grpcFlow.GRPCClient
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import io.grpc.ClientCall
import org.bouncycastle.its.asn1.EndEntityType

import org.bouncycastle.its.asn1.EndEntityType.app





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

    public fun buttonGRPCClicked(v: View)
    {
        var textRe: String = "Не получил."
        var  responseText: String = "test"
        val client= createChannelForClient()
            CoroutineScope(Dispatchers.IO).launch {
                responseText= client.sayHello("KEKW")
            }
        val view= findViewById<Button>(R.id.buttonGRPC)
        view?.text=responseText
    }

    suspend fun sayHelloFromClient(): String
    {
        val client= createChannelForClient()
        var text= client.sayHello("kekw")
        return text
    }

    fun createChannelForClient() : GRPCClient
    {
        val port = System.getenv("PORT")?.toInt() ?: 50051

        val channel = ManagedChannelBuilder
            .forAddress("10.0.2.2", port)
            .usePlaintext()
            .build()
        val client = GRPCClient(channel)
        return client
    }


}