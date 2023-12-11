package com.example.mydwindemoapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

object ModbusTypeConverter {

    fun Byte.getIntValueFromByte(): Int {
        return this.toInt() and 0xFF
    }

    fun byteArrayToFloat(bytes: ByteArray): Float {
        val byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        return byteBuffer.float
    }
}