package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth?
) {
    companion object {
        private const val TAG = "AuthRepository"

        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val fbAuth = try {
                        FirebaseAuth.getInstance()
                    } catch (e: Exception) {
                        null
                    }
                    AuthRepository(fbAuth).also { instance = it }
                }
            }
        }
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(auth?.currentUser != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        auth?.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            _isAuthenticated.value = user != null
            Log.d(TAG, "Auth state updated: user=${user?.uid}, email=${user?.email}")
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth?.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth?.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth?.currentUser?.email
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        return try {
            val result = fbAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: return Result.failure(IllegalStateException("User is null after sign in"))
            _currentUser.value = user
            _isAuthenticated.value = true
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed for $email", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        return try {
            val result = fbAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: return Result.failure(IllegalStateException("User is null after sign up"))
            _currentUser.value = user
            _isAuthenticated.value = true
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up failed for $email", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            return Result.failure(IllegalStateException("Missing default_web_client_id in resources"))
        }

        return try {
            val credentialManager = CredentialManager.create(context)
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = fbAuth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: return Result.failure(IllegalStateException("FirebaseUser is null after Google credential"))
                _currentUser.value = user
                _isAuthenticated.value = true

                // Immediately check Firestore and cache existing birth profile
                try {
                    val bpRepo = BirthProfileRepository.getInstance(context)
                    val fetchResult = bpRepo.fetchProfiles()
                    if (fetchResult.isSuccess) {
                        val profiles = fetchResult.getOrDefault(emptyList())
                        if (profiles.isNotEmpty()) {
                            val mainRepo = JyotishaRepository.getInstance()
                            profiles.forEach { profile ->
                                mainRepo.saveProfile(profile)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to auto-cache birth profiles after Google Sign-in", e)
                }

                Result.success(user)
            } else {
                Result.failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-in was cancelled by user")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error during Google Sign-in", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-in", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
        _isAuthenticated.value = false
    }
}
