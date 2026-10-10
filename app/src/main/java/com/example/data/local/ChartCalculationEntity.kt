package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart_calculations")
data class ChartCalculationEntity(
    @PrimaryKey val chartId: String,
    val birthProfileId: String,
    val tradition: String,
    val ayanamsha: String,
    val engineName: String,
    val engineVersion: String,
    val calculatedAt: String,
    val sunSign: String,
    val moonSign: String,
    val lagnaSign: String?,
    val nakshatra: String,
    val pada: Int,
    val placementsJson: String,
    val isTimeUncertain: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
