package com.example.mydwindemoapp.views

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.databinding.ActivityReadAcmeterInfoBinding
import com.example.mydwindemoapp.models.ACMeterModel
import com.example.mydwindemoapp.util.ModBusUtils
import com.example.mydwindemoapp.util.ModBusUtils.floatArrayToHexString
import com.example.mydwindemoapp.util.ModBusUtils.parseInputRegistersResponse
import com.example.mydwindemoapp.util.ModBusUtils.toHex
import com.example.mydwindemoapp.util.ModbusReadObserver
import com.example.mydwindemoapp.util.ModbusRequestFrames
import com.example.mydwindemoapp.util.ModbusTypeConverter.byteArrayToFloat
import com.example.mydwindemoapp.views.adapters.ACMeterListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ReadACChargerACMeterInfoActivity : SerialPortBaseActivity() {

    private lateinit var observer: ModbusReadObserver
    private lateinit var acMeterListAdapter: ACMeterListAdapter
    private lateinit var binding: ActivityReadAcmeterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadAcmeterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "AC Charger AC Meter Information"

        acMeterListAdapter = ACMeterListAdapter {}

        binding.apply {
            rvAcMeterInfo.layoutManager = LinearLayoutManager(this@ReadACChargerACMeterInfoActivity)
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
                        mOutputStream,
                        mInputStream, 53,
                        ModbusRequestFrames.getACChargerACMeterInfoRequestFrame()
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
        val newResponse = parseInputRegistersResponse(buffer)
        Log.d("TAG", "onDataReceived: ${floatArrayToHexString(newResponse)}")
        Log.d("TAG", "onDataReceived: ${newResponse.toList()}")
        lifecycleScope.launch(Dispatchers.Main) {
            if (newResponse.isNotEmpty()) {
                val acMeterModel = ACMeterModel(
                    1,
                    newResponse[0],
                    newResponse[1],
                    newResponse[2],
                    newResponse[3],
                    newResponse[4],
                    newResponse[5],
                    newResponse[6],
                    newResponse[7],
                    newResponse[8],
                    newResponse[9],
                    newResponse[10],
                    newResponse[11]
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