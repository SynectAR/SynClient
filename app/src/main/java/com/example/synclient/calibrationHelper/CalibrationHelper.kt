package com.example.synclient.calibrationHelper

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import vnarpc.SweepType


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

    suspend fun getPortStatus(port: Int): Array<Boolean>? {
        var receivedStatus: Array<Boolean>? = null
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

    suspend fun getReadyStatus(): Boolean? {
        var responseState: Boolean? = null
        runBlocking {
            val receivedState = async { clientStub.isReady() }
            responseState = receivedState.await()
        }
        return responseState
    }

    suspend fun getSweepType(): SweepType.sweep_type {
        var responseType: SweepType.sweep_type
        runBlocking {
            val receivedType = async { clientStub.sweepType() }
            responseType = receivedType.await()
        }
        return responseType
    }

    suspend fun getPointsCount(): Int? {
        var responseCount: Int? = null
        runBlocking {
            val receivedCount = async { clientStub.pointsCount() }
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

    suspend fun getSpan(sweepType: SweepType.sweep_type): Array<Double>? {
        var responseMinMax: Array<Double>? = null
        runBlocking {
            val receivedMinMax = async { clientStub.span(sweepType) }
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

    suspend fun getCalibrationType(): String? {
        var responseType: String? = null
        runBlocking {
            val receivedType = async { clientStub.calibrationType() }
            responseType = receivedType.await()
        }
        return responseType
    }

    suspend fun getPortList(): MutableList<Int>? {
        var responseList: MutableList<Int>? = null
        runBlocking {
            val receivedList = async { clientStub.portList() }
            responseList = receivedList.await()
        }
        return responseList
    }

    suspend fun getChoosePortsSolt2(firstPort: Int, secondPort: Int) {
        runBlocking {
            val responseSolt2 = async { clientStub.choosePortsSolt2(firstPort, secondPort) }
        }
    }

}
