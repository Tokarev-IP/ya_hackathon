package com.it.ya_hackathon.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

class UserAuthenticator : UserAuthenticatorInterface {

    private val firebaseAuth = Firebase.auth

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun authenticateAnonymousUser() {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener { result ->
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

}

interface UserAuthenticatorInterface {
    fun getCurrentUser(): FirebaseUser?
    suspend fun authenticateAnonymousUser()
}