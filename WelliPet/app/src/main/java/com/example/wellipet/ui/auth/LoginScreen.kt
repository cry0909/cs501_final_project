// File: com/example/wellipet/ui/auth/LoginScreen.kt
package com.example.wellipet.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellipet.data.AuthPreferences.setRememberMe
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import com.example.wellipet.ui.components.CuteTopBar
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.wellipet.R
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // 1) Check Health Connect SDK status
    val providerPackage = "com.google.android.apps.healthdata"
    val sdkStatus = remember {
        HealthConnectClient.getSdkStatus(context, providerPackage)
    }
    LaunchedEffect(sdkStatus) {
        when (sdkStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                // Not installed: open Play Store
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$providerPackage"))
                )
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                // Provider needs update
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        setPackage("com.android.vending")
                        data = Uri.parse("market://details?id=$providerPackage&url=healthconnect%3A%2F%2Fonboarding")
                        putExtra("overlay", true)
                        putExtra("callerId", context.packageName)
                    }
                )
            }
            else -> {
                // SDK_AVAILABLE or other: continue
            }
        }
    }

    // 2) Prepare Health Connect permission request
    val client = HealthConnectClient.getOrCreate(context)
    val REQUIRED_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class)
    )

    // Controls whether to show a "permissions denied" dialog
    var showPermissionsDenied by remember { mutableStateOf(false) }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        // Callback for permission results
        if (!granted.containsAll(REQUIRED_PERMISSIONS)) {
            // At least one required permission was denied
            showPermissionsDenied = true
        }
    }

    // 3) If not yet granted, launch the permission request
    LaunchedEffect(sdkStatus) {
        if (sdkStatus == HealthConnectClient.SDK_AVAILABLE) {
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.containsAll(REQUIRED_PERMISSIONS)) {
                requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }
    }



    // Use LaunchedEffect to collect one‑off navigation events
    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect {
            // 1) Save RememberMe preference
            context.setRememberMe(rememberMe)

            // 2) Get the current user's UID
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@collect

            // 3) Send UID to all paired watch nodes
            Wearable.getNodeClient(context)
                .connectedNodes
                .addOnSuccessListener { nodes ->
                    nodes.forEach { node ->
                        Wearable.getMessageClient(context)
                            .sendMessage(node.id, "/auth-uid", uid.toByteArray())
                            .addOnSuccessListener {
                                Log.d("Phone→Watch", "➡️ uid sent to ${node.displayName}")
                            }
                            .addOnFailureListener {
                                Log.e("Phone→Watch", "sendMessage failed", it)
                            }
                    }
                }

            // 4) Perform actual navigation
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            CuteTopBar(
                title     = "Login",
                fontSize  = 22.sp,
                gradient  = Brush.horizontalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
                elevation = 4f
            )
        }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4F2603),
                        uncheckedColor = Color(0xFF4F2603),
                        checkmarkColor = Color(0xFFFFF3E0)
                    )
                )
                Text("Remember me")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { authViewModel.signIn(email, password) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F2603),    // button bgColor
                    contentColor = Color(0xFFFFF3E0)        // button textColor
                )
            ) {
                Text("Login")
            }
            TextButton(
                onClick = onSignUpClick,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4F2603),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFF4F2603)
                )
            ) {
                Text("No account? Sign Up")
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
                else -> {} // Idle state: no action
            }
        }
    }
}
