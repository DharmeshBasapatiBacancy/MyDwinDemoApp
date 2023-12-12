package com.example.mydwindemoapp.views

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.databinding.ActivityGun1InformationBinding
import com.example.mydwindemoapp.util.ModBusUtils.toHex
import com.example.mydwindemoapp.util.ModbusReadObserver
import com.example.mydwindemoapp.util.ModbusRequestFrames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class Gun1InformationActivity : SerialPortBaseActivity() {

    private var isGun1: Boolean? = null
    private lateinit var observer: ModbusReadObserver
    private lateinit var binding: ActivityGun1InformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGun1InformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isGun1 = intent.extras?.getBoolean("IS_GUN1", true)
        startReadingGun1Information()
    }

    private fun startReadingGun1Information() {

        val gunRequestFrame: ByteArray = if (isGun1 == true) {
            Log.d("TAG", "startReadingGun1Information: Gun1")
            ModbusRequestFrames.getGun1InfoRequestFrame()
        } else {
            Log.d("TAG", "startReadingGun1Information: Gun2")
            ModbusRequestFrames.getGun2InfoRequestFrame()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    observer = ModbusReadObserver()
                    observer.startObserving(
                        mOutputStream,
                        mInputStream, 41,
                        gunRequestFrame
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
    }

    override fun onPause() {
        super.onPause()
        observer.stopObserving()
    }
}