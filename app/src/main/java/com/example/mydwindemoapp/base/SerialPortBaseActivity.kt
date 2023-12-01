package com.example.mydwindemoapp.base

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android_serialport_api.SerialPort
import androidx.appcompat.app.AppCompatActivity
import com.example.mydwindemoapp.App
import java.io.InputStream
import java.io.OutputStream

abstract class SerialPortBaseActivity : AppCompatActivity(){

    protected var mApplication: App? = null
    protected var mSerialPort: SerialPort? = null
    protected var mOutputStream: OutputStream? = null
    var mInputStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mApplication = application as App
        try {
            mSerialPort = mApplication!!.getSerialPort()
            mOutputStream = mSerialPort!!.outputStream
            mInputStream = mSerialPort!!.inputStream
        } catch (e: Exception) {
            Log.d("TAG", "onCreate: Exception = ${e.toString()}")
        }
    }

    override fun onDestroy() {
        mApplication!!.closeSerialPort()
        mSerialPort = null
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}