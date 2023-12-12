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

    fun bytesToAsciiString(bytes: ByteArray): String {
        return String(bytes, Charsets.US_ASCII)
    }

    fun decimalToHex(decimalValue: Int): String {
        return Integer.toHexString(decimalValue)
    }

    fun decimalArrayToHexArray(decimalList: List<Int>): MutableList<String> {
        val hexList = mutableListOf<String>()
        decimalList.forEach {
            hexList.add(decimalToHex(it))
        }
        return hexList
    }
}