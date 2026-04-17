package com.example.myapplication

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun WeatherApp(
    appViewModel: WeatherAppViewModel = viewModel()
) {
    val state by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.networkErrorMessage) {
        val msg = state.networkErrorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            appViewModel.consumeNetworkError()
        }
    }

    MyApplicationTheme(darkTheme = state.isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (currentDestination?.route != AppRoutes.DETAIL) {
                        BottomNavigationBar(
                            currentDestination = currentDestination?.route ?: AppRoutes.HOME,
                            labels = state.labels,
                            onNavigate = { route ->
                                navController.navigateTopLevel(route)
                            }
                        )
                    }
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.HOME,
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = { fadeIn(animationSpec = tween(0)) },
                        exitTransition = { fadeOut(animationSpec = tween(0)) },
                        popEnterTransition = { fadeIn(animationSpec = tween(0)) },
                        popExitTransition = { fadeOut(animationSpec = tween(0)) }
                    ) {
                    composable(AppRoutes.HOME) {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            city = state.homeCity,
                            favorite = state.favorites.firstOrNull { it.cityId == state.homeCity.id },
                            labels = state.labels,
                            language = state.language,
                            isDark = state.isDark,
                            onAddToFavorites = appViewModel::addFavorite,
                            onOpenDetail = { cityId -> navController.navigate(AppRoutes.detail(cityId)) }
                        )
                    }

                    composable(
                        route = AppRoutes.DETAIL,
                        arguments = listOf(navArgument("cityId") { type = NavType.StringType })
                    ) { entry ->
                        val cityId = entry.arguments?.getString("cityId") ?: WeatherCatalog.HOME_CITY_ID
                        val city = state.cities.firstOrNull { it.id == cityId } ?: state.homeCity
                        val favorite = state.favorites.firstOrNull { it.cityId == city.id }

                        DetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            city = city,
                            favorite = favorite,
                            labels = state.labels,
                            language = state.language,
                            isDark = state.isDark,
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigateTopLevel(AppRoutes.HOME)
                                }
                            },
                            onOpenMap = { navController.navigateTopLevel(AppRoutes.MAP) },
                            onAddToFavorites = appViewModel::addFavorite,
                            onSaveNote = appViewModel::updateFavoriteNote
                        )
                    }

                    composable(AppRoutes.MAP) {
                        MapScreen(
                            modifier = Modifier.fillMaxSize(),
                            cities = state.cities,
                            favorites = state.favorites,
                            homeCityId = state.homeCityId,
                            labels = state.labels,
                            language = state.language,
                            isDark = state.isDark,
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigateTopLevel(AppRoutes.HOME)
                                }
                            },
                            onOpenDetail = { cityId -> navController.navigate(AppRoutes.detail(cityId)) },
                            onDeleteFavorite = appViewModel::deleteFavorite,
                            onSelectHomeCatalog = appViewModel::setHomeCityCatalog,
                            onSelectHomeCustom = appViewModel::setHomeCityCustom
                        )
                    }

                    composable(AppRoutes.FAVORITES) {
                        FavoritesScreen(
                            modifier = Modifier.fillMaxSize(),
                            favorites = state.favorites,
                            labels = state.labels,
                            language = state.language,
                            isDark = state.isDark,
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigateTopLevel(AppRoutes.HOME)
                                }
                            },
                            onOpenDetail = { cityId -> navController.navigate(AppRoutes.detail(cityId)) },
                            onSelectHome = appViewModel::setHomeCityCatalog,
                            onDeleteFavorite = appViewModel::deleteFavorite,
                            onUpdateNote = appViewModel::updateFavoriteNote
                        )
                    }

                    composable(AppRoutes.SETTINGS) {
                        SettingsScreen(
                            modifier = Modifier.fillMaxSize(),
                            labels = state.labels,
                            language = state.language,
                            theme = state.theme,
                            isDark = state.isDark,
                            mapKeyRows = state.mapKeyRows,
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigateTopLevel(AppRoutes.HOME)
                                }
                            },
                            onLanguageChange = appViewModel::setLanguage,
                            onThemeChange = appViewModel::setTheme,
                            onSetActiveMapKey = appViewModel::setActiveMapApiKey,
                            onAddMapKey = appViewModel::addMapApiKey,
                            onRemoveMapKey = appViewModel::removeMapApiKey
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentDestination: String,
    labels: AppStrings,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        val items = listOf(
            Triple(AppRoutes.HOME, Icons.Default.Home, labels.home),
            Triple(AppRoutes.MAP, Icons.Default.Map, labels.map),
            Triple(AppRoutes.FAVORITES, Icons.Default.Favorite, labels.favorites),
            Triple(AppRoutes.SETTINGS, Icons.Default.Settings, labels.settings)
        )

        items.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentDestination == route,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = { Text(label) }
            )
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
