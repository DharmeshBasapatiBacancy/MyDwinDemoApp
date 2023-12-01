package com.example.mydwindemoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.util.ModBusUtils.convertModbusResponseFrameToString
import com.example.mydwindemoapp.util.ModBusUtils.createReadHoldingRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createReadInputRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteMultipleRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteSingleRegisterRequest
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
    }

    override fun onResume() {
        super.onResume()
        try {
            setupSerialPort()
        } catch (e: Exception) {
            Log.d("TAG", "onFail: $e")
        }
    }

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
        val decodeResponse = convertModbusResponseFrameToString(buffer)
        Log.d("TAG", "onDataReceived: $decodeResponse")
        withContext(Dispatchers.Main) {
            txtDataRead.text = "Data received =\n $decodeResponse"
        }
    }



    fun openPrefs(view: View) {
        startActivity(Intent(this, PrefsActivity::class.java))
    }

    fun writeData(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var address = 2

                    var quantity = 1
                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        address = edtStartAddress.text.toString().toInt()
                    }
                    if(!TextUtils.isEmpty(edtRegistersCount.text.toString())){
                        quantity = edtRegistersCount.text.toString().toInt()
                    }
                    writeToMultipleHoldingRegisters(address)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun writeToMultipleHoldingRegisters(startAddress: Int) {
        val writeData = intArrayOf(72,84,96,108,120) // Example data to write

        val requestFrame: ByteArray = createWriteMultipleRegistersRequest(1, startAddress, writeData)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                readHoldingRegisters(startAddress,writeData.size)
            }
        }
    }

    private suspend fun writeToSingleHoldingRegister(startAddress: Int, regValue: Int) {
        val requestFrame: ByteArray = createWriteSingleRegisterRequest(1, startAddress, regValue)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                readHoldingRegisters(startAddress,1)
            }
        }
    }

    private suspend fun readInputRegisters(startAddress: Int, quantity: Int){
        val requestFrame: ByteArray = createReadInputRegistersRequest(1, startAddress, quantity)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(256)
        val bytesRead: Int? = mInputStream?.read(responseFrame)
        Log.d("TAG", "readInputRegisters: $bytesRead")
        if (bytesRead != null) {
            if (bytesRead > 0) {
                onDataReceived(responseFrame, bytesRead)
            }
        }
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
        val requestFrame: ByteArray = createReadHoldingRegistersRequest(1, startAddress, quantity)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                onDataReceived(responseFrame, size)
            }
        }
    }
}