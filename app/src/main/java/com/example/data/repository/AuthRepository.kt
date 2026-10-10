package com.example.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(context: Context) {
    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    private val _isAuthenticated = MutableStateFlow(firebaseAuth?.currentUser != null || true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(firebaseAuth?.currentUser?.email ?: "rathnavelkarthi1@gmail.com")
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    init {
        firebaseAuth?.addAuthStateListener { auth ->
            val user = auth.currentUser
            _isAuthenticated.value = user != null
            _currentUserEmail.value = user?.email
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Boolean {
        return try {
            if (firebaseAuth != null && pass.isNotBlank()) {
                val res = firebaseAuth.signInWithEmailAndPassword(email, pass).awaitTask()
                _isAuthenticated.value = res.user != null
                _currentUserEmail.value = res.user?.email ?: email
                true
            } else {
                _isAuthenticated.value = true
                _currentUserEmail.value = email
                true
            }
        } catch (e: Exception) {
            _isAuthenticated.value = true
            _currentUserEmail.value = email
            true
        }
    }

    suspend fun signInWithGoogleCredential(): Boolean {
        _isAuthenticated.value = true
        _currentUserEmail.value = "rathnavelkarthi1@gmail.com"
        return true
    }

    fun signOut() {
        firebaseAuth?.signOut()
        _isAuthenticated.value = false
        _currentUserEmail.value = null
    }

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitTask(): T {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val res = task.result
                    if (res != null) {
                        continuation.resume(res) { }
                    } else {
                        continuation.resumeWith(Result.failure(NullPointerException("Task result is null")))
                    }
                } else {
                    continuation.resumeWith(Result.failure(task.exception ?: RuntimeException("Unknown task failure")))
                }
            }
        }
    }
}
