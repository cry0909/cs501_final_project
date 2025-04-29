// File: com/example/wellipet/data/repository/FirebaseBadgeRepository.kt
package com.example.wellipet.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseBadgeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userDoc() = auth.currentUser
        ?.let { firestore.collection("users").document(it.uid) }
        ?: throw IllegalStateException("User not signed in")

    fun selectedBadgesFlow(): Flow<Set<String>> = callbackFlow {
        val docRef = try { userDoc() } catch(_:Exception) {
            trySend(emptySet()); close(); return@callbackFlow
        }
        val listener = docRef.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            val arr = snap?.get("selectedBadges") as? List<*>
            trySend(arr?.filterIsInstance<String>()?.toSet() ?: emptySet())
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveSelectedBadges(badges: Set<String>) {
        userDoc()
            .update("selectedBadges", badges.toList())
            .await()
    }

}
