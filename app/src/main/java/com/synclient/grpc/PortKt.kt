//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: vnarpc.proto

package com.synclient.grpc

@kotlin.jvm.JvmSynthetic
inline fun port(block: com.synclient.grpc.PortKt.Dsl.() -> kotlin.Unit): com.synclient.grpc.Vnarpc.Port =
  com.synclient.grpc.PortKt.Dsl._create(com.synclient.grpc.Vnarpc.Port.newBuilder()).apply { block() }._build()
object PortKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  class Dsl private constructor(
    private val _builder: com.synclient.grpc.Vnarpc.Port.Builder
  ) {
    companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.synclient.grpc.Vnarpc.Port.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.synclient.grpc.Vnarpc.Port = _builder.build()

    /**
     * <code>int32 port = 1;</code>
     */
    var port: kotlin.Int
      @JvmName("getPort")
      get() = _builder.port
      @JvmName("setPort")
      set(value) {
          _builder.port = value
      }
    /**
     * <code>int32 port = 1;</code>
     */
    fun clearPort() {
      _builder.clearPort()
    }
  }
}
@kotlin.jvm.JvmSynthetic
inline fun com.synclient.grpc.Vnarpc.Port.copy(block: com.synclient.grpc.PortKt.Dsl.() -> kotlin.Unit): com.synclient.grpc.Vnarpc.Port =
  com.synclient.grpc.PortKt.Dsl._create(this.toBuilder()).apply { block() }._build()
