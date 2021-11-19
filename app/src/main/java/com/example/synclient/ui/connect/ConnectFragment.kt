package com.example.synclient.ui.connect

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.synclient.R
import com.example.synclient.connection.SocketConnect
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class ConnectFragment : Fragment() {
    private var isConnected: Boolean = false
    private lateinit var handler: Handler
    private lateinit var bitmap:Bitmap
    private val address= "192.168.1.187"
    private var port = 8000
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onStart() {
        super.onStart()
        val button = view?.findViewById(R.id.button_connect) as Button
        val addressText = view?.findViewById(R.id.ip_adress_view) as TextInputEditText
        button.setOnClickListener{
            if(!isConnected){
                button.text = "Disconnect"
                isConnected = true
                Toast.makeText(context,"Подключено",Toast.LENGTH_SHORT).show()
                CoroutineScope(IO).launch {
                }
            }
            else{
                isConnected = false;
                button.text = "Connect"
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun createConnection(address: String, port: Int){
        val connect = SocketConnect(address,port)
        bitmap = connect.getBitmap()
        connect.closeSocket()
    }

}
