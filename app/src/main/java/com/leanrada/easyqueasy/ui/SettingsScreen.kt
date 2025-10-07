package com.leanrada.easyqueasy.ui

import AppDataOuterClass.OverlayColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import com.leanrada.easyqueasy.AppDataClient
import com.leanrada.easyqueasy.ui.theme.disabledAlpha
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
@androidx.compose.material3.ExperimentalMaterial3Api
fun SettingsScreen(
    appData: AppDataClient,
    onBackClick: () -> Unit,
    enabled: Boolean = true
) {
    val alphaForEnabled = if (enabled) 1f else disabledAlpha
    var overlayColor by appData.rememberOverlayColor()
    val (overlayAreaSize, setOverlayAreaSize) = appData.rememberOverlayAreaSize()
    val overlayAreaSizeSliderState = rememberSliderState(overlayAreaSize, setOverlayAreaSize)
    val (overlaySpeed, setOverlaySpeed) = appData.rememberOverlaySpeed()
    val overlaySpeedSliderState = rememberSliderState(overlaySpeed, setOverlaySpeed)
    
    // Color customization states
    var appBackgroundColor by appData.rememberAppBackgroundColor()
    var buttonBackgroundColor by appData.rememberButtonBackgroundColor()
    var playButtonColor by appData.rememberPlayButtonColor()
    
    // Add preview mode state management
    val (previewMode, setPreviewMode) = remember { mutableStateOf(PreviewMode.NONE) }

    Scaffold(
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) { innerPadding ->
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { -it / 3 },
                animationSpec = tween(300)
            ),
            exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
                targetOffsetY = { -it / 3 },
                animationSpec = tween(200)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚙️",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customize your experience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Settings content
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // App Background Color
                    ColorPicker(
                        title = "App Background",
                        description = "Customize the main app background color",
                        selectedColor = appBackgroundColor,
                        onColorChange = { appBackgroundColor = it },
                        modifier = Modifier.alpha(alphaForEnabled)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button Background Color
                    ColorPicker(
                        title = "Button Color",
                        description = "Customize the main button background color",
                        selectedColor = buttonBackgroundColor,
                        onColorChange = { buttonBackgroundColor = it },
                        modifier = Modifier.alpha(alphaForEnabled)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play Button Color
                    ColorPicker(
                        title = "Play Button Color",
                        description = "Customize the play/stop button color",
                        selectedColor = playButtonColor,
                        onColorChange = { playButtonColor = it },
                        modifier = Modifier.alpha(alphaForEnabled)
                    )

                    // Dot Grid Color Picker
                    DotGridColorPicker(
                        selectedColor = overlayColor,
                        onColorChange = { overlayColor = it },
                        modifier = Modifier.alpha(alphaForEnabled)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Size setting
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alphaForEnabled),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Overlay Size",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                enabled = enabled,
                                value = overlayAreaSizeSliderState.value,
                                onValueChange = {
                                    overlayAreaSizeSliderState.onValueChange(it)
                                    setPreviewMode(PreviewMode.SIZE)
                                },
                                onValueChangeFinished = { setPreviewMode(PreviewMode.NONE) },
                                valueRange = 0f..1f,
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )
                            Text(
                                "Adjust the size of the overlay effect",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speed setting
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alphaForEnabled),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Animation Speed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                enabled = enabled,
                                value = overlaySpeedSliderState.value,
                                onValueChange = {
                                    overlaySpeedSliderState.onValueChange(it)
                                    setPreviewMode(PreviewMode.SPEED)
                                },
                                onValueChangeFinished = { setPreviewMode(PreviewMode.NONE) },
                                valueRange = 0f..1f,
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )
                            Text(
                                "Control how fast the overlay animates",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Premium section at the bottom
                    PremiumSection()
                }
            }
        }
    }
    
    // Add preview overlay rendering
    if (previewMode != PreviewMode.NONE) {
        Overlay(
            appData = appData,
            previewMode = previewMode,
        )
    }
}

@Composable
@androidx.compose.material3.ExperimentalMaterial3Api
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun DotGridColorPicker(
    selectedColor: OverlayColor,
    onColorChange: (OverlayColor) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Black to OverlayColor.BLACK,
        Color.White to OverlayColor.WHITE,
        Color.Gray to OverlayColor.BLACK_AND_WHITE,
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Dot Grid Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Choose the color for the motion relief dots",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors.size) { index ->
                    val (color, overlayColorValue) = colors[index]
                    val isSelected = selectedColor == overlayColorValue
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(overlayColorValue) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumSection() {
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6), // Purple
                            Color(0xFFA855F7), // Light purple
                            Color(0xFFC084FC)  // Very light purple
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crown icon
                Text(
                    text = "👑",
                    style = MaterialTheme.typography.displayMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Premium",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "7-Day Free Trial",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "After your free trial, you'll need to subscribe to continue using Motion Relief",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Buy now button
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = tween(150),
                    label = "buyNowButtonScale"
                )
                
                Button(
                    onClick = {
                        isPressed = true
                        // TODO: Implement purchase logic
                        GlobalScope.launch {
                            delay(150)
                            isPressed = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF8B5CF6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    Text(
                        text = "Buy Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


