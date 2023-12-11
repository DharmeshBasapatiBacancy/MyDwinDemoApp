package com.example.mydwindemoapp.util

object ModbusRequestFrames {

    fun getGunOneDCMeterInfoRequestFrame(
        slaveAddress: Int = 1,
        startAddress: Int = 50,
        quantity: Int = 18
    ): ByteArray {
        return ModBusUtils.createReadInputRegistersRequest(slaveAddress, startAddress, quantity)
    }

    fun getGunTwoDCMeterInfoRequestFrame(
        slaveAddress: Int = 1,
        startAddress: Int = 100,
        quantity: Int = 18
    ): ByteArray {
        return ModBusUtils.createReadInputRegistersRequest(slaveAddress, startAddress, quantity)
    }

    fun getACChargerACMeterInfoRequestFrame(
        slaveAddress: Int = 1,
        startAddress: Int = 150,
        quantity: Int = 24
    ): ByteArray {
        return ModBusUtils.createReadInputRegistersRequest(slaveAddress, startAddress, quantity)
    }

    fun getACMeterInfoRequestFrame(
        slaveAddress: Int = 1,
        startAddress: Int = 0,
        quantity: Int = 24
    ): ByteArray {
        return ModBusUtils.createReadInputRegistersRequest(slaveAddress, startAddress, quantity)
    }

    fun getMiscInfoRequestFrame(
        slaveAddress: Int = 1,
        startAddress: Int = 0,
        quantity: Int = 75
    ): ByteArray{
        return ModBusUtils.createReadHoldingRegistersRequest(slaveAddress, startAddress, quantity)
    }

}