package com.example.synclient.grpcFlow

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import vnarpc.*

import vnarpc.VnaRpcGrpcKt.VnaRpcCoroutineStub
import java.io.Closeable
import java.util.concurrent.TimeUnit

public class GRPCClient(private val channel: ManagedChannel) : Closeable {
    private val stub: VnaRpcCoroutineStub = VnaRpcCoroutineStub(channel)

    suspend fun sayHello(name: String): String {
        val request = HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
        return response.message
    }

    suspend fun portCount(): Int {
        val request = EmptyMessage.newBuilder().build()
        val response = stub.getPortCount(request)
        println("Received: ${response.portcount}")
        return response.portcount
    }

    suspend fun portStatus(port: Int): Boolean {
        val request = Port.newBuilder().build()
        val response = stub.getPortStatus(request)
        if (response.open) return response.open
        if (response.short) return response.short
        if (response.load) return response.load
        else return false
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
    client.sayHello(user)
    //client.sayHello("TestName")
    println("Поздоровался ли?")
    channel.shutdown()
}