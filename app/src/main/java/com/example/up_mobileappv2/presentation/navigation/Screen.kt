package com.example.up_mobileappv2.presentation.navigation

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object SignIn : Screen("sign_in")
    object ForgotPassword : Screen("forgot_password")
    object Verification : Screen("verification/{email}") {
        fun createRoute(email: String) = "verification/$email"
    }
    object Favourite : Screen("favourite")
    object CreateNewPassword : Screen("create_new_password")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")
    object LoyaltyCard : Screen("loyalty_card")

}