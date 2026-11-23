package com.seuapp.cycletrack.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seuapp.cycletrack.presentation.ui.calendar.CalendarScreen
import com.seuapp.cycletrack.presentation.ui.content.ArticleDetailScreen
import com.seuapp.cycletrack.presentation.ui.content.ContentScreen
import com.seuapp.cycletrack.presentation.ui.home.HomeScreen
import com.seuapp.cycletrack.presentation.ui.insights.InsightsScreen
import com.seuapp.cycletrack.presentation.ui.logging.LoggingScreen
import com.seuapp.cycletrack.presentation.ui.messages.ChatScreen
import com.seuapp.cycletrack.presentation.ui.messages.MessagesScreen
import com.seuapp.cycletrack.presentation.ui.onboarding.OnboardingScreen
import com.seuapp.cycletrack.presentation.ui.partner.PartnerScreen
import com.seuapp.cycletrack.presentation.ui.profile.ProfileScreen
import com.seuapp.cycletrack.presentation.ui.settings.CycleSettingsScreen
import com.seuapp.cycletrack.presentation.ui.settings.RemindersScreen

/** Rotas do app. */
object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val LOGGING = "logging"
    const val INSIGHTS = "insights"
    const val PROFILE = "profile"
    const val CYCLE_SETTINGS = "cycle_settings"
    const val REMINDERS = "reminders"
    const val CONTENT = "content"
    const val ARTICLE_DETAIL = "article_detail/{articleId}"
    const val MESSAGES = "messages"
    const val CHAT = "chat"
    const val PARTNER = "partner"
    fun articleDetail(articleId: String) = "article_detail/$articleId"
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Inicio", Icons.Filled.Home),
    BottomItem(Routes.CALENDAR, "Calendario", Icons.Filled.CalendarMonth),
    BottomItem(Routes.LOGGING, "Registrar", Icons.Filled.EditNote),
    BottomItem(Routes.CONTENT, "Conteudo", Icons.Filled.MenuBook),
    BottomItem(Routes.MESSAGES, "Mensagens", Icons.Filled.ChatBubble),
    BottomItem(Routes.INSIGHTS, "Insights", Icons.Filled.Insights),
    BottomItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
)

@Composable
fun CycleTrackApp() {
    val navController = rememberNavController()
    var onboardingDone by remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomItems.any { item -> dest.route == item.route }
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (onboardingDone) Routes.HOME else Routes.ONBOARDING,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        onboardingDone = true
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToLogging = {
                        navController.navigate(Routes.LOGGING) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Routes.CALENDAR) { CalendarScreen() }
            composable(Routes.LOGGING) { LoggingScreen() }
            composable(Routes.INSIGHTS) { InsightsScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onNavigateToCycleSettings = { navController.navigate(Routes.CYCLE_SETTINGS) },
                    onNavigateToReminders = { navController.navigate(Routes.REMINDERS) },
                    onNavigateToPartner = { navController.navigate(Routes.PARTNER) }
                )
            }
            composable(Routes.CYCLE_SETTINGS) {
                CycleSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.REMINDERS) {
                RemindersScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.CONTENT) {
                ContentScreen(onArticleClick = { articleId -> navController.navigate(Routes.articleDetail(articleId)) })
            }
            composable(
                Routes.ARTICLE_DETAIL,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                ArticleDetailScreen(articleId = articleId, onBack = { navController.popBackStack() })
            }
            composable(Routes.MESSAGES) {
                MessagesScreen(onOpenChat = { navController.navigate(Routes.CHAT) })
            }
            composable(Routes.CHAT) {
                ChatScreen(onClose = { navController.popBackStack() })
            }
            composable(Routes.PARTNER) {
                PartnerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
