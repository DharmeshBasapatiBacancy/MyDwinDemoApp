package com.example.mydwindemoapp.models

data class GunsDCMeterModel(
    val id: Int,
    val voltage: Float,
    val current: Float,
    val power: Float,
    val importEnergy: Float,
    val exportEnergy: Float,
    val maxVoltage: Float,
    val minVoltage: Float,
    val maxCurrent: Float,
    val minCurrent: Float,
)
