package com.example.mydwindemoapp.util

object ModBusUtils {

    //Input Register
    private const val READ_INPUT_REGISTERS_FUNCTION_CODE: Byte = 0x04

    //Holding Register
    private const val WRITE_MULTIPLE_REGISTERS_FUNCTION_CODE: Byte = 0x10
    private const val READ_HOLDING_REGISTERS_FUNCTION_CODE: Byte = 0x03
    private const val WRITE_SINGLE_REGISTER_FUNCTION_CODE: Byte = 0x06

    private fun calculateCRC(vararg values: Int): Int {
        var crc = 0xFFFF
        for (value in values) {
            crc = crc xor (value and 0xFF)
            for (i in 0..7) {
                if (crc and 0x0001 != 0) {
                    crc = crc shr 1
                    crc = crc xor 0xA001
                } else {
                    crc = crc shr 1
                }
            }
        }
        return crc
    }

    fun createReadInputRegistersRequest(
        slaveAddress: Int,
        startAddress: Int,
        quantity: Int
    ): ByteArray {
        // Calculate CRC (Cyclic Redundancy Check)
        val crc =
            calculateCRC(slaveAddress, READ_INPUT_REGISTERS_FUNCTION_CODE.toInt(), startAddress, quantity)

        // Create the Modbus RTU frame
        return byteArrayOf(
            slaveAddress.toByte(),
            READ_INPUT_REGISTERS_FUNCTION_CODE,
            (startAddress shr 8).toByte(),
            startAddress.toByte(),
            (quantity shr 8).toByte(),
            quantity.toByte(),
            (crc and 0xFF).toByte(),
            (crc shr 8 and 0xFF).toByte()
        )
    }

    fun createWriteMultipleRegistersRequest(
        slaveAddress: Int,
        startAddress: Int,
        data: IntArray
    ): ByteArray {
        val quantity = data.size
        val byteCount = quantity * 2 // Each register is 2 bytes
        val crc = calculateCRC(
            slaveAddress,
            WRITE_MULTIPLE_REGISTERS_FUNCTION_CODE.toInt(), startAddress, quantity, byteCount
        )
        val frame = ByteArray(9 + byteCount)
        frame[0] = slaveAddress.toByte()
        frame[1] = WRITE_MULTIPLE_REGISTERS_FUNCTION_CODE
        frame[2] = (startAddress shr 8).toByte()
        frame[3] = startAddress.toByte()
        frame[4] = (quantity shr 8).toByte()
        frame[5] = quantity.toByte()
        frame[6] = byteCount.toByte()
        for (i in 0 until quantity) {
            val registerValue = data[i]
            frame[7 + 2 * i] = (registerValue shr 8).toByte()
            frame[8 + 2 * i] = registerValue.toByte()
        }
        frame[frame.size - 2] = (crc and 0xFF).toByte()
        frame[frame.size - 1] = (crc shr 8 and 0xFF).toByte()
        return frame
    }

    // Create Modbus RTU frame for reading holding registers
    fun createReadHoldingRegistersRequest(
        slaveAddress: Int,
        startAddress: Int,
        quantity: Int
    ): ByteArray {
        val crc = calculateCRC(
            slaveAddress,
            READ_HOLDING_REGISTERS_FUNCTION_CODE.toInt(), startAddress, quantity
        )
        return byteArrayOf(
            slaveAddress.toByte(),
            READ_HOLDING_REGISTERS_FUNCTION_CODE,
            (startAddress shr 8).toByte(),
            startAddress.toByte(),
            (quantity shr 8).toByte(),
            quantity.toByte(),
            (crc and 0xFF).toByte(),
            (crc shr 8 and 0xFF).toByte()
        )
    }

    // Create Modbus RTU frame for writing a single holding register
    fun createWriteSingleRegisterRequest(
        slaveAddress: Int,
        registerAddress: Int,
        registerValue: Int
    ): ByteArray {
        val crc = calculateCRC(
            slaveAddress,
            WRITE_SINGLE_REGISTER_FUNCTION_CODE.toInt(), registerAddress, registerValue
        )
        return byteArrayOf(
            slaveAddress.toByte(),
            WRITE_SINGLE_REGISTER_FUNCTION_CODE,
            (registerAddress shr 8).toByte(),
            registerAddress.toByte(),
            (registerValue shr 8).toByte(),
            registerValue.toByte(),
            (crc and 0xFF).toByte(),
            (crc shr 8 and 0xFF).toByte()
        )
    }

    private fun littleEndianConversion(bytes: ByteArray): Int {
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }
        return result
    }

}