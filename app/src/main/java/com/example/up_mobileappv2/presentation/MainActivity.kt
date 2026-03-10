package com.example.up_mobileappv2.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.up_mobileappv2.ui.theme.UP_MobileAppV2Theme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.up_mobileappv2.presentation.navigation.Screen
import com.example.up_mobileappv2.presentation.screen.FavouriteScreen
import com.example.up_mobileappv2.presentation.screen.HomeScreen
import com.example.up_mobileappv2.presentation.screen.LoyaltyCardScreen
import com.example.up_mobileappv2.presentation.screen.MainScreen
import com.example.up_mobileappv2.presentation.screen.auth.CreateNewPasswordScreen
import com.example.up_mobileappv2.presentation.screen.auth.ForgotPasswordScreen
import com.example.up_mobileappv2.presentation.screen.auth.RegisterScreen
import com.example.up_mobileappv2.presentation.screen.auth.SignInScreen
import com.example.up_mobileappv2.presentation.screen.auth.VerificationScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UP_MobileAppV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Register.route
    ) {
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.SignIn.route) {
            SignInScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            MainScreen(
                onLogout = {
                    navController.popBackStack()
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFavourite = {
                    navController.navigate(Screen.Favourite.route)
                }
            )
        }
        composable(Screen.Verification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationScreen(navController = navController, email = email)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Screen.CreateNewPassword.route) {
            CreateNewPasswordScreen(navController = navController)
        }
        composable(Screen.LoyaltyCard.route) {
            LoyaltyCardScreen(navController = navController)
        }
        composable(Screen.Favourite.route) {
            FavouriteScreen(navController = navController)
        }
    }
}