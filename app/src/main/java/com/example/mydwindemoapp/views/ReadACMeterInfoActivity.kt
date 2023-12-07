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

    override fun onPause() {
        super.onPause()
        observer.stopObserving()
    }
}