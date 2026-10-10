package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.models.BirthProfile
import com.example.data.models.BirthTimeConfidence
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class BirthProfileRepository(
    private val firestore: FirebaseFirestore?,
    private val auth: FirebaseAuth?
) {
    companion object {
        private const val TAG = "BirthProfileRepo"

        @Volatile
        private var instance: BirthProfileRepository? = null

        fun getInstance(context: Context): BirthProfileRepository {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val dbId = try {
                        context.getString(R.string.firestore_database_id)
                    } catch (e: Exception) {
                        null
                    }
                    val fs = try {
                        if (!dbId.isNullOrBlank()) {
                            FirebaseFirestore.getInstance(dbId)
                        } else {
                            FirebaseFirestore.getInstance()
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to initialize FirebaseFirestore instance", e)
                        null
                    }
                    val fbAuth = try {
                        FirebaseAuth.getInstance()
                    } catch (e: Exception) {
                        null
                    }
                    BirthProfileRepository(fs, fbAuth).also { instance = it }
                }
            }
        }
    }

    private fun getCurrentUserId(): String? {
        return auth?.currentUser?.uid
    }

    suspend fun saveProfile(profile: BirthProfile): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        val fs = firestore ?: return Result.failure(IllegalStateException("Firestore not configured"))

        return try {
            val profileDoc = fs.collection("users")
                .document(uid)
                .collection("birth_profiles")
                .document(profile.id)

            val payload = hashMapOf(
                "id" to profile.id,
                "userId" to uid,
                "name" to profile.name,
                "dateOfBirth" to profile.dateOfBirth,
                "timeOfBirth" to profile.timeOfBirth,
                "timeConfidence" to profile.timeConfidence.name,
                "placeName" to profile.placeName,
                "latitude" to profile.latitude,
                "longitude" to profile.longitude,
                "timezoneId" to profile.timezoneId,
                "isDefault" to profile.isDefault,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            profileDoc.set(payload, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving birth profile ${profile.id} to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun fetchProfiles(): Result<List<BirthProfile>> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        val fs = firestore ?: return Result.failure(IllegalStateException("Firestore not configured"))

        return try {
            val snapshot = fs.collection("users")
                .document(uid)
                .collection("birth_profiles")
                .get()
                .await()

            val profiles = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val name = doc.getString("name") ?: return@mapNotNull null
                val dob = doc.getString("dateOfBirth") ?: ""
                val tob = doc.getString("timeOfBirth") ?: ""
                val confStr = doc.getString("timeConfidence") ?: "EXACT"
                val place = doc.getString("placeName") ?: ""
                val lat = doc.getDouble("latitude") ?: 13.0827
                val lng = doc.getDouble("longitude") ?: 80.2707
                val tz = doc.getString("timezoneId") ?: "Asia/Kolkata (GMT+05:30)"
                val isDef = doc.getBoolean("isDefault") ?: false

                val conf = when (confStr.uppercase()) {
                    "APPROXIMATE" -> BirthTimeConfidence.APPROXIMATE
                    "UNKNOWN" -> BirthTimeConfidence.UNKNOWN
                    else -> BirthTimeConfidence.EXACT
                }

                BirthProfile(
                    id = id,
                    name = name,
                    dateOfBirth = dob,
                    timeOfBirth = tob,
                    timeConfidence = conf,
                    placeName = place,
                    latitude = lat,
                    longitude = lng,
                    timezoneId = tz,
                    isDefault = isDef
                )
            }
            Result.success(profiles)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching birth profiles from Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun checkProfileExists(): Boolean {
        val uid = getCurrentUserId() ?: return false
        val fs = firestore ?: return false
        return try {
            val snapshot = fs.collection("users")
                .document(uid)
                .collection("birth_profiles")
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "Error checking profile existence in Firestore", e)
            false
        }
    }

    suspend fun deleteProfile(profileId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        val fs = firestore ?: return Result.failure(IllegalStateException("Firestore not configured"))

        return try {
            // Delete profile document in Firestore
            fs.collection("users")
                .document(uid)
                .collection("birth_profiles")
                .document(profileId)
                .delete()
                .await()

            // Delete associated readings for this profile
            val readingsSnapshot = fs.collection("users")
                .document(uid)
                .collection("readings")
                .whereEqualTo("profileId", profileId)
                .get()
                .await()

            for (readingDoc in readingsSnapshot.documents) {
                readingDoc.reference.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting birth profile $profileId in Firestore", e)
            Result.failure(e)
        }
    }
}
