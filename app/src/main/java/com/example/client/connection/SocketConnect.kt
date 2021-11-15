package com.example.client.connection

import android.graphics.Bitmap
import android.graphics.Color
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.net.Socket;

public class SocketConnect {

    fun createSocket(address: String, port: Int): Socket {
        return Socket(address, port)
    }
    fun closeSocket(socket: Socket){
        socket.close()
    }
    fun getBitmap(socket:Socket): Bitmap{
        var bytesArray = getMessageByCustomProtocol(socket)
        return convertToBitmap(bytesArray)
    }
    
    private fun getMessageByCustomProtocol(socket: Socket):ByteArray{
        val `in` = DataInputStream(BufferedInputStream(socket.getInputStream()))
        val messageMetadata = ByteArray(4)
        `in`.read(messageMetadata)
        val messageSize = convert4byteToInt(messageMetadata,0)
        val messageFromServer = ByteArray(messageSize)
        `in`.read(messageFromServer)
        `in`.close()
        return messageFromServer
    }

    private fun convertToBitmap(bytes: ByteArray): Bitmap {
        val pictureSize = bytes.size / 4
        val pixels: Int = kotlin.math.sqrt(pictureSize.toDouble()).toInt()
        val resultBitmap = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888)
        for(j in 0 until pixels)
            for(i in 0 until pixels){
                val index = i * pixels + j
                resultBitmap.setPixel(i,j, Color.rgb(convertByteToInt(bytes,index*4+2),
                    convertByteToInt(bytes,index*4+1),convertByteToInt(bytes,index*4+3)))
            }
        return resultBitmap
    }

    private fun convert4byteToInt(bytes: ByteArray, shift: Int): Int {
        return (bytes[3].toInt() shl 24) or
                (bytes[2].toInt() and 0xff shl 16) or
                (bytes[1].toInt() and 0xff shl 8) or
                (bytes[0].toInt() and 0xff)
    }

    private fun convertByteToInt(bytes: ByteArray, shift: Int): Int {
        return (bytes[0+shift].toInt() and 0xff)
    }

}
