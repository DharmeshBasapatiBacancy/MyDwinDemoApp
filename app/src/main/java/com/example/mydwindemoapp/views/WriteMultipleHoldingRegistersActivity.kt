package com.example.mydwindemoapp.views

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.App
import com.example.mydwindemoapp.R
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.util.ModBusUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class WriteMultipleHoldingRegistersActivity : SerialPortBaseActivity() {

    private lateinit var txtDataRead: TextView
    private lateinit var edtStartAddress: EditText
    private lateinit var edtRegistersCount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_multiple_holding_registers)
        supportActionBar?.title = "Write Multiple Holding Registers"

        txtDataRead = findViewById(R.id.txtDataRead)
        edtStartAddress = findViewById(R.id.edtStartAddress)
        edtRegistersCount = findViewById(R.id.edtRegistersCount)
    }

    private suspend fun writeToMultipleHoldingRegisters(startAddress: Int, registersCount: Int) {
        //val writeData = intArrayOf(72,84,96,108,120) // Example data to write

        val dateToWrite = mutableListOf<Int>()
        for (i in 0 until registersCount){
            dateToWrite.add(i+10)
        }

        val requestFrame: ByteArray =
            ModBusUtils.createWriteMultipleRegistersRequest(1, startAddress, dateToWrite.toIntArray())

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(64)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                readHoldingRegisters(startAddress,registersCount)
            }
        }
    }

    private suspend fun readHoldingRegisters(startAddress: Int, quantity: Int){
        val requestFrame: ByteArray =
            ModBusUtils.createReadHoldingRegistersRequest(1, startAddress, quantity)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(256)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                onDataReceived(responseFrame)
            }
        }
    }

    private suspend fun onDataReceived(buffer: ByteArray) {
        val decodeResponse = ModBusUtils.convertModbusResponseFrameToString(buffer)
        Log.d("TAG", "onDataReceived: $decodeResponse")
        withContext(Dispatchers.Main) {
            txtDataRead.text = "Data received =\n $decodeResponse"
        }
    }

    fun writeInMultipleRegisters(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var startAddress = 2
                    var registersCount = 2

                    if(!TextUtils.isEmpty(edtStartAddress.text.toString())){
                        startAddress = edtStartAddress.text.toString().toInt()
                    }
                    if(!TextUtils.isEmpty(edtRegistersCount.text.toString())){
                        registersCount = edtRegistersCount.text.toString().toInt()
                    }

                    writeToMultipleHoldingRegisters(startAddress, registersCount)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}