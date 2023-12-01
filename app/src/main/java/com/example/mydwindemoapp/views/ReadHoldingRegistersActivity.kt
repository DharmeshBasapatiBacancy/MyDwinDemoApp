package com.example.mydwindemoapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
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

class ReadHoldingRegistersActivity : SerialPortBaseActivity() {

    private lateinit var txtDataRead: TextView
    private lateinit var edtStartAddress: EditText
    private lateinit var edtRegistersCount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_holding_registers)
        supportActionBar?.title = "Read Holding Registers"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtDataRead = findViewById(R.id.txtDataRead)
        edtStartAddress = findViewById(R.id.edtStartAddress)
        edtRegistersCount = findViewById(R.id.edtRegistersCount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the Up button press (navigate up or any custom action)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
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

    fun readHoldingRegisters(view: View) {
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
}