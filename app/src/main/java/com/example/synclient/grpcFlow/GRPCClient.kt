package com.example.synclient.grpcFlow

import io.grpc.LoadBalancerRegistry
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.grpclb.GrpclbLoadBalancerProvider
import io.grpc.internal.PickFirstLoadBalancerProvider
import vnarpc.HelloRequest
import vnarpc.PortCount
import vnarpc.PortRequest

import vnarpc.vnarpcGrpcKt.vnarpcCoroutineStub
import java.io.Closeable
import java.util.concurrent.TimeUnit

//GRPCClient(private val channel: ManagedChannel)
public class GRPCClient(private val channel: ManagedChannel) : Closeable {
    //val channel = ManagedChannelBuilder.forAddress("localhost",50051).usePlaintext().build()
    private val stub: vnarpcCoroutineStub = vnarpcCoroutineStub(channel)

    suspend fun greet(name: String) {
        val request = HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
    }

    suspend fun portCount(count: Int)  {
        //var portRequest: PortRequest? = PortRequest.newBuilder().setPortname(count).build()
        //var response: PortCount = stub.getPortCount(portRequest!!)

        //val request = PortRequest {this.portname = count}
        //val answer= stub.getPortCount(request)
    }

    suspend fun portStatus() {}

    suspend fun measurePort() {}

    suspend fun measureThru() {}

    suspend fun apply() {}

    suspend fun reset() {}


/*
    suspend fun greet(name: String) {
        val request = helloRequest { this.name = name }
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
    }

 */

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
suspend fun main(args: Array<String>) {
    LoadBalancerRegistry.getDefaultRegistry().register(GrpclbLoadBalancerProvider())
    val port = System.getenv("PORT")?.toInt() ?: 50051

    val channel = ManagedChannelBuilder
        .forAddress("localhost", port)
        .usePlaintext()
        .build()

    val client = GRPCClient(channel)

    //val user = args.singleOrNull() ?: 0
    val user = args.singleOrNull() ?: "world"
    client.greet(user)
}