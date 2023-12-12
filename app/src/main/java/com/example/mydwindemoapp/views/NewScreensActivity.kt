package com.example.mydwindemoapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mydwindemoapp.R
import com.example.mydwindemoapp.base.SerialPortBaseActivity
import com.example.mydwindemoapp.databinding.ActivityNewScreensBinding

class NewScreensActivity : SerialPortBaseActivity() {
    private lateinit var binding: ActivityNewScreensBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewScreensBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}