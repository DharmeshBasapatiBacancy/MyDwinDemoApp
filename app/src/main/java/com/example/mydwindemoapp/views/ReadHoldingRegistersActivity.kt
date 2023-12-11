package com.example.mydwindemoapp.views

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.R
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.util.ModbusReadObserver
import com.example.mydwindemoapp.util.ModBusUtils
import com.example.mydwindemoapp.util.ModBusUtils.READ_HOLDING_REGISTERS_FUNCTION_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ReadHoldingRegistersActivity : SerialPortBaseActivity() {

    private lateinit var observer: ModbusReadObserver
    private lateinit var txtDataRead: TextView
    private lateinit var edtStartAddress: EditText
    private lateinit var edtRegistersCount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_holding_registers)
        supportActionBar?.title = "Read Holding Registers"

        txtDataRead = findViewById(R.id.txtDataRead)
        edtStartAddress = findViewById(R.id.edtStartAddress)
        edtRegistersCount = findViewById(R.id.edtRegistersCount)
    }

    private suspend fun readHoldingRegisters(startAddress: Int, quantity: Int) {
        val requestFrame: ByteArray =
            ModBusUtils.createReadHoldingRegistersRequest(1, startAddress, quantity)

        mOutputStream?.write(requestFrame)

        val responseFrame = ByteArray(64)
        val size: Int? = mInputStream?.read(responseFrame)

        if (size != null) {
            if (size > 0) {
                //onDataReceived(responseFrame)
            }
        }
    }

    private fun onDataReceived(buffer: ByteArray) {
        val decodeResponse = ModBusUtils.convertModbusResponseFrameToString(buffer)
        Log.d("TAG", "onDataReceived: $decodeResponse")
        lifecycleScope.launch(Dispatchers.Main) {
            txtDataRead.text = "Data received =\n $decodeResponse"
        }
    }

    fun readHoldingRegisters(view: View) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var address = 1
                    var quantity = 1
                    if (!TextUtils.isEmpty(edtStartAddress.text.toString())) {
                        address = edtStartAddress.text.toString().toInt()
                    }
                    if (!TextUtils.isEmpty(edtRegistersCount.text.toString())) {
                        quantity = edtRegistersCount.text.toString().toInt()
                    }

                    observer = ModbusReadObserver()
                    observer.startObserving(
                        mOutputStream, mInputStream, 256,
                        ModBusUtils.createReadHoldingRegistersRequest(1, address, quantity)
                    ) { responseFrameArray ->
                        onDataReceived(responseFrameArray)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        observer.stopObserving()
    }
}