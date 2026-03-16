package com.example.up_mobileappv2.presentation.navigation

sealed class Screen(val route: String) {
    // Аутентификация
    object Register : Screen("register")
    object SignIn : Screen("sign_in")
    object ForgotPassword : Screen("forgot_password")
    object Verification : Screen("verification/{email}") {
        fun createRoute(email: String) = "verification/$email"
    }
    object CreateNewPassword : Screen("create_new_password")

    // Главный экран (контейнер)
    object Home : Screen("home")

    // Вкладки внутри главного экрана
    object HomeTab : Screen("home_tab")
    object FavouriteTab : Screen("favourite_tab")
    object ProfileTab : Screen("profile_tab")

    // Другие полноэкранные экраны
    object LoyaltyCard : Screen("loyalty_card")
    object Catalog : Screen("catalog")
}