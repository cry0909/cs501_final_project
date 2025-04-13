// File: com/example/wellipet/ui/auth/AuthViewModel.kt
package com.example.wellipet.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
                _authState.value = AuthState.Success(result.user!!)
                // 這裡未來可以額外設定例如用戶等級的初始化資料
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                viewModelScope.launch {
                    _navigationEvent.emit(Unit)
                }
            }
    }
}
