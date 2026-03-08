package com.example.up_mobileappv2.presentation.navigation

sealed class MainTab(val route: String, val title: String, val icon: Int) {
    object Home : MainTab("home_tab", "Главная", 0) // иконку добавим позже
    object Catalog : MainTab("catalog_tab", "Каталог", 0)
    object Profile : MainTab("profile_tab", "Профиль", 0)
}