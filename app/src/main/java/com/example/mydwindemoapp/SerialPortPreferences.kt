package com.example.mydwindemoapp

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android_serialport_api.SerialPortFinder

class SerialPortPreferences : PreferenceActivity() {

    private var mApplication: App? = null
    private var mSerialPortFinder: SerialPortFinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mApplication = application as App
        mSerialPortFinder = mApplication!!.mSerialPortFinder

        addPreferencesFromResource(R.xml.preferences)

        // Devices
        val devices = findPreference("DEVICE") as ListPreference
        val entries: Array<String> = mSerialPortFinder!!.getAllDevices()
        val entryValues: Array<String> = mSerialPortFinder!!.getAllDevicesPath()
        devices.entries = entries
        devices.entryValues = entryValues
        devices.summary = devices.value
        devices.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue as String
                true
            }

        // Baud rates

        // Baud rates
        val baudrates = findPreference("BAUDRATE") as ListPreference
        baudrates.summary = baudrates.value
        baudrates.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue as String
                true
            }
    }

}