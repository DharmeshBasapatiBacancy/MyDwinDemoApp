package com.example.mydwindemoapp.views

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.databinding.ActivityReadMiscInfoBinding
import com.example.mydwindemoapp.util.ModBusUtils
import com.example.mydwindemoapp.util.ModBusUtils.toHex
import com.example.mydwindemoapp.util.ModbusReadObserver
import com.example.mydwindemoapp.util.ModbusRequestFrames
import com.example.mydwindemoapp.util.ModbusTypeConverter.getIntValueFromByte
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ReadMiscInfoActivity : SerialPortBaseActivity() {

    private lateinit var observer: ModbusReadObserver
    private lateinit var binding: ActivityReadMiscInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMiscInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Misc Information"
        startReadingMiscInformation()
    }

    private fun startReadingMiscInformation() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    observer = ModbusReadObserver()
                    observer.startObserving(
                        mOutputStream,
                        mInputStream, 256,
                        ModbusRequestFrames.getMiscInfoRequestFrame()
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
        Log.d("TAG", "onDataReceived: ${buffer.toHex()}")
        //val decodeResponse = ModBusUtils.convertModbusResponseFrameToString(buffer)
        Log.d("TAG", "onDataReceived: AMBIENT TEMP = ${getAmbientTemperature(buffer)}")
        Log.d("TAG", "onDataReceived: MCU FIRMWARE VERSION = ${getMCUFirmwareVersion(buffer)}")
        Log.d("TAG", "onDataReceived: OCPP FIRMWARE VERSION = ${getOCPPFirmwareVersion(buffer)}")
        Log.d("TAG", "onDataReceived: RFID FIRMWARE VERSION = ${getRFIDFirmwareVersion(buffer)}")
        Log.d("TAG", "onDataReceived: PLC1 FAULT = ${getPLC1Fault(buffer)}")
        Log.d("TAG", "onDataReceived: PLC2 FAULT = ${getPLC2Fault(buffer)}")

    }

    private fun getAmbientTemperature(response: ByteArray): String {
        val reg1MSB = response[5].getIntValueFromByte()
        val reg1LSB = response[6].getIntValueFromByte()
        val reg2MSB = response[7].getIntValueFromByte()
        val reg2LSB = response[8].getIntValueFromByte()
        return "$reg1MSB$reg1LSB.$reg2MSB$reg2LSB c"
    }

    private fun getMCUFirmwareVersion(response: ByteArray):String {
        val reg3MSB = response[9].getIntValueFromByte()
        val reg3LSB = response[10].getIntValueFromByte()
        val reg4MSB = response[11].getIntValueFromByte()
        val reg4LSB = response[12].getIntValueFromByte()

        return "$reg3MSB.$reg4LSB.$reg4MSB"
    }

    private fun getOCPPFirmwareVersion(response: ByteArray):String {
        val reg3MSB = response[65].getIntValueFromByte()
        val reg3LSB = response[66].getIntValueFromByte()
        val reg4MSB = response[67].getIntValueFromByte()
        val reg4LSB = response[68].getIntValueFromByte()

        return "$reg3MSB.$reg3LSB.$reg4LSB.$reg4MSB"
    }

    private fun getRFIDFirmwareVersion(response: ByteArray):String {
        val reg3MSB = response[75].getIntValueFromByte()
        val reg3LSB = response[76].getIntValueFromByte()
        val reg4MSB = response[77].getIntValueFromByte()
        val reg4LSB = response[78].getIntValueFromByte()

        return "$reg3MSB.$reg4LSB.$reg4MSB"
    }

    private fun getPLC1Fault(response: ByteArray): String {
        val reg5MSB = response[13].getIntValueFromByte()
        val reg5LSB = response[14].getIntValueFromByte()

        val reg6MSB = response[15].getIntValueFromByte()
        val reg6LSB = response[16].getIntValueFromByte()

        val reg7MSB = response[17].getIntValueFromByte()
        val reg7LSB = response[18].getIntValueFromByte()

        val reg8MSB = response[19].getIntValueFromByte()
        val reg8LSB = response[20].getIntValueFromByte()
        return "$reg5MSB-$reg5LSB . $reg6MSB-$reg6LSB . $reg7MSB-$reg7LSB . $reg8MSB-$reg8LSB"
    }

    private fun getPLC2Fault(response: ByteArray): String {
        val reg5MSB = response[21].getIntValueFromByte()
        val reg5LSB = response[22].getIntValueFromByte()

        val reg6MSB = response[23].getIntValueFromByte()
        val reg6LSB = response[24].getIntValueFromByte()

        val reg7MSB = response[25].getIntValueFromByte()
        val reg7LSB = response[26].getIntValueFromByte()

        val reg8MSB = response[27].getIntValueFromByte()
        val reg8LSB = response[28].getIntValueFromByte()
        return "$reg5MSB-$reg5LSB . $reg6MSB-$reg6LSB . $reg7MSB-$reg7LSB . $reg8MSB-$reg8LSB"
    }

    override fun onPause() {
        super.onPause()
        observer.stopObserving()
    }
}