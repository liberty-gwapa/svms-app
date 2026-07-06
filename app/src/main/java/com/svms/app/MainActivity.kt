package com.svms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.svms.app.navigation.Screen
import com.svms.app.presentation.history.HistoryScreen
import com.svms.app.presentation.login.LoginScreen
import com.svms.app.presentation.profile.ProfileScreen
import com.svms.app.presentation.shared.SVMSTheme
import com.svms.app.presentation.splash.SplashScreen
import com.svms.app.presentation.violation.AddViolationScreen
import com.svms.app.presentation.violation.details.ViolationDetailsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SVMSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SVMSNavGraph()
                }
            }
        }
    }
}

@Composable
fun SVMSNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNextScreen = { isLoggedIn ->
                    val nextRoute = if (isLoggedIn) {
                        Screen.AddViolation.createRoute("none")
                    } else {
                        Screen.Login.route
                    }
                    navController.navigate(nextRoute) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.AddViolation.createRoute("none")) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddViolation.route) {
            AddViolationScreen(
                onViolationAdded = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToScan = {
                    navController.navigate(Screen.AddViolation.createRoute("none"))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToDetails = { violationId ->
                    navController.navigate(Screen.ViolationDetails.createRoute(violationId))
                }
            )
        }

        composable(Screen.ViolationDetails.route) {
            ViolationDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
