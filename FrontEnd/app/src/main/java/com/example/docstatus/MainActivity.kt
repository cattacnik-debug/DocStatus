package com.example.docstatus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.docstatus.data.TokenManager
import com.example.docstatus.ui.history.HistoryScreen
import com.example.docstatus.ui.login.LoginScreen
import com.example.docstatus.ui.scan.ScanScreen
import com.example.docstatus.ui.theme.DocStatusTheme
import com.example.docstatus.utils.BiometricAuthenticator
import com.example.docstatus.utils.SessionManager

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val startDestination = if (sessionManager.isTokenValid()) {
            TokenManager.accessToken = sessionManager.fetchAuthToken()
            "scanner"
        } else {
            "login"
        }

        setContent {
            DocStatusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("scanner") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }
                        composable("scanner") {
                            ScanScreen(
                                onHistoryClick = {
                                    BiometricAuthenticator.showBiometricPrompt(
                                        activity = this@MainActivity,
                                        onSuccess = { navController.navigate("history") },
                                        onError = { error ->
                                            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                                onLogoutClick = {
                                    sessionManager.clearAuthToken()
                                    TokenManager.accessToken = null
                                    navController.navigate("login") {
                                        popUpTo("scanner") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("history") {
                            HistoryScreen(onBackClick = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}