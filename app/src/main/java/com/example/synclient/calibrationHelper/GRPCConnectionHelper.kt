package com.example.synclient.calibrationHelper

import com.example.synclient.grpcFlow.GRPCClient
import io.grpc.ManagedChannelBuilder

object GRPCConnectionHelper {
    fun setupConnection(): GRPCClient {
        //.forAddress("10.0.2.2", port)
        val port = System.getenv("PORT")?.toInt() ?: 50051
        val channel = ManagedChannelBuilder
            .forAddress("192.168.1.85", port)
            .usePlaintext()
            .build()
        val client = GRPCClient(channel)
        return client
    }
}