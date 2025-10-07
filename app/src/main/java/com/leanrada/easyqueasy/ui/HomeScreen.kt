package com.leanrada.easyqueasy.ui

import AppDataOuterClass.DrawingMode
import AppDataOuterClass.OverlayColor
import android.app.StatusBarManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.leanrada.easyqueasy.AppDataClient
import com.leanrada.easyqueasy.PermissionChecker
import com.leanrada.easyqueasy.Permissions
import com.leanrada.easyqueasy.services.ForegroundOverlayTileService
import com.leanrada.easyqueasy.ui.theme.disabledAlpha

@Composable
fun HomeScreen(
    appData: AppDataClient,
    foregroundOverlayActive: MutableState<Boolean>,
    onNavigateToSettings: () -> Unit = {},
    debug_onReset: () -> Unit = {}
) {
    val permissionChecker by Permissions.rememberPermissionChecker(appData)
    val loaded by appData.rememberLoaded()
    val drawingMode by appData.rememberDrawingMode()
    val (previewMode, setPreviewMode) = remember { mutableStateOf(PreviewMode.NONE) }
    var playButtonColor by appData.rememberPlayButtonColor()

    Scaffold(
        topBar = {
            SimpleTopBar(
                onSettingsClick = onNavigateToSettings
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) { innerPadding ->
        if (!loaded) return@Scaffold

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content - centered
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Permission section
                    AnimatedVisibility(
                        visible = permissionChecker.status != PermissionChecker.Status.OK,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        PermissionSection(permissionChecker)
                    }

                    // Main button
                    AnimatedVisibility(
                        visible = permissionChecker.status == PermissionChecker.Status.OK,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        when (drawingMode) {
                            DrawingMode.DRAW_OVER_OTHER_APPS -> {
                                CenteredToggleButton(foregroundOverlayActive, playButtonColor)
                            }
                            DrawingMode.ACCESSIBILITY_SERVICE -> {
                                CenteredAccessibilityButton()
                            }
                            else -> {}
                        }
                    }
                }
            }

        }
    }

    if (previewMode != PreviewMode.NONE) {
        Overlay(
            appData = appData,
            previewMode = previewMode,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SimpleTopBar(
    onSettingsClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(150),
        label = "settingsScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 45f else 0f,
        animationSpec = tween(200),
        label = "settingsRotation"
    )

    TopAppBar(
        title = { },
        actions = {
            IconButton(
                onClick = {
                    isPressed = true
                    onSettingsClick()
                    // Reset press state after animation
                    GlobalScope.launch {
                        kotlinx.coroutines.delay(200)
                        isPressed = false
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(scale)
                        .rotate(rotation)
                )
            }
        },
    )
}

@Composable
fun CenteredToggleButton(overlayActive: MutableState<Boolean>, playButtonColor: Int) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(150),
        label = "pressScale"
    )
    
    val bounceScale by animateFloatAsState(
        targetValue = if (overlayActive.value) 1.1f else 1f,
        animationSpec = tween(400, delayMillis = 100),
        label = "bounceScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (overlayActive.value) 180f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(80)
        }
    }

    // Custom button without grey background
    androidx.compose.material3.Button(
        onClick = {
            isPressed = true
            vibrate()
            overlayActive.value = !overlayActive.value
            // Reset press state after animation
            GlobalScope.launch {
                kotlinx.coroutines.delay(200)
                isPressed = false
            }
        },
        modifier = Modifier
            .size(200.dp)
            .scale(pressScale * bounceScale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (overlayActive.value)
                MaterialTheme.colorScheme.error
            else
                Color(playButtonColor),
            contentColor = if (overlayActive.value)
                Color.White
            else
                if (Color(playButtonColor).luminance() > 0.5f) Color.Black else Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = if (overlayActive.value) Icons.Filled.Close else Icons.Filled.PlayArrow,
            contentDescription = if (overlayActive.value) "Stop overlay" else "Start overlay",
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation),
        )
    }
}

@Composable
fun CenteredAccessibilityButton() {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "pressScale"
    )

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    Button(
        onClick = {
            isPressed = true
            vibrate()
            ContextCompat.startActivity(context, Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), null)
            // Reset press state after animation
            GlobalScope.launch {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        },
        modifier = Modifier
            .size(120.dp)
            .scale(scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ExitToApp,
            contentDescription = "Open Accessibility Settings",
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun GetStartedSection(permissionChecker: PermissionChecker) {
    if (permissionChecker.status == PermissionChecker.Status.OK) return

    Column(Modifier.padding(bottom = 16.dp)) {
        Text(
            "Get started",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        when (permissionChecker.status) {
            PermissionChecker.Status.REQUEST_DRAW_OVERLAY_PERMISSION -> {
                GetStartedCard(
                    onClick = { permissionChecker.request {} }
                ) {
                    GetStartedChecklistItem {
                        Text(
                            buildAnnotatedString {
                                append("First, grant ")
                                appendBold("Motion Relief")
                                append(" the permission to draw over other apps.")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            PermissionChecker.Status.REQUEST_ACCESSIBILITY_PERMISSION ->
                GetStartedCard(
                    onClick = { permissionChecker.request {} }
                ) {
                    GetStartedChecklistItem {
                        Text(
                            buildAnnotatedString {
                                append("First, enable the ")
                                appendBold("Motion Relief")
                                append(" Accessibility app and shortcut. The permission will only be used to draw over apps.")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

            else -> {}
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GetStartedCard(onClick: () -> Unit = {}, content: @Composable ColumnScope.() -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        content()
    }
}

@Composable
fun GetStartedChecklistItem(content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
    ) {
        Checkbox(checked = false, onCheckedChange = null)
        Spacer(Modifier.size(8.dp))
        content()
    }
}

@Composable
private fun SettingsSection(appData: AppDataClient, enabled: Boolean = false, setPreviewMode: (value: PreviewMode) -> Unit = {}) {
    val context = LocalContext.current
    val drawingMode by appData.rememberDrawingMode()
    val alphaForEnabled = if (enabled) 1f else disabledAlpha

    var overlayColor by appData.rememberOverlayColor()

    val (overlayAreaSize, setOverlayAreaSize) = appData.rememberOverlayAreaSize()
    val overlayAreaSizeSliderState = rememberSliderState(overlayAreaSize, setOverlayAreaSize)

    val (overlaySpeed, setOverlaySpeed) = appData.rememberOverlaySpeed()
    val overlaySpeedSliderState = rememberSliderState(overlaySpeed, setOverlaySpeed)

    Column {
        Text(
            "Settings",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .alpha(alphaForEnabled)
        )

        var colorSchemeDialogActive by remember { mutableStateOf(false) }

        Surface(
            onClick = { colorSchemeDialogActive = true },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alphaForEnabled),
        ) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Box {
                    DropdownMenu(
                        expanded = colorSchemeDialogActive,
                        onDismissRequest = { colorSchemeDialogActive = false }
                    ) {
                        OverlayColor.values().forEach {
                            DropdownMenuItem(
                                text = { Text(overlayColorLabel(it)) },
                                onClick = {
                                    overlayColor = it
                                    colorSchemeDialogActive = false
                                },
                            )
                        }
                    }
                }
                Text(
                    "Color scheme",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    overlayColorLabel(overlayColor),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Column(
            Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .alpha(alphaForEnabled)
        ) {
            Text(
                "Size",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Slider(
                enabled = enabled,
                value = overlayAreaSizeSliderState.value,
                onValueChange = {
                    overlayAreaSizeSliderState.onValueChange(it)
                    setPreviewMode(PreviewMode.SIZE)
                },
                onValueChangeFinished = { setPreviewMode(PreviewMode.NONE) },
                valueRange = 0f..1f,
            )
        }

        Column(
            Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .alpha(alphaForEnabled)
        ) {
            Text(
                "Speed",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Slider(
                enabled = enabled,
                value = overlaySpeedSliderState.value,
                onValueChange = {
                    overlaySpeedSliderState.onValueChange(it)
                    setPreviewMode(PreviewMode.SPEED)
                },
                onValueChangeFinished = { setPreviewMode(PreviewMode.NONE) },
                valueRange = 0f..1f,
            )
        }
    }
}

private fun overlayColorLabel(overlayColor: OverlayColor) = when (overlayColor) {
    OverlayColor.BLACK_AND_WHITE -> "Black and white"
    OverlayColor.BLACK -> "Black"
    OverlayColor.WHITE -> "White"
}

data class SliderState(
    val value: Float,
    val onValueChange: (Float) -> Unit,
)

@Composable
fun rememberSliderState(source: Float, setSource: (Float) -> Unit): SliderState {
    val sliderValue by rememberUpdatedState(source)

    return SliderState(
        sliderValue
    ) {
        setSource(it)
    }
}

// New minimalistic components
@Composable
private fun HeroSection(isActive: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isActive) "Overlay Active" else "Ready to Help",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isActive) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isActive) 
                    "Motion sickness assistance is running" 
                else 
                    "Tap the button below to start",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = if (isActive) 
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PermissionSection(permissionChecker: PermissionChecker) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Setup Required",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.5.sp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "We need permission to help with motion sickness",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
            letterSpacing = 0.2.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        when (permissionChecker.status) {
            PermissionChecker.Status.REQUEST_DRAW_OVERLAY_PERMISSION -> {
                PermissionCard(
                    title = "Grant Overlay Permission",
                    description = "Allow Motion Relief to draw over other apps",
                    onClick = { permissionChecker.request {} }
                )
            }
            PermissionChecker.Status.REQUEST_ACCESSIBILITY_PERMISSION -> {
                PermissionCard(
                    title = "Enable Accessibility",
                    description = "Enable Motion Relief in accessibility settings",
                    onClick = { permissionChecker.request {} }
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "permissionCardScale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2.dp.value else 8.dp.value,
        animationSpec = tween(150),
        label = "permissionCardElevation"
    )
    
    Button(
        onClick = {
            isPressed = true
            onClick()
            GlobalScope.launch {
                kotlinx.coroutines.delay(150)
                isPressed = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )
        }
    }
}

@Composable
private fun AnimatedAccessibilityButton() {
    val context = LocalContext.current
    
    FloatingActionButton(
        onClick = {
            ContextCompat.startActivity(context, Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), null)
        },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        modifier = Modifier
            .padding(16.dp)
            .size(72.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.ExitToApp,
            contentDescription = "Open Accessibility Settings",
            modifier = Modifier.size(32.dp),
        )
    }
}
