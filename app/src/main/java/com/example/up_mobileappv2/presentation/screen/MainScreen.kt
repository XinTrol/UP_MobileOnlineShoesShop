package com.example.up_mobileappv2.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.up_mobileappv2.presentation.navigation.MainTab
import com.example.up_mobileappv2.presentation.screen.CatalogScreen
import com.example.up_mobileappv2.presentation.screen.HomeScreen
import com.example.up_mobileappv2.presentation.screen.ProfileScreen
import com.example.up_mobileappv2.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModel: MainViewModel = hiltViewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Заголовок меню
                Text("Меню", modifier = Modifier.padding(16.dp))
                Divider()
                // Пункты меню
                NavigationDrawerItem(
                    label = { Text("Профиль") },
                    selected = false,
                    onClick = {
                        // Закрыть drawer и перейти на Profile (если нужно)
                        navController.navigate(MainTab.Profile.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Избранное") },
                    selected = false,
                    onClick = {
                        // Переход на экран избранного (пока нет)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Выйти") },
                    selected = false,
                    onClick = {
                        viewModel.logout {
                            onLogout()
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UP Shoe Store") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = MainTab.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                addMainTab(MainTab.Home, navController) { HomeScreen(navController) }
                addMainTab(MainTab.Catalog, navController) { CatalogScreen(navController) }
                addMainTab(MainTab.Profile, navController) { ProfileScreen(navController) }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val tabs = listOf(MainTab.Home, MainTab.Catalog, MainTab.Profile)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        tabs.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon(), contentDescription = tab.title) },
                label = { Text(tab.title) },
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.addMainTab(
    tab: MainTab,
    navController: NavController,
    content: @Composable () -> Unit
) {
    composable(route = tab.route) {
        content()
    }
}

// Вспомогательная функция для получения иконки (можно добавить в сам MainTab)
fun MainTab.icon() = when (this) {
    MainTab.Home -> Icons.Default.Home
    MainTab.Catalog -> Icons.Default.ShoppingCart
    MainTab.Profile -> Icons.Default.Person
}