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
        CoroutineScope(Dispatchers.IO).launch {
            textRe= sayHelloFromClient()
        }
        val view= findViewById<Button>(R.id.buttonGRPC)
        view?.text=textRe
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
            .forAddress("127.0.0.1", port)
            .usePlaintext()
            .build()
        val client = GRPCClient(channel)
        return client
    }


}