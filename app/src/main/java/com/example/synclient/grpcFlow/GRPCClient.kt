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
        var responseString= response.connectionState.toString()
        return responseString
    }

    suspend fun portCount(): Int {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.getPortCount(request)
        println("Received: ${response.portcount}")
        return response.portcount
    }

    suspend fun portStatus(port: Int): Array<Boolean> {
        val request = Port.newBuilder().setPort(port).build()
        val response = stub.getPortStatus(request)
        var responseArray= arrayOf(response.open,response.short,response.load)
        return responseArray
    }

    suspend fun measurePort(port: Int, type: String) {
        val request = MeasureParams.newBuilder()
            .setPort(port)
            .setType(type)
            .setGender(true)
            .build()
        val response = stub.measurePort(request)
        println("Received: ${response}")
    }

    suspend fun measureThru(firstPort: Int, secondPort: Int) {
        val request = PortsPair.newBuilder()
            .setFirstport(firstPort)
            .setSecondport(secondPort)
            .build()
        val response = stub.measureThru(request)
        println("Received: ${response}")
    }

    suspend fun apply() {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.reset(request)
        println("Received: ${response} ")
    }

    suspend fun reset() {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.reset(request)
        println("Received: ${response} ")
    }

    suspend fun isReady(): Boolean{
        val request = EmptyMessage.newBuilder().build()
        val response = stub.isReady(request)
        var responseState= response.state
        return responseState
    }
    // Возможо не правильный тип возврата данных
    suspend fun sweepType() : String {
        val request = EmptyMessage.newBuilder().build()
        val response= stub.sweepType(request)
        var responseType= response.type.toString()
        return responseType
    }

    suspend fun pointsCount(): Int{
        val request = EmptyMessage.newBuilder().build()
        val response = stub.pointsCount(request)
        var responseCount= response.count
        return responseCount
    }

    suspend fun triggerMode(): String{
        val request = EmptyMessage.newBuilder().build()
        val response= stub.triggerMode(request)
        var responseMode= response.triggermode.toString()
        return responseMode
    }

    suspend fun span(sweepType :SweepType.sweep_type) : Array<Double>{
        val request = SweepType.newBuilder().setType(sweepType).build()
        val response = stub.span(request)
        var responseMinMax= arrayOf(response.min,response.max)
        return responseMinMax
    }

    suspend fun rfOut(): Boolean{
        val request = EmptyMessage.newBuilder().build()
        val response= stub.rfOut(request)
        var responseRF= response.state
        return  responseRF
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