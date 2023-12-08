package com.example.mydwindemoapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydwindemoapp.R
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.databinding.ActivityReadAcmeterInfoBinding
import com.example.mydwindemoapp.models.ACMeterModel
import com.example.mydwindemoapp.util.ModBusUtils
import com.example.mydwindemoapp.util.ModBusUtils.toHex
import com.example.mydwindemoapp.util.ModbusReadObserver
import com.example.mydwindemoapp.views.adapters.ACMeterListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ReadACMeterInfoActivity : SerialPortBaseActivity() {

    private lateinit var observer: ModbusReadObserver
    private lateinit var acMeterListAdapter: ACMeterListAdapter
    private lateinit var binding: ActivityReadAcmeterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadAcmeterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        acMeterListAdapter = ACMeterListAdapter {}

        binding.apply {
            rvAcMeterInfo.layoutManager = LinearLayoutManager(this@ReadACMeterInfoActivity)
            rvAcMeterInfo.adapter = acMeterListAdapter
        }

        startReadingACMeterInformation()
    }

    private fun startReadingACMeterInformation() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    observer = ModbusReadObserver()
                    observer.startObserving(
                        ModBusUtils.READ_INPUT_REGISTERS_FUNCTION_CODE,
                        1,
                        0,
                        24,
                        mOutputStream,
                        mInputStream
                    ) { responseFrameArray ->
                        onDataReceived(responseFrameArray)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun onDataReceived(buffer: ByteArray) {
        val decodeResponse = ModBusUtils.getResponseDataInListFromModbusResponse(buffer)

        val newResponse = parseInputRegistersResponse(buffer)
        Log.d("TAG", "onDataReceived: NEW RESPONSE SIZE = ${newResponse.size}")
        newResponse.forEach {
            Log.d("TAG","Converted Float Value: $it")
        }
        Log.d("TAG", "onDataReceived: $decodeResponse")
        lifecycleScope.launch(Dispatchers.Main) {
            if(decodeResponse.isNotEmpty()){
                val acMeterModel = ACMeterModel(
                    1,
                    decodeResponse[0],
                    decodeResponse[1],
                    decodeResponse[2],
                    decodeResponse[3],
                    decodeResponse[4],
                    decodeResponse[5],
                    decodeResponse[6],
                    decodeResponse[7],
                    decodeResponse[8],
                    decodeResponse[9],
                    decodeResponse[10],
                    decodeResponse[11]
                )
                acMeterListAdapter.submitList(listOf(acMeterModel))
            }
        }
    }

    private fun hexToFloat(highByte: Byte, lowByte: Byte): Float {
        // Convert two bytes (highByte and lowByte) to a 32-bit float
        val intBits = (highByte.toInt() and 0xFF shl 8) or (lowByte.toInt() and 0xFF)
        return Float.fromBits(intBits)
    }

    private fun parseInputRegistersResponse(response: ByteArray): FloatArray {
        Log.d("TAG", "parseMultipleHoldingRegistersResponse: RESPONSE SIZE = ${response.size}")
        Log.d("TAG", "parseMultipleHoldingRegistersResponse: RESPONSE HEX = ${response.toHex()}")
        val floatValues = FloatArray(response.size / 2)

        for (i in 0 until response.size step 2) {
            val highByte = response[i]
            val lowByte = response[i + 1]

            floatValues[i / 2] = hexToFloat(highByte, lowByte)
        }

        return floatValues
    }

    private fun parseMultipleHoldingRegistersResponse(
        response: ByteArray,
        quantity: Short
    ): FloatArray {
        // Implement the logic to parse the response and extract floating-point values
        // The response structure depends on your Modbus library and device
        // For simplicity, assume the response has the format: Slave Address + Function Code + Data
        // Modify this based on your actual Modbus device response format
        Log.d("TAG", "parseMultipleHoldingRegistersResponse: RESPONSE HEX = ${response.toHex()}")
        val floatValues = FloatArray(quantity.toInt())

        for (i in 0 until quantity) {
            val dataIndex = 3 + 2 * i
            val highByte = response[dataIndex].toInt() and 0xFFFF
            val lowByte = response[dataIndex + 1].toInt() and 0xFFFF
            val intBits = (highByte shl 8) or lowByte
            floatValues[i] = Float.fromBits(intBits)
        }

        return floatValues
    }

    /*private fun parseMultipleHoldingRegistersResponse(response: ByteArray, quantity: Short): FloatArray {
        Log.d("TAG", "parseMultipleHoldingRegistersResponse: RESPONSE HEX = ${response.toHex()}")
        val floatValues = FloatArray(quantity.toInt())

        if (response.size >= 3 + 4 * quantity) {
            for (i in 0 until quantity) {
                val dataIndex = 3 + 4 * i
                val highByte1 = response[dataIndex].toInt() and 0xFF
                val lowByte1 = response[dataIndex + 1].toInt() and 0xFF
                val highByte2 = response[dataIndex + 2].toInt() and 0xFF
                val lowByte2 = response[dataIndex + 3].toInt() and 0xFF

                val intBits = (highByte1 shl 24) or (lowByte1 shl 16) or (highByte2 shl 8) or lowByte2
                floatValues[i] = Float.fromBits(intBits)
            }
        } else {
            Log.d("TAG","Invalid response format")
        }

        return floatValues
    }*/

    override fun onPause() {
        super.onPause()
        observer.stopObserving()
    }
}