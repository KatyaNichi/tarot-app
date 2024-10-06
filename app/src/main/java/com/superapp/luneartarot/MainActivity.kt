package com.superapp.luneartarot

import SplashScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.superapp.luneartarot.data.CardOfDay
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.ui.screens.*
import com.superapp.luneartarot.ui.theme.LunearTarotTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var cardRepository: CardRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardRepository = CardRepository(this)

        setContent {
            LunearTarotTheme {
                val navController = rememberNavController()
                val bottomBarColor = Color(0xFF1D3450)

                val items = listOf(
                    Screen.CardOfDay,
                    Screen.YesOrNo,
                    Screen.Spread,
                    Screen.Settings
                )

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        if (currentRoute != "splash") {
                            NavigationBar(
                                containerColor = bottomBarColor,
                                modifier = Modifier.height(64.dp)
                            ) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    items.forEach { screen ->
                                        CustomNavigationBarItem(
                                            icon = {
                                                Icon(
                                                    painter = painterResource(id = screen.iconResourceId),
                                                    contentDescription = null,
                                                    tint = if (currentRoute == screen.route) Color(0xFFFFFFFF) else Color(0xFF808080)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = screen.title,
                                                    color = if (currentRoute == screen.route) Color.White else Color.Gray
                                                )
                                            },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                            onClick = {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = Color(0xFFFFFFFF),
                                                selectedTextColor = Color(0xFFFFFFFF),
                                                indicatorColor = Color(0xFFD3A69C),
                                                unselectedIconColor = Color(0xFF808080),
                                                unselectedTextColor = Color(0xFF808080)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen {
                                navController.navigate(Screen.CardOfDay.route) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable(Screen.CardOfDay.route) {
                            MainScreen(
                                onCardOfDayClick = { cardOfDay ->
                                    navController.navigate("card_detail/${cardOfDay.card.id}/${cardOfDay.isUpright}")
                                },
                                cardRepository = cardRepository
                            )
                        }
                        composable(Screen.YesOrNo.route) {
                            YesOrNoScreen(cardRepository = cardRepository)
                        }
                        composable(Screen.Spread.route) {
                            SpreadScreen(cardRepository = cardRepository)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen()
                        }
                        composable(
                            route = "card_detail/{cardId}/{isUpright}",
                            arguments = listOf(
                                navArgument("cardId") { type = NavType.IntType },
                                navArgument("isUpright") { type = NavType.BoolType }
                            )
                        ) { backStackEntry ->
                            val cardId = backStackEntry.arguments?.getInt("cardId") ?: 0
                            val isUpright = backStackEntry.arguments?.getBoolean("isUpright") ?: true
                            val cardOfDay = remember(cardId, isUpright) {
                                cardRepository.getCardById(cardId)?.let { card ->
                                    CardOfDay(card, isUpright)
                                }
                            }
                            cardOfDay?.let {
                                CardDetailScreen(cardOfDay = it)
                            } ?: run {
                                Text("Card not found")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CustomNavigationBarItem(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    colors: NavigationBarItemColors
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        label()
    }
}

sealed class Screen(val route: String, val title: String, val iconResourceId: Int) {
    object CardOfDay : Screen("card_of_day", "Daily Card", R.drawable.ic_card_of_day)
    object YesOrNo : Screen("yes_or_no", "Yes/No", R.drawable.ic_yes_or_no)
    object Spread : Screen("spread", "Spread", R.drawable.ic_spread)
    object Settings : Screen("settings", "Settings", R.drawable.ic_settings)
}