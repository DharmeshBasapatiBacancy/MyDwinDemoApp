package com.example.mydwindemoapp.util

import com.example.mydwindemoapp.util.ModBusUtils.READ_HOLDING_REGISTERS_FUNCTION_CODE
import com.example.mydwindemoapp.util.ModBusUtils.READ_INPUT_REGISTERS_FUNCTION_CODE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class ModbusReadObserver {

    private lateinit var registerValues: ByteArray
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    fun startObserving(
        functionCode: Byte,
        slaveAddress: Int,
        startAddress: Int,
        quantity: Int,
        mOutputStream: OutputStream?,
        mInputStream: InputStream?,
        onResponse: (ByteArray) -> Unit
    ) {
        job = scope.launch {
            while (isActive) {
                try {

                    when (functionCode) {
                        READ_HOLDING_REGISTERS_FUNCTION_CODE -> {
                            registerValues = ModBusUtils.createReadHoldingRegistersRequest(
                                slaveAddress,
                                startAddress,
                                quantity
                            )

                        }

                        READ_INPUT_REGISTERS_FUNCTION_CODE -> {
                            registerValues = ModBusUtils.createReadInputRegistersRequest(
                                slaveAddress,
                                startAddress,
                                quantity
                            )
                        }
                    }

                    withContext(Dispatchers.IO) {
                        mOutputStream?.write(registerValues)
                    }

                    val responseFrame = ByteArray(5+(quantity*2))
                    withContext(Dispatchers.IO) {
                        mInputStream?.read(responseFrame)
                    }

                    onResponse(responseFrame)

                    delay(TimeUnit.MILLISECONDS.toMillis(500))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopObserving() {
        job?.cancel()
    }
}