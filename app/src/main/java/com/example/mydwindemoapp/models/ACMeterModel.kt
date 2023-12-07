package com.example.mydwindemoapp.models

data class ACMeterModel(
    val id: Int,
    val voltageL1: Int,
    val voltageL2: Int,
    val voltageL3: Int,
    val voltageAverage: Int,
    val currentL1: Int,
    val currentL2: Int,
    val currentL3: Int,
    val currentAverage: Int,
    val totalKW: Int,
    val totalKWH: Int,
    val frequency: Int,
    val averagePowerFactor: Int
)
