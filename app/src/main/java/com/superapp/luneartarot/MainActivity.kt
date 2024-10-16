package com.superapp.luneartarot

import SplashScreen
import YesOrNoScreen
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.superapp.luneartarot.data.CardOfDay
import com.superapp.luneartarot.data.CardRepository
import com.superapp.luneartarot.ui.screens.*
import com.superapp.luneartarot.ui.theme.LunearTarotTheme
import com.superapp.luneartarot.viewmodel.SettingsViewModel
import com.superapp.luneartarot.workers.DailyCardNotificationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var cardRepository: CardRepository
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var settingsViewModel: SettingsViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        cardRepository = CardRepository(this)
        mediaPlayer = MediaPlayer.create(this, R.raw.midnight_forest)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(resources.openRawResourceFd(R.raw.midnight_forest))
            isLooping = true
            prepare()
        }

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel.onMusicEnabledChanged = { enabled ->
            if (enabled) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }
        initializeDailyNotification()

        setContent {
            LunearTarotTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showNavBar by remember { mutableStateOf(false) }
                val navController = rememberNavController()
                val bottomBarColor = Color(0xFF1D3450)
                val isMusicEnabled by settingsViewModel.isMusicEnabled.collectAsState()

                LaunchedEffect(isMusicEnabled) {
                    if (isMusicEnabled) {
                        mediaPlayer?.start()
                    } else {
                        mediaPlayer?.pause()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = !showSplash,
                        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(1000)),
                        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(1000))
                    ) {
                        Scaffold(
                            bottomBar = {
                                if (showNavBar) {
                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                                    val currentRoute = navBackStackEntry?.destination?.route
                                    NavigationBar(
                                        containerColor = bottomBarColor,
                                        modifier = Modifier.height(64.dp)
                                    ) {
                                        val items = listOf(
                                            Screen.CardOfDay,
                                            Screen.YesOrNo,
                                            Screen.Spread,
                                            Screen.Settings
                                        )
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
                                                            text = stringResource(id = screen.title),
                                                            color = if (currentRoute == screen.route) Color.White else Color.Gray,
                                                            fontSize = 14.sp
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
                                startDestination = Screen.CardOfDay.route,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable(Screen.CardOfDay.route) {
                                    MainScreen(
                                        onCardOfDayClick = { cardOfDay ->
                                            navController.navigate("card_detail/${cardOfDay.card.id}/${cardOfDay.isUpright}")
                                        },
                                        cardRepository = cardRepository
                                    )
                                }
                                composable(Screen.YesOrNo.route) {
                                    YesOrNoScreen(
                                        cardRepository = cardRepository,
                                    )
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

                    AnimatedVisibility(
                        visible = showSplash,
                        enter = fadeIn(),
                        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(1000))
                    ) {
                        SplashScreen {
                            showSplash = false
                            lifecycleScope.launch {
                                delay(1000)
                                showNavBar = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initializeDailyNotification() {
        DailyCardNotificationWorker.schedule(this)
    }

    override fun onResume() {
        super.onResume()
        if (settingsViewModel.isMusicEnabled.value) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
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

sealed class Screen(val route: String, val title: Int, val iconResourceId: Int) {
    object CardOfDay : Screen("card_of_day", R.string.daily_card, R.drawable.ic_card_of_day)
    object YesOrNo : Screen("yes_or_no", R.string.yes_no, R.drawable.ic_yes_or_no)
    object Spread : Screen("spread", R.string.spread, R.drawable.ic_spread)
    object Settings : Screen("settings", R.string.settings, R.drawable.ic_settings)
}
