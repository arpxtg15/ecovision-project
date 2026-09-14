package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.EcoBottomNavigation
import com.example.ui.components.EcoTopBar
import com.example.ui.navigation.EcoScreen
import com.example.ui.screens.AboutTeamScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EcoTipsAndChallengesScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SolutionsScreen
import com.example.ui.screens.WasteScannerScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EcoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EcoVisionApp()
            }
        }
    }
}

@Composable
fun EcoVisionApp(
    viewModel: EcoViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: EcoScreen.Welcome.route

    // Collect Viewmodel States
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()
    val solutionsState by viewModel.solutionsState.collectAsStateWithLifecycle()
    val tipsState by viewModel.tipsState.collectAsStateWithLifecycle()
    val shopState by viewModel.shopState.collectAsStateWithLifecycle()
    val dailyFact by viewModel.dailyFact.collectAsStateWithLifecycle()
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val homeFeedback by viewModel.homeFeedback.collectAsStateWithLifecycle()

    val availableCredits = viewModel.getAvailableEcoCredits(userProfile, scanHistory.size)

    val shouldShowBars = currentRoute != EcoScreen.Welcome.route && currentRoute != EcoScreen.AboutTeam.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (shouldShowBars) {
                EcoTopBar(
                    title = "EcoVision",
                    onAboutClick = {
                        navController.navigate(EcoScreen.AboutTeam.route)
                    }
                )
            }
        },
        bottomBar = {
            if (shouldShowBars) {
                EcoBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(EcoScreen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EcoScreen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Welcome Onboarding Screen
            composable(
                route = EcoScreen.Welcome.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(EcoScreen.Dashboard.route) {
                            popUpTo(EcoScreen.Welcome.route) { inclusive = true }
                        }
                    },
                    onLearnMore = {
                        navController.navigate(EcoScreen.AboutTeam.route)
                    }
                )
            }

            // 2. Enhanced Main Dashboard
            composable(
                route = EcoScreen.Dashboard.route,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) }
            ) {
                val dailyTip = viewModel.getAllEcoTips().firstOrNull()
                val badges = viewModel.getBadges(userProfile, scanHistory.size)
                val weeklyActivity = viewModel.getWeeklyActivity()
                val quickActions = viewModel.getQuickEcoActions()

                DashboardScreen(
                    userProfile = userProfile,
                    recentScans = scanHistory,
                    dailyTip = dailyTip,
                    dailyFact = dailyFact,
                    quizState = quizState,
                    badges = badges,
                    weeklyActivity = weeklyActivity,
                    quickActions = quickActions,
                    homeFeedback = homeFeedback,
                    onCycleFact = { viewModel.cycleNextDailyFact() },
                    onNavigateToScanner = { navController.navigate(EcoScreen.Scanner.route) },
                    onNavigateToSolutions = { navController.navigate(EcoScreen.Solutions.route) },
                    onNavigateToTips = { navController.navigate(EcoScreen.TipsAndHabits.route) },
                    onNavigateToAbout = { navController.navigate(EcoScreen.AboutTeam.route) },
                    onQuickScanItem = {
                        viewModel.triggerQuickArScanSimulation()
                        navController.navigate(EcoScreen.Scanner.route)
                    },
                    onCompleteTip = { tip ->
                        viewModel.completeEcoTip(tip)
                    },
                    onLogQuickAction = { action ->
                        viewModel.logQuickEcoAction(action)
                    },
                    onSubmitQuizAnswer = { selectedIndex ->
                        viewModel.submitQuizAnswer(selectedIndex)
                    },
                    onCycleNextQuiz = {
                        viewModel.cycleNextQuiz()
                    }
                )
            }

            // 3. AR Waste Scanner Screen with Gemini Sustainability Analysis
            composable(
                route = EcoScreen.Scanner.route,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) }
            ) {
                WasteScannerScreen(
                    items = viewModel.getWasteItems(),
                    isScanningActive = scannerState.isScanningActive,
                    isAnalyzing = scannerState.isAnalyzing,
                    scanErrorMessage = scannerState.scanErrorMessage,
                    selectedItem = scannerState.selectedItem,
                    currentAiProfile = scannerState.currentAiProfile,
                    capturedFrameBitmap = scannerState.capturedFrameBitmap,
                    searchQuery = scannerState.scanSearchQuery,
                    activeFilter = scannerState.activeFilterCategory,
                    showDetailDialog = scannerState.showDetailDialog,
                    onSearchChanged = { viewModel.onScannerSearchQueryChanged(it) },
                    onFilterSelected = { viewModel.onSelectWasteCategoryFilter(it) },
                    onSelectItem = { viewModel.selectItemForScanning(it) },
                    onTriggerScan = { bitmap, marker -> viewModel.analyzeDetectedObjectFrame(bitmap, marker) },
                    onRetryScan = { viewModel.retryLastScan() },
                    onDismissError = { viewModel.clearScanError() },
                    onDismissDialog = { viewModel.dismissDetailDialog() }
                )
            }

            // 4. Sustainability Solutions Screen
            composable(
                route = EcoScreen.Solutions.route,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) }
            ) {
                SolutionsScreen(
                    solutions = viewModel.getAllSolutions(),
                    selectedSolution = solutionsState.selectedSolution,
                    showDetailDialog = solutionsState.showDetailDialog,
                    calculatorInputValue = solutionsState.calculatorInputValue,
                    calculatedAnnualSavings = solutionsState.calculatedAnnualSavings,
                    onUpdateCalculatorInput = { viewModel.updateCalculatorInput(it) },
                    onSelectSolution = { viewModel.selectSolution(it) },
                    onDismissDialog = { viewModel.dismissSolutionDialog() }
                )
            }

            // 5. Eco Shop & Rewards Screen
            composable(
                route = EcoScreen.Shop.route,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) }
            ) {
                ShopScreen(
                    availableCredits = availableCredits,
                    selectedCategory = shopState.selectedCategory,
                    searchQuery = shopState.searchQuery,
                    selectedProduct = shopState.selectedProduct,
                    showDetailDialog = shopState.showDetailDialog,
                    showOrderSuccessDialog = shopState.showOrderSuccessDialog,
                    lastOrderedProduct = shopState.lastOrderedProduct,
                    purchasedProductIds = shopState.purchasedProductIds,
                    feedbackMessage = shopState.feedbackMessage,
                    onCategorySelected = { viewModel.onShopCategorySelected(it) },
                    onSearchQueryChanged = { viewModel.onShopSearchQueryChanged(it) },
                    onProductSelected = { viewModel.onSelectShopProduct(it) },
                    onDismissDialog = { viewModel.onDismissShopDialog() },
                    onDismissSuccessDialog = { viewModel.onDismissOrderSuccessDialog() },
                    onRedeemProduct = { viewModel.redeemProductWithCredits(it, availableCredits) },
                    onPurchaseProduct = { viewModel.purchaseProductWithDirectOrder(it) }
                )
            }

            // 6. Real-Time Eco Tips & Habits
            composable(
                route = EcoScreen.TipsAndHabits.route,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) }
            ) {
                EcoTipsAndChallengesScreen(
                    tips = viewModel.getAllEcoTips(),
                    challenges = viewModel.getAllChallenges(),
                    selectedContext = tipsState.selectedContext,
                    challengeProgress = emptyList(),
                    feedbackMessage = tipsState.feedbackMessage,
                    onSelectContext = { viewModel.selectTipContext(it) },
                    onCompleteTip = { viewModel.completeEcoTip(it) },
                    onAdvanceChallenge = { viewModel.advanceChallenge(it) }
                )
            }

            // 7. Project Report & Team Screen
            composable(
                route = EcoScreen.AboutTeam.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                AboutTeamScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
