// File: com/example/wellipet/ui/auth/AuthViewModel.kt
package com.example.wellipet.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val firestore    = FirebaseFirestore.getInstance()
    val authState: StateFlow<AuthState> = _authState

    // SharedFlow 用於傳遞一次性導航事件
    private val _navigationEvent = MutableSharedFlow<Unit>(replay = 0)
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                _authState.value = AuthState.Success(result.user!!)
                viewModelScope.launch {
                    _navigationEvent.emit(Unit)
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
            }
    }

    fun signUp(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user!!
                // 1) 初始化 Firestore 裡的 users/{uid} 文件
                firestore.collection("users")
                    .document(user.uid)
                    .set(
                        mapOf(
                            "selectedBadges"    to emptyList<String>(),
                            "selectedPet"       to null,
                            "selectedBackground" to null
                        )
                    )
                // 2) 繼續後面的流程
                _authState.value = AuthState.Success(user)
                viewModelScope.launch { _navigationEvent.emit(Unit) }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
            }
    }
}
