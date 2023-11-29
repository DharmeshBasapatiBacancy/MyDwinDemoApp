package com.example.mydwindemoapp

import android.os.Bundle
import android_serialport_api.SerialPortFinder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SerialPortPrefsNew: PreferenceFragmentCompat() {

    private var mApplication: App? = null
    private var mSerialPortFinder: SerialPortFinder? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        mApplication = context?.applicationContext as App
        mSerialPortFinder = mApplication!!.mSerialPortFinder

        // Devices
        val devices = findPreference<androidx.preference.ListPreference>("DEVICE")
        val entries: Array<String> = mSerialPortFinder!!.allDevices
        val entryValues: Array<String> = mSerialPortFinder!!.allDevicesPath
        devices?.entries = entries
        devices?.entryValues = entryValues
        devices?.summary = devices?.value
        devices?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue as String
                true
            }

        // Baud rates
        val baudrates = findPreference<androidx.preference.ListPreference>("BAUDRATE")
        baudrates?.summary = baudrates?.value
        baudrates?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                preference.summary = newValue as String
                true
            }
    }

}