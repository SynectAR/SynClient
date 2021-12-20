package com.example.synclient.calibrationHelper

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


object CalibrationHelper {
    val clientStub = GRPCConnectionHelper.setupConnection()

    suspend fun sayHello(name: String): String? {
        var receivedText: String? = null
        runBlocking {
            val responseText = async { clientStub.sayHello(name) }
            receivedText = responseText.await()
        }
        return receivedText
    }

    suspend fun getPortCount(): Int? {
        var receivedCount: Int? = null
        runBlocking {
            val responseCount = async { clientStub.portCount() }
            receivedCount = responseCount.await()
        }
        return receivedCount
    }

    suspend fun getPortStatus(port: Int): Boolean? {
        var receivedStatus: Boolean? = null
        runBlocking {
            val responseStatus = async { clientStub.portStatus(port) }
            receivedStatus = responseStatus.await()
        }
        return receivedStatus
    }

    suspend fun getPortMeasure(port: Int, type: String) {
        runBlocking {
            val responseMeasure = async { clientStub.measurePort(port, type) }
            //receivedStatus= responseStatus.await()
        }

    }

    suspend fun getPortMeasureThru(firstPort: Int, secondPort: Int) {
        runBlocking {
            val responseMeasure = async { clientStub.measureThru(firstPort, secondPort) }
            //receivedStatus= responseStatus.await()
        }

    }

    suspend fun getApply() {
        runBlocking {
            val responseMeasure = async { clientStub.apply() }
            //receivedStatus= responseStatus.await()
        }
    }

    suspend fun getReset() {
        runBlocking {
            val responseMeasure = async { clientStub.reset() }
            //receivedStatus= responseStatus.await()
        }
    }
}