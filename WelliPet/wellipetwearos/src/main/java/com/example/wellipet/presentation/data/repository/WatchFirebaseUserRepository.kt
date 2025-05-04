package com.example.wellipet.presentation.data.repository

import android.app.Application
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.wellipet.data.dataStore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

private val UID_KEY = stringPreferencesKey("user_uid")

class WatchFirebaseUserRepository(private val app: Application) {
    private val firestore = FirebaseFirestore.getInstance()

    /** Retrieve uid from DataStore **/
    val uidFlow: Flow<String?> = app.dataStore.data
        .map { it[UID_KEY] }

    /** petStatus **/
    fun petStatusFlow(): Flow<String> = uidFlow.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf("happy")
        else callbackFlow {
            val sub = firestore.collection("users").document(uid)
                .addSnapshotListener { snap, err ->
                    if (err != null) close(err)
                    else trySend(snap?.getString("petStatus") ?: "happy")
                }
            awaitClose { sub.remove() }
        }
    }

    /** selectedPet **/
    fun selectedPetFlow(): Flow<String?> = uidFlow.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null)
        else callbackFlow {
            val sub = firestore.collection("users").document(uid)
                .addSnapshotListener { snap, _ ->
                    trySend(snap?.getString("selectedPet"))
                }
            awaitClose { sub.remove() }
        }
    }

    /** selectedBackground **/
    fun selectedBackgroundFlow(): Flow<String?> = uidFlow.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null)
        else callbackFlow {
            val sub = firestore.collection("users").document(uid)
                .addSnapshotListener { snap, _ ->
                    trySend(snap?.getString("selectedBackground"))
                }
            awaitClose { sub.remove() }
        }
    }

    /** selectedBadges  **/
    fun selectedBadgesFlow(): Flow<Set<String>> = uidFlow.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(emptySet())
        else callbackFlow {
            val sub = firestore.collection("users").document(uid)
                .addSnapshotListener { snap, _ ->
                    val list = (snap?.get("selectedBadges") as? List<*>)
                        ?.filterIsInstance<String>()
                        ?: emptyList()
                    trySend(list.toSet())
                }
            awaitClose { sub.remove() }
        }
    }
}
