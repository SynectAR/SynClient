package com.example.synclient.grpcFlow

import io.grpc.Server
import vnarpc.*


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


public class GRPCService : VnaRpcGrpcKt.VnaRpcCoroutineImplBase() {


    override suspend fun getPortCount(request: EmptyMessage) : PortCount {
        val message ="Port Count is ${request}"
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

}
}



fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = GRPCServer(port)
    server.start()
    server.blockUntilShutdown()
}