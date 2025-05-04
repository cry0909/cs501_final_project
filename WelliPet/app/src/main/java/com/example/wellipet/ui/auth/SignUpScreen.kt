// File: com/example/wellipet/ui/auth/SignUpScreen.kt
package com.example.wellipet.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.R
import com.example.wellipet.ui.components.CuteTopBar

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    // Navigate on successful sign‑up
    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect {
            onSignUpSuccess()
        }
    }

    Scaffold(
        topBar = {
            CuteTopBar(
                title     = "Sign Up",
                fontSize  = 22.sp,
                gradient  = Brush.horizontalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
                elevation = 4f
            )        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.welli_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 32.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (password == confirmPassword && email.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.signUp(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F2603),    // button bgColor
                    contentColor = Color(0xFFFFF3E0)        // button textColor
                )
            ) {
                Text("Sign Up")
            }
            TextButton(
                onClick = onBackToLogin,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4F2603),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFF4F2603)
                )
            ) {
                Text("Back to Login")
            }
            when (authState) {
                is AuthState.Loading -> {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Spacer(Modifier.height(16.dp))
                    Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }
}
