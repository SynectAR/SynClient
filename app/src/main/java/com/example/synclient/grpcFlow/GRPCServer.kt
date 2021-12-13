package com.example.synclient.grpcFlow

import com.google.common.base.Stopwatch
import com.google.common.base.Ticker
import com.google.protobuf.util.Durations
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import vnarpc.*
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class GRPCServer(private val port: Int){

    val server: Server = io.grpc.netty.NettyServerBuilder
    .forPort(port)
    .addService(GRPCService())
    .build()

fun start() {
    server.start()
    println("Server started, listening on $port")
    Runtime.getRuntime().addShutdownHook(
        Thread {
            println("*** shutting down gRPC server since JVM is shutting down")
            this@GRPCServer.stop()
            println("*** server shut down")
        }
    )
}

private fun stop() {
    server.shutdown()
}

fun blockUntilShutdown() {
    server.awaitTermination()
}


public class GRPCService : vnarpcGrpcKt.vnarpcCoroutineImplBase() {

    override suspend fun sayHello(request: HelloRequest) = HelloReply
        .newBuilder()
        .setMessage("Hello ${request.name}")
        .build()

    override suspend fun getPortCount(request: PortRequest) : PortCount {
        val message ="Port Count is ${request.portname}"
        return super.getPortCount(request)
    }

    override suspend fun getPortStatus(request: Port): PortStatus {
        return super.getPortStatus(request)
    }

    override suspend fun measurePort(request: MeasureParams): EmptyMessage {
        return super.measurePort(request)
    }

    override suspend fun measureThru(request: PortsPair): EmptyMessage {
        return super.measureThru(request)
    }

    override suspend fun apply(request: EmptyMessage): EmptyMessage {
        return super.apply(request)
    }

    override suspend fun reset(request: EmptyMessage): EmptyMessage {
        return super.reset(request)
    }



    /*
override suspend fun getPortCount(request: PortRequest)= PortCount  {
    portcountdisp = "Port Count: ${request.portname}"
}

 */
}
}



fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = GRPCServer(port)
    server.start()
    server.blockUntilShutdown()
}