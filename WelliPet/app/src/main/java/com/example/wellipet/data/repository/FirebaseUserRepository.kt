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

    /** 变更监听 Flow helpers **/
    private fun <T> listenField(field: String, cast: (Any?) -> T?): Flow<T?> = callbackFlow {
        val docRef = try { userDoc() } catch(e:Exception){
            trySend(null); close(); return@callbackFlow
        }
        val sub = docRef.addSnapshotListener { snap, err ->
            if (err != null) {
                // 如果是权限不足，就发一个 null，继续监听
                if (err is FirebaseFirestoreException && err.code == PERMISSION_DENIED) {
                    trySend(null)
                    return@addSnapshotListener
                }
                // 其他错误才关闭
                close(err)
                return@addSnapshotListener
            }
            // 正常流程
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

    /** 已解锁徽章 Flow */
    fun unlockedBadgesFlow(): Flow<Set<String>> =
        listenField("unlockedBadges") { raw ->
            (raw as? List<*>)?.filterIsInstance<String>()?.toSet()
        }.map { it ?: emptySet() }

    /** petStatus 流 */
    fun petStatusFlow(): Flow<String?> =
        listenField("petStatus") { raw -> raw as? String }
            .map { it ?: "happy" } // 默认 happy

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

    /** 保存已解锁徽章 */
    suspend fun saveUnlockedBadges(badges: Set<String>) =
        updateField("unlockedBadges", badges.toList())

    /** 保存 petStatus **/
    suspend fun savePetStatus(status: String) =
        updateField("petStatus", status)
}
