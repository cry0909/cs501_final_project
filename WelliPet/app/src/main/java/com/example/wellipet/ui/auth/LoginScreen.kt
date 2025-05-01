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
import com.example.wellipet.data.AuthPreferences.rememberMeFlow
import com.example.wellipet.data.AuthPreferences.setRememberMe
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect

import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord

@OptIn(ExperimentalMaterial3Api::class)
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

    // 1) 检查 Health Connect SDK 状态
    val providerPackage = "com.google.android.apps.healthdata"
    val sdkStatus = remember {
        HealthConnectClient.getSdkStatus(context, providerPackage)
    }
    LaunchedEffect(sdkStatus) {
        when (sdkStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                // 未安装：跳到 Play 商店
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$providerPackage"))
                )
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                // 需要更新 provider
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
                // SDK_AVAILABLE 或其他，都可继续
            }
        }
    }

    // 2) 准备 Health Connect 权限请求
    val client = HealthConnectClient.getOrCreate(context)
    val REQUIRED_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class)
    )
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        // 授权结果回调
//        if (!granted.containsAll(REQUIRED_PERMISSIONS)) {
//            // TODO: 提示用户必须授权才能继续
//        }
    }

    // 3) 如果还没授权，就发起一次请求
    LaunchedEffect(sdkStatus) {
        if (sdkStatus == HealthConnectClient.SDK_AVAILABLE) {
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.containsAll(REQUIRED_PERMISSIONS)) {
                requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }
    }



    // 利用 LaunchedEffect 收集一次性導航事件
    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect {
            // 存储用户的 “Remember me” 选择
            context.setRememberMe(rememberMe)
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("WelliPet - Login") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    onCheckedChange = { rememberMe = it }
                )
                Text("Remember me")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { authViewModel.signIn(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            TextButton(onClick = onSignUpClick) {
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
                else -> {} // Idle 狀態不作處理
            }
        }
    }
}
