package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JyotishaDao {
    @Query("SELECT * FROM birth_profiles ORDER BY isDefault DESC, createdAt ASC")
    fun getAllProfilesFlow(): Flow<List<BirthProfileEntity>>

    @Query("SELECT * FROM birth_profiles ORDER BY isDefault DESC, createdAt ASC")
    suspend fun getAllProfiles(): List<BirthProfileEntity>

    @Query("SELECT * FROM birth_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): BirthProfileEntity?

    @Query("SELECT * FROM birth_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): BirthProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BirthProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: BirthProfileEntity)

    @Query("DELETE FROM birth_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)

    @Query("DELETE FROM chart_calculations WHERE birthProfileId = :profileId")
    suspend fun deleteChartsByProfileId(profileId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChart(chart: ChartCalculationEntity)

    @Query("SELECT * FROM chart_calculations WHERE birthProfileId = :profileId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestChartForProfile(profileId: String): ChartCalculationEntity?
}
