package com.example.mydwindemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PrefsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SerialPortPrefsNew())
            .commit()
    }
}