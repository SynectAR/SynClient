package com.example.synclient.calibrationHelper

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import vnarpc.SweepType
import vnarpc.sweep_type


object CalibrationHelper {
    val clientStub = GRPCConnectionHelper.setupConnection()

    suspend fun getConnectionStatus(): String? {
        var receivedStatus: String? = null
        runBlocking {
            val responseStatus = async { clientStub.isConnected() }
            receivedStatus = responseStatus.await()
        }
        return receivedStatus
    }

    suspend fun getPortCount(): Int? {
        var receivedCount: Int? = null
        runBlocking {
            val responseCount = async { clientStub.portCount() }
            receivedCount = responseCount.await()
        }
        return receivedCount
    }

    suspend fun getPortStatus(port: Int,channel: Int): Array<Boolean>? {
        var receivedStatus: Array<Boolean>? = null
        runBlocking {
            val responseStatus = async { clientStub.portStatus(port,channel) }
            receivedStatus = responseStatus.await()
        }
        return receivedStatus
    }

    suspend fun getPortMeasure(port: Int, type: String,channel: Int) {
        runBlocking {
            val responseMeasure = async { clientStub.measurePort(port, type, channel) }
            //receivedStatus= responseStatus.await()
        }

    }

    suspend fun getPortMeasureThru(firstPort: Int, secondPort: Int,channel: Int) {
        runBlocking {
            val responseMeasure = async { clientStub.measureThru(firstPort, secondPort,channel) }
            //receivedStatus= responseStatus.await()
        }

    }

    suspend fun getApply(channel: Int) {
        runBlocking {
            val responseMeasure = async { clientStub.apply(channel) }
            //receivedStatus= responseStatus.await()
        }
    }

    suspend fun getReset(channel: Int) {
        runBlocking {
            val responseMeasure = async { clientStub.reset(channel) }
            //receivedStatus= responseStatus.await()
        }
    }

    suspend fun getReadyStatus(): Boolean? {
        var responseState: Boolean? = null
        runBlocking {
            val receivedState = async { clientStub.isReady() }
            responseState = receivedState.await()
        }
        return responseState
    }

    suspend fun getSweepType(channel: Int): sweep_type {
        var responseType: sweep_type
        runBlocking {
            val receivedType = async { clientStub.sweepType(channel) }
            responseType = receivedType.await()
        }
        return responseType
    }

    suspend fun getPointsCount(channel: Int): Int? {
        var responseCount: Int? = null
        runBlocking {
            val receivedCount = async { clientStub.pointsCount(channel) }
            responseCount = receivedCount.await()
        }
        return responseCount
    }

    suspend fun getTriggerMode(): String? {
        var responseMode: String? = null
        runBlocking {
            val receivedMode = async { clientStub.triggerMode() }
            responseMode = receivedMode.await()
        }
        return responseMode
    }

    suspend fun getSpan(sweepType: sweep_type,channel: Int): Array<Double>? {
        var responseMinMax: Array<Double>? = null
        runBlocking {
            val receivedMinMax = async { clientStub.span(sweepType,channel) }
            responseMinMax = receivedMinMax.await()
        }
        return responseMinMax
    }

    suspend fun getRfOut(): Boolean? {
        var responseRF: Boolean? = null
        runBlocking {
            val receivedRF = async { clientStub.rfOut() }
            responseRF = receivedRF.await()
        }
        return responseRF
    }

    suspend fun getCalibrationType(channel: Int): String? {
        var responseType: String? = null
        runBlocking {
            val receivedType = async { clientStub.calibrationType(channel) }
            responseType = receivedType.await()
        }
        return responseType
    }

    suspend fun getPortList(channel: Int): MutableList<Int>? {
        var responseList: MutableList<Int>? = null
        runBlocking {
            val receivedList = async { clientStub.portList(channel) }
            responseList = receivedList.await()
        }
        return responseList
    }

    //TODO: Дописать Метод
    suspend fun getChoosePortsSolt2(portArray: MutableIterable<Int>,channel: Int) {
        runBlocking {
            val responseSolt2 = async { clientStub.choosePortsSolt2(channel,portArray) }
        }
    }

}
