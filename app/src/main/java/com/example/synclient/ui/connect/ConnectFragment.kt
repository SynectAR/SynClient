package com.example.synclient.ui.connect

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.synclient.R


class ConnectFragment : Fragment() {
    private var isConnected: Boolean = false
    private lateinit var handler: Handler
    private lateinit var bitmap: Bitmap
    private lateinit var deviceInfo: String
    private var port = 8000
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onStart() {
        super.onStart()
    }
}
