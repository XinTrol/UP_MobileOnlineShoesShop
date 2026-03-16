package com.example.up_mobileappv2.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.up_mobileappv2.presentation.navigation.Screen
import com.example.up_mobileappv2.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    outerNavController: NavHostController,
    onLogout: () -> Unit
) {
    val innerNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModel: MainViewModel = hiltViewModel()
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF4DA3C7)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(40.dp))

                    // Фото профиля
                    if (profile?.photo != null) {
                        AsyncImage(
                            model = profile!!.photo,
                            contentDescription = "Фото профиля",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(90.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    val fullName = buildString {
                        profile?.firstName?.let { append(it) }
                        if (profile?.lastName != null) {
                            if (isNotEmpty()) append(" ")
                            append(profile!!.lastName)
                        }
                    }
                    Text(
                        text = fullName.ifEmpty { "Имя не указано" },
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(32.dp))

                    NavigationDrawerItem(
                        label = { Text("Профиль", color = Color.White) },
                        selected = false,
                        onClick = {
                            innerNavController.navigate(Screen.ProfileTab.route) {
                                popUpTo(Screen.HomeTab.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Person, null, tint = Color.White) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Избранное", color = Color.White) },
                        selected = false,
                        onClick = {
                            innerNavController.navigate(Screen.FavouriteTab.route) {
                                popUpTo(Screen.HomeTab.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.FavoriteBorder, null, tint = Color.White) }
                    )
                    Spacer(Modifier.height(32.dp))
                    Divider(color = Color.White.copy(alpha = 0.4f))
                    Spacer(Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { Text("Выйти", color = Color.White) },
                        selected = false,
                        onClick = {
                            viewModel.logout { onLogout() }
                        },
                        icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.White) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UP Shoe Store") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(innerNavController)
            }
        ) { paddingValues ->
            NavHost(
                navController = innerNavController,
                startDestination = Screen.HomeTab.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.HomeTab.route) {
                    HomeScreen(navController = outerNavController)
                }
                composable(Screen.FavouriteTab.route) {
                    FavouriteScreen(navController = outerNavController)
                }
                composable(Screen.ProfileTab.route) {
                    ProfileScreen(navController = outerNavController)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val tabs = listOf(
        Triple(Screen.HomeTab, "Главная", Icons.Default.Home),
        Triple(Screen.FavouriteTab, "Избранное", Icons.Default.FavoriteBorder),
        Triple(Screen.ProfileTab, "Профиль", Icons.Default.Person)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        tabs.forEach { (tab, title, icon) ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(Screen.HomeTab.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = title) },
                label = { Text(title) }
            )
        }
    }
}