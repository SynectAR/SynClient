package com.example.synclient.connection

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import java.io.DataInputStream
import java.net.Socket

class SocketConnect(address: String, port: Int) {
    private val socket: Socket = Socket(address, port)

    fun closeSocket(): Boolean {
        socket.close()
        return true
    }

    fun getBitmap(): Bitmap {
        var bytesArray = getMessageByCustomProtocol(socket)
        return convertToBitmap(bytesArray)
    }

    fun getMessageByParam(param: String): String? {
        sendMessage(param)
        return getMessage()
    }

    private fun sendMessage(message: String): Boolean {
        try {
            socket.outputStream.write(message.toByteArray())
        } catch (exception: Throwable) {
            Log.e("TAG", exception.toString())
            return false
        }
        return true
    }

    private fun getMessage(): String? {
        return try {
            val inputStream = DataInputStream(socket.getInputStream())
            inputStream.readLine()
        } catch (exception: Throwable) {
            Log.e("TAG", exception.toString())
            null
        }
    }

    private fun getMessageByCustomProtocol(socket: Socket): ByteArray {
        val inputStream = DataInputStream(socket.getInputStream())
        val messageMetadata = ByteArray(4)
        inputStream.read(messageMetadata)
        val messageSize = convertBytesToInt(messageMetadata)
        val imageData = ByteArray(messageSize)
        inputStream.read(imageData)
        inputStream.close()
        return imageData
    }

    private fun convertToBitmap(bytes: ByteArray): Bitmap {
        val pictureSize = bytes.size / 4
        val pixels: Int = kotlin.math.sqrt(pictureSize.toDouble()).toInt()
        val resultBitmap = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888)
        for (j in 0 until pixels)
            for (i in 0 until pixels) {
                val index = i * pixels + j
                resultBitmap.setPixel(
                    i, j, Color.rgb(
                        convertByteToInt(bytes, index * 4 + 2),
                        convertByteToInt(bytes, index * 4 + 1),
                        convertByteToInt(bytes, index * 4 + 3)
                    )
                )
            }
        return resultBitmap
    }

    private fun convertBytesToInt(bytes: ByteArray): Int {
        return (bytes[3].toInt() shl 24) or
                (bytes[2].toInt() and 0xff shl 16) or
                (bytes[1].toInt() and 0xff shl 8) or
                (bytes[0].toInt() and 0xff)
    }

    private fun convertByteToInt(bytes: ByteArray, shift: Int): Int {
        return (bytes[0 + shift].toInt() and 0xff)
    }

}