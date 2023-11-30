package com.example.mydwindemoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.util.ModBusUtils.convertModbusReadHoldingRegistersResponse
import com.example.mydwindemoapp.util.ModBusUtils.createReadHoldingRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createReadInputRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteMultipleRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteSingleRegisterRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteStringToSingleRegisterRequest
import com.example.mydwindemoapp.util.ModBusUtils.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var txtDataRead: TextView
    private lateinit var edtStartAddress: EditText
    private lateinit var edtRegistersCount: EditText
    private var mOutputStream: OutputStream? = null
    private var mInputStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtDataRead = findViewById(R.id.txtDataRead)
        edtStartAddress = findViewById(R.id.edtStartAddress)
        edtRegistersCount = findViewById(R.id.edtRegistersCount)

        Log.d("TAG", "onCreate: HEX TO STRING = ${hexStringToString("01 10 00 02 00 02 04 00 09 00 04")} ")
    }

    override fun onResume() {
        super.onResume()
        try {
            setupSerialPort()
        } catch (e: Exception) {
            Log.d("TAG", "onFail: $e")
        }
    }

    /*private fun readReceivedData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val size: Int
            try {
                val buffer = ByteArray(64)
                if (mInputStream == null) return@launch
                size = mInputStream!!.read(buffer)
                if (size > 0) {
                    onDataReceived(buffer, size)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@launch
            }
        }
    }*/

    private fun setupSerialPort() {
        val app = application as App
        val serialPort = app.getSerialPort()
        Log.d("TAG", "SerialPort: $serialPort")
        mOutputStream = serialPort.outputStream
        Log.d("TAG", "mOutputStream: $mOutputStream")
        mInputStream = serialPort.inputStream
        Log.d("TAG", "mInputStream: $mInputStream")
    }

    private suspend fun onDataReceived(buffer: ByteArray, size: Int) {
        Log.d("TAG", "onDataReceived: In Hex = ${buffer.toHex()}")
        val decodeResponse = convertModbusReadHoldingRegistersResponse(buffer)
        Log.d("TAG", "onDataReceived: $decodeResponse")
        //val string = String(buffer, 0, size)
        withContext(Dispatchers.Main) {
            txtDataRead.text = "Data received = $decodeResponse"
        }
    }

    fun hexStringToString(hexString: String): String {
        val hexValues = hexString.split(" ")
        val byteValues = hexValues.map { it.toInt(16).toByte() }.toByteArray()
        return String(byteValues, Charsets.UTF_8)
    }

    fun openPrefs(view: View) {
        startActivity(Intent(this, PrefsActivity::class.java))
    }

    fun writeData(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    //mOutputStream?.write("HELLO".toByteArray())
                    var address = 2

                    var quantity = 1
                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        address = edtStartAddress.text.toString().toInt()
                    }
                    if(!TextUtils.isEmpty(edtRegistersCount.text.toString())){
                        quantity = edtRegistersCount.text.toString().toInt()
                    }
                    writeToSingleHoldingRegister(address, quantity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun writeToMultipleHoldingRegisters(startAddress: Int) {
        // Create a Modbus RTU Write Multiple Registers request frame
        val writeData = intArrayOf(0x09, 0x04) // Example data to write

        val requestFrame: ByteArray = createWriteMultipleRegistersRequest(1, startAddress, writeData)

        // Send the request frame
        mOutputStream?.write(requestFrame)

        // Receive the response frame
        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                onDataReceived(responseFrame, size)
            }
        }
    }



    private suspend fun writeToSingleHoldingRegister(startAddress: Int, regValue: Int) {
        // Create a Modbus RTU Write Multiple Registers request frame

        val requestFrame: ByteArray = createWriteSingleRegisterRequest(1, startAddress, regValue)

        // Send the request frame
        mOutputStream?.write(requestFrame)

        // Receive the response frame
        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                readHoldingRegisters(startAddress,1)
            }
        }
    }

    private suspend fun readInputRegisters(startAddress: Int){
        // Create a Modbus RTU Read Input Registers request frame
        val requestFrame: ByteArray = createReadInputRegistersRequest(1, startAddress, 5)

        // Send the request frame
        mOutputStream?.write(requestFrame)

        // Receive the response frame
        val responseFrame = ByteArray(256)
        val bytesRead: Int? = mInputStream?.read(responseFrame)
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, "Bytes read = $bytesRead",Toast.LENGTH_LONG).show()
        }
        Log.d("TAG", "readInputRegisters: bytesRead = $bytesRead")
    }

    fun readData(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var address = 1
                    var quantity = 1
                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        address = edtStartAddress.text.toString().toInt()
                    }
                    if(!TextUtils.isEmpty(edtRegistersCount.text.toString())){
                        quantity = edtRegistersCount.text.toString().toInt()
                    }

                    readHoldingRegisters(address,quantity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun readHoldingRegisters(startAddress: Int, quantity: Int){
        // Create a Modbus RTU Read Input Registers request frame
        val requestFrame: ByteArray = createReadHoldingRegistersRequest(1, startAddress, quantity)

        // Send the request frame
        mOutputStream?.write(requestFrame)

        // Receive the response frame
        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                onDataReceived(responseFrame, size)
            }
        }
    }
}