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
import com.example.mydwindemoapp.util.ModBusUtils.createReadInputRegistersRequest
import com.example.mydwindemoapp.util.ModBusUtils.createWriteMultipleRegistersRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var txtDataRead: TextView
    private lateinit var edtStartAddress: EditText
    private var mOutputStream: OutputStream? = null
    private var mInputStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtDataRead = findViewById(R.id.txtDataRead)
        edtStartAddress = findViewById(R.id.edtStartAddress)
    }

    override fun onResume() {
        super.onResume()
        try {
            setupSerialPort()
            readReceivedData()
        } catch (e: Exception) {
            Log.d("TAG", "onFail: $e")
        }
    }

    private fun readReceivedData() {
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
    }

    private fun setupSerialPort() {
        val app = application as App
        val serialPort = app.getSerialPort()
        Log.d("TAG", "SerialPort: $serialPort")
        mOutputStream = serialPort?.outputStream
        Log.d("TAG", "mOutputStream: $mOutputStream")
        mInputStream = serialPort?.inputStream
        Log.d("TAG", "mInputStream: $mInputStream")
    }

    private suspend fun onDataReceived(buffer: ByteArray, size: Int) {
        val string = String(buffer, 0, size)
        withContext(Dispatchers.Main) {
            txtDataRead.text = "Data received = $string"
        }
        Log.d("TAG", "onDataReceived: Buffer = $string and size = $size")
    }

    fun openPrefs(view: View) {
        startActivity(Intent(this, PrefsActivity::class.java))
    }

    fun writeData(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    //mOutputStream?.write("HELLO".toByteArray())
                    var address = 200
                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        address = edtStartAddress.text.toString().toInt()
                    }
                    writeToHoldingRegisters(address)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun writeToHoldingRegisters(startAddress: Int) {
        // Create a Modbus RTU Write Multiple Registers request frame
        val writeData = intArrayOf(0x00, 0x01) // Example data to write

        val requestFrame: ByteArray = createWriteMultipleRegistersRequest(1, startAddress, writeData)

        // Send the request frame
        mOutputStream?.write(requestFrame)

        // Receive the response frame
        val responseFrame = ByteArray(256)
        val bytesRead: Int? = mInputStream?.read(responseFrame)
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, "Bytes read = $bytesRead",Toast.LENGTH_LONG).show()
        }
        Log.d("TAG", "writeToHoldingRegisters: bytesRead = $bytesRead")
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
                    var address = 100
                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        address = edtStartAddress.text.toString().toInt()
                    }

                    readInputRegisters(address)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}