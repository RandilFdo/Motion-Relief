package com.leanrada.easyqueasy

import AppDataOuterClass
import AppDataOuterClass.DrawingMode
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.leanrada.easyqueasy.Permissions.Companion.foregroundServicePermissions
import com.leanrada.easyqueasy.Permissions.Companion.permissionsEnsurer
import com.leanrada.easyqueasy.services.ForegroundOverlayService
import com.leanrada.easyqueasy.ui.HomeScreen
import com.leanrada.easyqueasy.ui.ModeSelectScreen
import com.leanrada.easyqueasy.ui.SettingsScreen
import com.leanrada.easyqueasy.ui.ReviewPopup
import com.leanrada.easyqueasy.ui.openPlayStore
import com.leanrada.easyqueasy.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var appData: AppDataClient
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Permission result handled
    }
    
    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    @androidx.compose.material3.ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appData = AppDataClient(this, lifecycleScope)
        enableEdgeToEdge()
        setContent { App() }
    }

    @Composable
    @androidx.compose.material3.ExperimentalMaterial3Api
    private fun App() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var drawingMode by appData.rememberDrawingMode()
        // Set default mode for minimalistic app
        if (drawingMode == DrawingMode.NONE) {
            drawingMode = DrawingMode.DRAW_OVER_OTHER_APPS
        }
        var onboarded by appData.rememberOnboarded()
        var showSettings by remember { mutableStateOf(false) }
        
        // Color customization states
        var appBackgroundColor by appData.rememberAppBackgroundColor()
        var buttonBackgroundColor by appData.rememberButtonBackgroundColor()
        
        // Review popup state with persistent tracking
        var showReviewPopup by remember { mutableStateOf(false) }
        var neverAskReview by remember { mutableStateOf(false) }
        
        // Persistent tracking
        val appDownloadTime by appData.rememberAppDownloadTime()
        val lastReviewPromptTime by appData.rememberLastReviewPromptTime()
        val reviewPrompted by appData.rememberReviewPrompted()
        val ensureForegroundOverlayPermissions = permissionsEnsurer(foregroundServicePermissions)
        val localForegroundOverlayActive = rememberLocalForegroundOverlayActive()

        
        // Set download time on first app launch
        LaunchedEffect(Unit) {
            if (appDownloadTime == 0L) {
                val currentTime = System.currentTimeMillis()
                try {
                    appData.dataStore.updateData { data ->
                        data.toBuilder()
                            .setAppDownloadTime(currentTime)
                            .build()
                    }
                } catch (e: Exception) {
                    // Log error but don't crash
                    android.util.Log.e("MainActivity", "Failed to set app download time", e)
                }
            }
        }
        
        // Review popup logic - show 1 day after download, then twice per week
        LaunchedEffect(neverAskReview, appDownloadTime, lastReviewPromptTime, reviewPrompted) {
            if (!neverAskReview && !showReviewPopup && !reviewPrompted && appDownloadTime > 0L) {
                val currentTime = System.currentTimeMillis()
                val daysSinceDownload = (currentTime - appDownloadTime) / (24 * 60 * 60 * 1000)
                val daysSinceLastPrompt = if (lastReviewPromptTime > 0) {
                    (currentTime - lastReviewPromptTime) / (24 * 60 * 60 * 1000)
                } else {
                    Long.MAX_VALUE
                }
                
                // Show popup 1 day after download, then every 2nd and 6th day of each week
                val shouldShowPopup = when {
                    daysSinceDownload >= 1 && lastReviewPromptTime == 0L -> true // First time: 1 day after download
                    daysSinceDownload >= 2 && daysSinceLastPrompt >= 2 -> true // 2nd day of week
                    daysSinceDownload >= 6 && daysSinceLastPrompt >= 4 -> true // 6th day of week
                    else -> false
                }
                
                if (shouldShowPopup) {
                    showReviewPopup = true
                    // Update last prompt time
                    coroutineScope.launch {
                        try {
                            appData.dataStore.updateData { data ->
                                data.toBuilder()
                                    .setLastReviewPromptTime(currentTime)
                                    .build()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to update review prompt time", e)
                        }
                    }
                }
            }
        }
        
        val shouldActivateForegroundOverlay = drawingMode == DrawingMode.DRAW_OVER_OTHER_APPS && localForegroundOverlayActive.value
        var lastOverlayState by remember { mutableStateOf(false) }
        
        LaunchedEffect(shouldActivateForegroundOverlay) {
            if (shouldActivateForegroundOverlay != lastOverlayState) {
                lastOverlayState = shouldActivateForegroundOverlay
                if (shouldActivateForegroundOverlay) {
                    try {
                        ensureForegroundOverlayPermissions {
                            try {
                                ForegroundOverlayService.start(context)
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Failed to start overlay service", e)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Failed to ensure permissions", e)
                    }
                } else {
                    try {
                        ForegroundOverlayService.stop(context)
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Failed to stop overlay service", e)
                    }
                }
            }
        }

        AppTheme(
            customAppBackgroundColor = appBackgroundColor,
            customButtonBackgroundColor = buttonBackgroundColor
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = showSettings,
                enter = androidx.compose.animation.slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = androidx.compose.animation.slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            ) {
                SettingsScreen(
                    appData = appData,
                    onBackClick = { showSettings = false },
                    enabled = true
                )
            }
            
            androidx.compose.animation.AnimatedVisibility(
                visible = !showSettings,
                enter = androidx.compose.animation.slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = androidx.compose.animation.slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            ) {
                    HomeScreen(
                        appData = appData,
                        foregroundOverlayActive = localForegroundOverlayActive,
                        onNavigateToSettings = { showSettings = true },
                    debug_onReset = {
                        coroutineScope.launch {
                            try {
                                appData.dataStore.updateData {
                                    AppDataOuterClass.AppData.getDefaultInstance()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Failed to reset app data", e)
                            }
                        }
                    }
                )
            }
            
            // Review popup
            ReviewPopup(
                isVisible = showReviewPopup,
                onDismiss = { showReviewPopup = false },
                onRate = {
                    openPlayStore(context)
                    showReviewPopup = false
                    // Mark as reviewed so popup never shows again
                    coroutineScope.launch {
                        try {
                            appData.dataStore.updateData { data ->
                                data.toBuilder()
                                    .setReviewPrompted(true)
                                    .build()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to mark as reviewed", e)
                        }
                    }
                },
                onRemindLater = {
                    showReviewPopup = false
                },
                onNeverAsk = {
                    neverAskReview = true
                    showReviewPopup = false
                }
            )
            }
        }
    }

    @Composable
    private fun rememberLocalForegroundOverlayActive(): MutableState<Boolean> {
        val foregroundOverlayStartTime by appData.rememberForegroundOverlayStartTime()
        val foregroundOverlayStopTime by appData.rememberForegroundOverlayStopTime()
        val foregroundOverlayActive = foregroundOverlayStartTime > foregroundOverlayStopTime
        val localForegroundOverlayActive = remember { mutableStateOf(foregroundOverlayActive) }
        LaunchedEffect(foregroundOverlayActive) {
            localForegroundOverlayActive.value = foregroundOverlayActive
        }
        return localForegroundOverlayActive
    }
}
