package com.seuapp.cycletrack.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.seuapp.cycletrack.presentation.ui.auth.AuthScreen
import com.seuapp.cycletrack.presentation.ui.calendar.CalendarScreen
import com.seuapp.cycletrack.presentation.ui.calendar.DayDetailScreen
import com.seuapp.cycletrack.presentation.ui.content.ContentScreen
import com.seuapp.cycletrack.presentation.ui.export.ExportScreen
import com.seuapp.cycletrack.presentation.ui.healthconnect.HealthConnectScreen
import com.seuapp.cycletrack.presentation.ui.home.HomeScreen
import com.seuapp.cycletrack.presentation.ui.insights.InsightsScreen
import com.seuapp.cycletrack.presentation.ui.logging.LoggingScreen
import com.seuapp.cycletrack.presentation.ui.onboarding.OnboardingScreen
import com.seuapp.cycletrack.presentation.ui.partner.PartnerScreen
import com.seuapp.cycletrack.presentation.ui.pregnancy.PregnancyScreen
import com.seuapp.cycletrack.presentation.ui.premium.PremiumScreen
import com.seuapp.cycletrack.presentation.ui.profile.ProfileScreen
import com.seuapp.cycletrack.presentation.ui.settings.SettingsScreen

/**
 * Rotas de navegação do CycleTrack.
 * Clean-room - estrutura própria baseada nos 33 screens do onboarding + 17 main screens.
 */
object Routes {
    // ============ Onboarding (33 telas) ============
    const val ONBOARDING = "onboarding"
    const val ONBOARDING_WELCOME = "onboarding/welcome"
    const val ONBOARDING_PERMISSIONS = "onboarding/permissions"
    const val ONBOARDING_CYCLE_INFO = "onboarding/cycle_info"
    const val ONBOARDING_LAST_PERIOD = "onboarding/last_period"
    const val ONBOARDING_CYCLE_LENGTH = "onboarding/cycle_length"
    const val ONBOARDING_PERIOD_LENGTH = "onboarding/period_length"
    const val ONBOARDING_CONTRACEPTION = "onboarding/contraception"
    const val ONBOARDING_PREGNANCY_STATUS = "onboarding/pregnancy_status"
    const val ONBOARDING_GOALS = "onboarding/goals"
    const val ONBOARDING_NOTIFICATIONS = "onboarding/notifications"
    const val ONBOARDING_THEME = "onboarding/theme"
    const val ONBOARDING_LANGUAGE = "onboarding/language"
    const val ONBOARDING_UNITS = "onboarding/units"
    const val ONBOARDING_PRIVACY = "onboarding/privacy"
    const val ONBOARDING_HEALTH_CONNECT = "onboarding/health_connect"
    const val ONBOARDING_PARTNER = "onboarding/partner"
    const val ONBOARDING_PREMIUM = "onboarding/premium"
    const val ONBOARDING_ACCOUNT = "onboarding/account"
    const val ONBOARDING_EMAIL = "onboarding/email"
    const val ONBOARDING_PASSWORD = "onboarding/password"
    const val ONBOARDING_PROFILE = "onboarding/profile"
    const val ONBOARDING_BIOMETRIC = "onboarding/biometric"
    const val ONBOARDING_COMPLETE = "onboarding/complete"

    // ============ Auth ============
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val VERIFY_EMAIL = "verify_email"

    // ============ Main App (Bottom Nav - 5 tabs) ============
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val LOGGING = "logging"
    const val INSIGHTS = "insights"
    const val PROFILE = "profile"

    // ============ Home Stack ============
    const val CYCLE_DETAIL = "cycle/detail"
    const val PREDICTION_DETAIL = "prediction/detail"

    // ============ Calendar Stack ============
    const val DAY_DETAIL = "day/detail"

    // ============ Logging Stack ============
    const val LOG_PERIOD = "logging/period"
    const val LOG_SYMPTOMS = "logging/symptoms"
    const val LOG_WEIGHT = "logging/weight"
    const val LOG_TEMPERATURE = "logging/temperature"
    const val LOG_MOOD = "logging/mood"

    // ============ Insights Stack ============
    const val INSIGHTS_CYCLES = "insights/cycles"
    const val INSIGHTS_SYMPTOMS = "insights/symptoms"
    const val INSIGHTS_FERTILITY = "insights/fertility"
    const val INSIGHTS_TRENDS = "insights/trends"
    const val INSIGHTS_REPORT = "insights/report"

    // ============ Pregnancy ============
    const val PREGNANCY = "pregnancy"
    const val PREGNANCY_WEEK = "pregnancy/week"
    const val PREGNANCY_APPOINTMENTS = "pregnancy/appointments"
    const val PREGNANCY_TESTS = "pregnancy/tests"
    const val PREGNANCY_WEIGHT = "pregnancy/weight"

    // ============ Perimenopause ============
    const val PERIMENOPAUSE = "perimenopause"

    // ============ Partner ============
    const val PARTNER = "partner"
    const val PARTNER_INVITE = "partner/invite"
    const val PARTNER_SETTINGS = "partner/settings"

    // ============ Premium ============
    const val PREMIUM = "premium"
    const val PREMIUM_FEATURES = "premium/features"
    const val PREMIUM_SUBSCRIPTION = "premium/subscription"
    const val PREMIUM_MANAGE = "premium/manage"

    // ============ Settings ============
    const val SETTINGS = "settings"
    const val SETTINGS_ACCOUNT = "settings/account"
    const val SETTINGS_PROFILE = "settings/profile"
    const val SETTINGS_CYCLE = "settings/cycle"
    const val SETTINGS_NOTIFICATIONS = "settings/notifications"
    const val SETTINGS_DISPLAY = "settings/display"
    const val SETTINGS_PRIVACY = "settings/privacy"
    const val SETTINGS_SECURITY = "settings/security"
    const val SETTINGS_DATA = "settings/data"
    const val SETTINGS_SYNC = "settings/sync"
    const val SETTINGS_HEALTH_CONNECT = "settings/health_connect"
    const val SETTINGS_PARTNER = "settings/partner"
    const val SETTINGS_SUBSCRIPTION = "settings/subscription"
    const val SETTINGS_EXPORT = "settings/export"
    const val SETTINGS_ABOUT = "settings/about"

    // ============ Export ============
    const val EXPORT = "export"

    // ============ Content ============
    const val CONTENT = "content"
    const val CONTENT_ARTICLE = "content/article"
    const val CONTENT_BOOKMARKS = "content/bookmarks"

    // ============ Health Connect ============
    const val HEALTH_CONNECT = "health_connect"
}

/**
 * Item da barra de navegação inferior.
 */
private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "Início",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.CALENDAR,
        label = "Calendário",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
        route = Routes.LOGGING,
        label = "Registrar",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        route = Routes.INSIGHTS,
        label = "Insights",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights
    ),
    BottomNavItem(
        route = Routes.PROFILE,
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

/**
 * App raiz do CycleTrack com Scaffold e barra de navegação inferior.
 */
@Composable
fun CycleTrackApp(
    startDestination: String = Routes.ONBOARDING_WELCOME
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Bottom nav visível apenas nas 5 telas principais
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
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
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * NavHost principal do app.
 */
@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ============ Onboarding Flow (33 telas) ============
        navigation(startDestination = Routes.ONBOARDING_WELCOME, route = Routes.ONBOARDING) {
            composable(Routes.ONBOARDING_WELCOME) {
                OnboardingScreen.Welcome(onNext = { navController.navigate(Routes.ONBOARDING_PERMISSIONS) })
            }
            composable(Routes.ONBOARDING_PERMISSIONS) {
                OnboardingScreen.Permissions(onNext = { navController.navigate(Routes.ONBOARDING_CYCLE_INFO) })
            }
            composable(Routes.ONBOARDING_CYCLE_INFO) {
                OnboardingScreen.CycleInfo(onNext = { navController.navigate(Routes.ONBOARDING_LAST_PERIOD) })
            }
            composable(Routes.ONBOARDING_LAST_PERIOD) {
                OnboardingScreen.LastPeriod(onNext = { navController.navigate(Routes.ONBOARDING_CYCLE_LENGTH) })
            }
            composable(Routes.ONBOARDING_CYCLE_LENGTH) {
                OnboardingScreen.CycleLength(onNext = { navController.navigate(Routes.ONBOARDING_PERIOD_LENGTH) })
            }
            composable(Routes.ONBOARDING_PERIOD_LENGTH) {
                OnboardingScreen.PeriodLength(onNext = { navController.navigate(Routes.ONBOARDING_CONTRACEPTION) })
            }
            composable(Routes.ONBOARDING_CONTRACEPTION) {
                OnboardingScreen.Contraception(onNext = { navController.navigate(Routes.ONBOARDING_PREGNANCY_STATUS) })
            }
            composable(Routes.ONBOARDING_PREGNANCY_STATUS) {
                OnboardingScreen.PregnancyStatus(onNext = { navController.navigate(Routes.ONBOARDING_GOALS) })
            }
            composable(Routes.ONBOARDING_GOALS) {
                OnboardingScreen.Goals(onNext = { navController.navigate(Routes.ONBOARDING_NOTIFICATIONS) })
            }
            composable(Routes.ONBOARDING_NOTIFICATIONS) {
                OnboardingScreen.Notifications(onNext = { navController.navigate(Routes.ONBOARDING_THEME) })
            }
            composable(Routes.ONBOARDING_THEME) {
                OnboardingScreen.Theme(onNext = { navController.navigate(Routes.ONBOARDING_LANGUAGE) })
            }
            composable(Routes.ONBOARDING_LANGUAGE) {
                OnboardingScreen.Language(onNext = { navController.navigate(Routes.ONBOARDING_UNITS) })
            }
            composable(Routes.ONBOARDING_UNITS) {
                OnboardingScreen.Units(onNext = { navController.navigate(Routes.ONBOARDING_PRIVACY) })
            }
            composable(Routes.ONBOARDING_PRIVACY) {
                OnboardingScreen.Privacy(onNext = { navController.navigate(Routes.ONBOARDING_HEALTH_CONNECT) })
            }
            composable(Routes.ONBOARDING_HEALTH_CONNECT) {
                OnboardingScreen.HealthConnect(onNext = { navController.navigate(Routes.ONBOARDING_PARTNER) })
            }
            composable(Routes.ONBOARDING_PARTNER) {
                OnboardingScreen.Partner(onNext = { navController.navigate(Routes.ONBOARDING_PREMIUM) })
            }
            composable(Routes.ONBOARDING_PREMIUM) {
                OnboardingScreen.Premium(onNext = { navController.navigate(Routes.ONBOARDING_ACCOUNT) })
            }
            composable(Routes.ONBOARDING_ACCOUNT) {
                OnboardingScreen.AccountType(onNext = { navController.navigate(Routes.ONBOARDING_EMAIL) })
            }
            composable(Routes.ONBOARDING_EMAIL) {
                OnboardingScreen.Email(onNext = { navController.navigate(Routes.ONBOARDING_PASSWORD) })
            }
            composable(Routes.ONBOARDING_PASSWORD) {
                OnboardingScreen.Password(onNext = { navController.navigate(Routes.ONBOARDING_PROFILE) })
            }
            composable(Routes.ONBOARDING_PROFILE) {
                OnboardingScreen.ProfileSetup(onNext = { navController.navigate(Routes.ONBOARDING_BIOMETRIC) })
            }
            composable(Routes.ONBOARDING_BIOMETRIC) {
                OnboardingScreen.Biometric(onNext = { navController.navigate(Routes.ONBOARDING_COMPLETE) })
            }
            composable(Routes.ONBOARDING_COMPLETE) {
                OnboardingScreen.Complete(
                    onComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
        }

        // ============ Auth Flow ============
        navigation(startDestination = Routes.LOGIN, route = "auth") {
            composable(Routes.LOGIN) {
                AuthScreen.Login(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onGoToRegister = { navController.navigate(Routes.REGISTER) },
                    onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
                )
            }
            composable(Routes.REGISTER) {
                AuthScreen.Register(
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onGoToLogin = { navController.navigate(Routes.LOGIN) }
                )
            }
            composable(Routes.FORGOT_PASSWORD) {
                AuthScreen.ForgotPassword(onBackToLogin = { navController.navigate(Routes.LOGIN) })
            }
            composable(Routes.RESET_PASSWORD) {
                AuthScreen.ResetPassword(onBackToLogin = { navController.navigate(Routes.LOGIN) })
            }
            composable(Routes.VERIFY_EMAIL) {
                AuthScreen.VerifyEmail(onVerified = { navController.navigate(Routes.LOGIN) })
            }
        }

        // ============ Main App - Bottom Navigation ============
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToDetail = { navController.navigate(Routes.CYCLE_DETAIL) },
                onNavigateToCalendar = { navController.navigate(Routes.CALENDAR) },
                onNavigateToLogging = { navController.navigate(Routes.LOGGING) },
                onNavigateToInsights = { navController.navigate(Routes.INSIGHTS) }
            )
        }
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onDayClick = { date -> navController.navigate("${Routes.DAY_DETAIL}/$date") },
                onViewModeChange = { /* handled internally */ }
            )
        }
        composable(
            route = "${Routes.DAY_DETAIL}/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { entry ->
            DayDetailScreen(
                date = entry.arguments?.getString("date").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.LOGGING) {
            LoggingScreen(
                onLogPeriod = { navController.navigate(Routes.LOG_PERIOD) },
                onLogSymptoms = { navController.navigate(Routes.LOG_SYMPTOMS) },
                onLogWeight = { navController.navigate(Routes.LOG_WEIGHT) },
                onLogTemperature = { navController.navigate(Routes.LOG_TEMPERATURE) },
                onLogMood = { navController.navigate(Routes.LOG_MOOD) }
            )
        }
        composable(Routes.INSIGHTS) {
            InsightsScreen(onNavigateToReport = { navController.navigate(Routes.INSIGHTS_REPORT) })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToPregnancy = { navController.navigate(Routes.PREGNANCY) },
                onNavigateToPartner = { navController.navigate(Routes.PARTNER) },
                onNavigateToPremium = { navController.navigate(Routes.PREMIUM) }
            )
        }

        // ============ Detail routes ============
        composable(Routes.CYCLE_DETAIL) { CycleDetailPlaceholder() }
        composable(Routes.PREDICTION_DETAIL) { CycleDetailPlaceholder() }
        composable(Routes.LOG_PERIOD) { LoggingTabPlaceholder("Registrar Período") }
        composable(Routes.LOG_SYMPTOMS) { LoggingTabPlaceholder("Registrar Sintomas") }
        composable(Routes.LOG_WEIGHT) { LoggingTabPlaceholder("Registrar Peso") }
        composable(Routes.LOG_TEMPERATURE) { LoggingTabPlaceholder("Registrar Temperatura") }
        composable(Routes.LOG_MOOD) { LoggingTabPlaceholder("Registrar Humor") }
        composable(Routes.INSIGHTS_CYCLES) { LoggingTabPlaceholder("Estatísticas do Ciclo") }
        composable(Routes.INSIGHTS_SYMPTOMS) { LoggingTabPlaceholder("Padrões de Sintomas") }
        composable(Routes.INSIGHTS_FERTILITY) { LoggingTabPlaceholder("Insights de Fertilidade") }
        composable(Routes.INSIGHTS_TRENDS) { LoggingTabPlaceholder("Tendências") }
        composable(Routes.INSIGHTS_REPORT) { LoggingTabPlaceholder("Relatório Completo") }

        // ============ Settings Flow ============
        navigation(startDestination = Routes.SETTINGS, route = "settings") {
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateToAccount = { navController.navigate(Routes.SETTINGS_ACCOUNT) },
                    onNavigateToCycle = { navController.navigate(Routes.SETTINGS_CYCLE) },
                    onNavigateToNotifications = { navController.navigate(Routes.SETTINGS_NOTIFICATIONS) },
                    onNavigateToDisplay = { navController.navigate(Routes.SETTINGS_DISPLAY) },
                    onNavigateToPrivacy = { navController.navigate(Routes.SETTINGS_PRIVACY) },
                    onNavigateToSecurity = { navController.navigate(Routes.SETTINGS_SECURITY) },
                    onNavigateToData = { navController.navigate(Routes.SETTINGS_DATA) },
                    onNavigateToSync = { navController.navigate(Routes.SETTINGS_SYNC) },
                    onNavigateToHealthConnect = { navController.navigate(Routes.SETTINGS_HEALTH_CONNECT) },
                    onNavigateToPartner = { navController.navigate(Routes.SETTINGS_PARTNER) },
                    onNavigateToSubscription = { navController.navigate(Routes.SETTINGS_SUBSCRIPTION) },
                    onNavigateToExport = { navController.navigate(Routes.SETTINGS_EXPORT) },
                    onNavigateToAbout = { navController.navigate(Routes.SETTINGS_ABOUT) }
                )
            }
            composable(Routes.SETTINGS_ACCOUNT) { LoggingTabPlaceholder("Conta") }
            composable(Routes.SETTINGS_PROFILE) { LoggingTabPlaceholder("Perfil") }
            composable(Routes.SETTINGS_CYCLE) { LoggingTabPlaceholder("Preferências de Ciclo") }
            composable(Routes.SETTINGS_NOTIFICATIONS) { LoggingTabPlaceholder("Notificações") }
            composable(Routes.SETTINGS_DISPLAY) { LoggingTabPlaceholder("Aparência") }
            composable(Routes.SETTINGS_PRIVACY) { LoggingTabPlaceholder("Privacidade") }
            composable(Routes.SETTINGS_SECURITY) { LoggingTabPlaceholder("Segurança") }
            composable(Routes.SETTINGS_DATA) { LoggingTabPlaceholder("Dados") }
            composable(Routes.SETTINGS_SYNC) { LoggingTabPlaceholder("Sincronização") }
            composable(Routes.SETTINGS_HEALTH_CONNECT) {
                HealthConnectScreen(onNavigateToSettings = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS_PARTNER) {
                PartnerScreen(
                    onNavigateToInvite = { navController.navigate(Routes.PARTNER_INVITE) },
                    onNavigateToSettings = { navController.navigate(Routes.PARTNER_SETTINGS) },
                    onNavigateToSharedView = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS_SUBSCRIPTION) {
                PremiumScreen(
                    onNavigateToFeatures = { navController.navigate(Routes.PREMIUM_FEATURES) },
                    onNavigateToSubscription = { navController.navigate(Routes.PREMIUM_SUBSCRIPTION) },
                    onNavigateToManage = { navController.navigate(Routes.PREMIUM_MANAGE) }
                )
            }
            composable(Routes.SETTINGS_EXPORT) {
                ExportScreen(
                    onNavigateToProgress = { navController.popBackStack() },
                    onNavigateToDownload = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS_ABOUT) { LoggingTabPlaceholder("Sobre") }
        }

        // ============ Pregnancy Flow ============
        navigation(startDestination = Routes.PREGNANCY, route = "pregnancy") {
            composable(Routes.PREGNANCY) {
                PregnancyScreen(
                    onNavigateToAppointments = { navController.navigate(Routes.PREGNANCY_APPOINTMENTS) },
                    onNavigateToTests = { navController.navigate(Routes.PREGNANCY_TESTS) },
                    onNavigateToWeight = { navController.navigate(Routes.PREGNANCY_WEIGHT) }
                )
            }
            composable(Routes.PREGNANCY_WEEK) { LoggingTabPlaceholder("Semana da Gestação") }
            composable(Routes.PREGNANCY_APPOINTMENTS) { LoggingTabPlaceholder("Consultas") }
            composable(Routes.PREGNANCY_TESTS) { LoggingTabPlaceholder("Exames") }
            composable(Routes.PREGNANCY_WEIGHT) { LoggingTabPlaceholder("Peso na Gestação") }
        }

        // ============ Partner Flow ============
        navigation(startDestination = Routes.PARTNER, route = "partner") {
            composable(Routes.PARTNER) {
                PartnerScreen(
                    onNavigateToInvite = { navController.navigate(Routes.PARTNER_INVITE) },
                    onNavigateToSettings = { navController.navigate(Routes.PARTNER_SETTINGS) },
                    onNavigateToSharedView = { navController.popBackStack() }
                )
            }
            composable(Routes.PARTNER_INVITE) { LoggingTabPlaceholder("Convidar Parceiro") }
            composable(Routes.PARTNER_SETTINGS) { LoggingTabPlaceholder("Preferências de Compartilhamento") }
        }

        // ============ Premium Flow ============
        navigation(startDestination = Routes.PREMIUM, route = "premium") {
            composable(Routes.PREMIUM) {
                PremiumScreen(
                    onNavigateToFeatures = { navController.navigate(Routes.PREMIUM_FEATURES) },
                    onNavigateToSubscription = { navController.navigate(Routes.PREMIUM_SUBSCRIPTION) },
                    onNavigateToManage = { navController.navigate(Routes.PREMIUM_MANAGE) }
                )
            }
            composable(Routes.PREMIUM_FEATURES) { LoggingTabPlaceholder("Recursos Premium") }
            composable(Routes.PREMIUM_SUBSCRIPTION) { LoggingTabPlaceholder("Assinatura") }
            composable(Routes.PREMIUM_MANAGE) { LoggingTabPlaceholder("Gerenciar Assinatura") }
        }

        // ============ Export Flow ============
        navigation(startDestination = Routes.EXPORT, route = "export") {
            composable(Routes.EXPORT) {
                ExportScreen(
                    onNavigateToProgress = { navController.popBackStack() },
                    onNavigateToDownload = { navController.popBackStack() }
                )
            }
        }

        // ============ Content Flow ============
        navigation(startDestination = Routes.CONTENT, route = "content") {
            composable(Routes.CONTENT) {
                ContentScreen(
                    onNavigateToArticle = { navController.navigate(Routes.CONTENT_ARTICLE) },
                    onNavigateToVideo = { navController.navigate(Routes.CONTENT_ARTICLE) }
                )
            }
            composable(Routes.CONTENT_ARTICLE) { LoggingTabPlaceholder("Artigo") }
            composable(Routes.CONTENT_BOOKMARKS) { LoggingTabPlaceholder("Favoritos") }
        }
    }
}
