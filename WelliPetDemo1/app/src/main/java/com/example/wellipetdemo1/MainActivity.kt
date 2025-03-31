package com.example.wellipetdemo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.layout.ContentScale


sealed class Screen(val route: String, val label: String) {
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Sign Up")
    object Home : Screen("home", "Home")
    object HealthData : Screen("healthData", "Health Data")
    object Store : Screen("store", "Store")
    object World : Screen("world", "World")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()

        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    // 假設使用一個簡單的狀態來判斷是否登入
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    // 這裡可以進行註冊邏輯後直接登入
                    isLoggedIn = true
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        // Home 畫面，包含 BottomNavigation
        composable(Screen.Home.route) {
            HomeScreenWithBottomNav(navController)
        }
        // Health Data 頁面
        composable(Screen.HealthData.route) {
            HealthDataScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        // Pet Store 頁面
        composable(Screen.Store.route) {
            StoreScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WelliPet - Login") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // 這裡可進行實際的驗證邏輯
                onLoginSuccess()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
            TextButton(onClick = onSignUpClick) {
                Text("No account? Sign Up")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WelliPet - Sign Up") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // 這裡可進行實際的註冊邏輯
                onSignUpSuccess()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Sign Up")
            }
            TextButton(onClick = onBackToLogin) {
                Text("Back to Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithBottomNav(navController: NavHostController) {
    // 使用 Scaffold + BottomBar 來實現底部導覽
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WelliPet - Home") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        // 寵物主畫面內容
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 可以在這裡放背景圖片

            Image(
                painter = painterResource(id = R.drawable.bg_home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


            // 寵物顯示區 (示範：單一圖片或 GIF / Lottie)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                 假設這裡是靜態圖片
                Image(
                    painter = painterResource(id = R.drawable.pet_dog),
                    contentDescription = "Pet",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
                Text("Feeling Happy", style = MaterialTheme.typography.headlineSmall)

                // 可再加入一些進度條或狀態指示
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Health progress: Steps / Sleep / Water ...")
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.HealthData,
        Screen.Store
    )
    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                        Screen.HealthData -> Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health Data"
                        )
                        Screen.Store -> Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Store"
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Default"
                        )
                    }
                },
                label = { Text(screen.label) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier,
                enabled = true,
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(),
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelliPet - Health Data") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Health Data Overview (Charts / Lists)")
            // 後續可整合 Health Connect 讀取數據並呈現
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WelliPet - Store") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pet Store Items")
            // 可以放一些商品列表 (e.g. 虛擬裝飾品、飼料、道具...)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreenWithBottomNav(navController)
}
//fun LoginScreenPreview() {
//    LoginScreen(
//        onLoginSuccess = { /* Handle success */ },
//        onSignUpClick = { /* Handle sign up */ }
//    )}
//fun SignUpScreenPreview() {
//    SignUpScreen(
//
//        onSignUpSuccess = { /* Handle success */ },
//        onBackToLogin = { /* Handle back to login */ }
//    )
//}