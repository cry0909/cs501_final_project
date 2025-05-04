// File: com/example/wellipet/data/repository/FirebaseUserRepository.kt
package com.example.wellipet.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.map

class FirebaseUserRepository {
    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun userDoc() = auth.currentUser
        ?.let { firestore.collection("users").document(it.uid) }
        ?: throw IllegalStateException("User not signed in")

    /** Helpers for listening to field changes via Flow (AI reference)**/
    private fun <T> listenField(field: String, cast: (Any?) -> T?): Flow<T?> = callbackFlow {
        val docRef = try { userDoc() } catch(e:Exception){
            trySend(null); close(); return@callbackFlow
        }
        val sub = docRef.addSnapshotListener { snap, err ->
            if (err != null) {
                // If the error is permission-denied, send null and continue listening
                if (err is FirebaseFirestoreException && err.code == PERMISSION_DENIED) {
                    trySend(null)
                    return@addSnapshotListener
                }
                //  For other errors, close the Flow
                close(err)
                return@addSnapshotListener
            }
            // Normal flow
            trySend(cast(snap?.get(field)))
        }
        awaitClose { sub.remove() }
    }

    /** Flow of selectedPet */
    fun selectedPetFlow(): Flow<String?> =
        listenField("selectedPet") { raw -> raw as? String }

    /** Flow of selectedBackground */
    fun selectedBackgroundFlow(): Flow<String?> =
        listenField("selectedBackground") { raw -> raw as? String }

    /** Flow of selectedBadges */
    fun selectedBadgesFlow(): Flow<Set<String>> = listenField("selectedBadges") { raw ->
        (raw as? List<*>)?.filterIsInstance<String>()?.toSet()
    }.map { it ?: emptySet() }

    /** Flow of unlocked badges */
    fun unlockedBadgesFlow(): Flow<Set<String>> =
        listenField("unlockedBadges") { raw ->
            (raw as? List<*>)?.filterIsInstance<String>()?.toSet()
        }.map { it ?: emptySet() }

    /** Flow of petStatus */
    fun petStatusFlow(): Flow<String?> =
        listenField("petStatus") { raw -> raw as? String }
            .map { it ?: "happy" } // 默认 happy

    /** Helper for updating a single field **/
    private suspend fun updateField(field: String, value: Any?) {
        userDoc().update(field, value).await()
    }

    /** Save selectedPet **/
    suspend fun saveSelectedPet(name: String) =
        updateField("selectedPet", name)

    /** Save selectedBackground **/
    suspend fun saveSelectedBackground(name: String) =
        updateField("selectedBackground", name)

    /** Save selectedBadges **/
    suspend fun saveSelectedBadges(badges: Set<String>) =
        updateField("selectedBadges", badges.toList())

    /** Save unlockedBadges */
    suspend fun saveUnlockedBadges(badges: Set<String>) =
        updateField("unlockedBadges", badges.toList())

    /** Save petStatus **/
    suspend fun savePetStatus(status: String) =
        updateField("petStatus", status)
}
