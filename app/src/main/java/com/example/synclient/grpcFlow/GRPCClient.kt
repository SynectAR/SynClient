package com.example.synclient.grpcFlow

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import vnarpc.*

import vnarpc.VnaRpcGrpcKt.VnaRpcCoroutineStub
import java.io.Closeable
import java.util.concurrent.TimeUnit

public class GRPCClient(private val channel: ManagedChannel) : Closeable {
    private val stub: VnaRpcCoroutineStub = VnaRpcCoroutineStub(channel)

    suspend fun isConnected(): String {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.isConnected(request)
        var responseStatus = response.connectionState.toString()
        return responseStatus
    }

    suspend fun portCount(): Int {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.getPortCount(request)
        println("Received: ${response.portcount}")
        return response.portcount
    }

    suspend fun portStatus(port: Int,channel : Int): Array<Boolean> {
        val request = PortAndChannel.newBuilder().setPort(port).setChannel(channel).build()
        val response = stub.getPortStatus(request)
        var responseArray = arrayOf(response.open, response.short, response.load)
        return responseArray
    }

    suspend fun measurePort(port: Int, type: String,channel: Int) {
        val request = MeasureParams.newBuilder()
            .setPort(port)
            .setChannel(channel)
            .setType(type)
            .setGender(true)
            .build()
        val response = stub.measurePort(request)
        println("Received: ${response}")
    }

    suspend fun measureThru(firstPort: Int, secondPort: Int,channel: Int) {
        val request = ThruParams.newBuilder()
            .setChannel(channel)
            .setRcvport(firstPort)
            .setSrcport(secondPort)
            .build()
        val response = stub.measureThru(request)
        println("Received: ${response}")
    }

    suspend fun apply(channel: Int) {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.apply(request)
        println("Received: ${response} ")
    }

    suspend fun reset(channel: Int) {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.reset(request)
        println("Received: ${response} ")
    }

    suspend fun isReady(): Boolean {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.isReady(request)
        var responseState = response.state
        return responseState
    }

    // Возможо не правильный тип возврата данных
    suspend fun sweepType(channel: Int): sweep_type {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.sweepType(request)
        var responseType = response.type
        return responseType
    }

    suspend fun pointsCount(channel: Int): Int {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.pointsCount(request)
        var responseCount = response.count
        return responseCount
    }

    suspend fun triggerMode(): String {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.triggerMode(request)
        var responseMode = response.triggermode.toString()
        return responseMode
    }

    suspend fun span(sweepType: sweep_type,channel: Int): Array<Double> {
        val request = SweepTypeAndChannel.newBuilder().setType(sweepType).setChannel(channel).build()
        val response = stub.span(request)
        var responseMinMax = arrayOf(response.min, response.max)
        return responseMinMax
    }

    suspend fun rfOut(): Boolean {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.rfOut(request)
        var responseRF = response.state
        return responseRF
    }

    suspend fun calibrationType(channel: Int): String {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.calibrationType(request)
        var responseType = response.type
        return responseType
    }

    suspend fun portList(channel: Int): MutableList<Int>? {
        val request = Channel.newBuilder().setChannel(channel).build()
        val response = stub.portList(request)
        var responseList = response.portsList
        return responseList
    }

    //TODO: Дописать с передачей выбранных портов для калибровки
    suspend fun choosePortsSolt2(channel: Int,portArray: MutableIterable<Int>) {
        val request =
            SoltPorts.newBuilder().setChannel(channel).addAllPorts(portArray).build()
        val response = stub.chooseSoltPorts(request)
        println("Received: ${response} ")
    }


    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
suspend fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val channel = ManagedChannelBuilder
        .forAddress("localhost", port)
        .usePlaintext()
        .build()

    val client = GRPCClient(channel)

    //val user = args.singleOrNull() ?: 0
    val user = args.singleOrNull() ?: "world"

    println("Поздоровался ли?")
    channel.shutdown()
}