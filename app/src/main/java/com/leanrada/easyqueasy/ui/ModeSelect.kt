package com.leanrada.easyqueasy.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leanrada.easyqueasy.R

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
fun ModeSelect(
    modifier: Modifier = Modifier,
    withOnboarding: Boolean,
    onSelectDrawOverOtherApps: () -> Unit = {},
    onSelectAccessibilityService: () -> Unit = {},
) {
    var selectedMode by remember { mutableStateOf<Int?>(null) }
    
    BoxWithConstraints {
        val constraints = this

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            // Header section
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (withOnboarding) {
                    Text(
                        "🍋",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ease that quease!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Text(
                    "Choose your mode",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (withOnboarding) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Select how you'd like to activate motion sickness assistance",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Mode selection cards
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Draw Over Apps Mode
                AnimatedModeCard(
                    isSelected = selectedMode == 0,
                    onClick = { selectedMode = 0 },
                    title = "Draw Over Apps",
                    subtitle = "Quick & Easy",
                    icon = Icons.Filled.PlayArrow,
                    description = "Tap button or Quick Settings tile",
                    color = MaterialTheme.colorScheme.primary,
                    onConfirm = onSelectDrawOverOtherApps
                )

                // Accessibility Mode
                AnimatedModeCard(
                    isSelected = selectedMode == 1,
                    onClick = { selectedMode = 1 },
                    title = "Accessibility Service",
                    subtitle = "Always Available",
                    icon = Icons.Filled.Lock,
                    description = "Use accessibility shortcuts",
                    color = MaterialTheme.colorScheme.tertiary,
                    onConfirm = onSelectAccessibilityService
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AnimatedModeCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    title: String,
    subtitle: String,
    icon: ImageVector,
    description: String,
    color: androidx.compose.ui.graphics.Color,
    onConfirm: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(200),
        label = "cardScale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 2f,
        animationSpec = tween(200),
        label = "cardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                color.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            AnimatedVisibility(
                visible = isSelected,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Select This Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListItem(icon: ImageVector, text: AnnotatedString) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Icon(
            imageVector = icon, "", modifier = Modifier
                .width(18.dp)
                .height(26.dp)
                .padding(vertical = 4.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Illustration(@DrawableRes drawable: Int, contentDescription: String) {
    Image(
        painter = painterResource(drawable),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), CircleShape),
    )
}
