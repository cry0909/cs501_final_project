// File: com/example/wellipet/data/repository/FirebaseUserRepository.kt
package com.example.wellipet.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    /** 变更监听 Flow helpers **/
    private fun <T> listenField(field: String, cast: (Any?) -> T?): Flow<T?> = callbackFlow {
        val docRef = try { userDoc() } catch(e:Exception){
            trySend(null); close(); return@callbackFlow
        }
        val sub = docRef.addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(cast(snap?.get(field)))
        }
        awaitClose { sub.remove() }
    }

    /** selectedPet 流 */
    fun selectedPetFlow(): Flow<Int?> = listenField("selectedPet") { raw ->
        (raw as? Long)?.toInt()  // Firestore 数字默认 Long
    }

    /** selectedBackground 流 */
    fun selectedBackgroundFlow(): Flow<Int?> = listenField("selectedBackground") { raw ->
        (raw as? Long)?.toInt()
    }

    /** selectedBadges 流 */
    fun selectedBadgesFlow(): Flow<Set<String>> = listenField("selectedBadges") { raw ->
        (raw as? List<*>)?.filterIsInstance<String>()?.toSet()
    }.map { it ?: emptySet() }

    /** 更新 helper **/
    private suspend fun updateField(field: String, value: Any?) {
        userDoc().update(field, value).await()
    }

    /** 写入 selectedPet **/
    suspend fun saveSelectedPet(resId: Int) =
        updateField("selectedPet", resId)

    /** 写入 selectedBackground **/
    suspend fun saveSelectedBackground(resId: Int) =
        updateField("selectedBackground", resId)

    /** 写入 selectedBadges **/
    suspend fun saveSelectedBadges(badges: Set<String>) =
        updateField("selectedBadges", badges.toList())
}
