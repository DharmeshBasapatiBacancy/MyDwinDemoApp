package com.example.mydwindemoapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mydwindemoapp.App
import com.example.mydwindemoapp.R
import java.io.InputStream
import java.io.OutputStream

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    fun goToReadInputRegisterScreen(view: View) {
        startActivity(Intent(this,ReadInputRegistersActivity::class.java))
    }
    fun goToReadHoldingRegistersScreen(view: View) {
        startActivity(Intent(this,ReadHoldingRegistersActivity::class.java))
    }
    fun goToWriteSingleHoldingRegisterScreen(view: View) {
        startActivity(Intent(this,WriteSingleHoldingRegisterActivity::class.java))
    }
    fun goToWriteMultipleHoldingRegistersScreen(view: View) {
        startActivity(Intent(this,WriteMultipleHoldingRegistersActivity::class.java))
    }
}