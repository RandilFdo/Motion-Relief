package com.leanrada.easyqueasy.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ReviewPopup(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRate: () -> Unit,
    onRemindLater: () -> Unit,
    onNeverAsk: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(200)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable { /* Prevent click through */ },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Star icon
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.displayMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "How are you liking Motion Relief?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Your feedback helps us improve the app for everyone",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Rate button
                    ReviewButton(
                        text = "Rate on Play Store",
                        onClick = onRate,
                        isPrimary = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Remind later button
                    ReviewButton(
                        text = "Remind me later",
                        onClick = onRemindLater,
                        isPrimary = false
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Never ask button
                    ReviewButton(
                        text = "Don't ask again",
                        onClick = onNeverAsk,
                        isPrimary = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "reviewButtonScale"
    )
    
    Button(
        onClick = {
            isPressed = true
            onClick()
            // Reset press state
            GlobalScope.launch {
                delay(150)
                isPressed = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color.Black else Color.Transparent,
            contentColor = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = if (isPrimary) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

fun openPlayStore(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to web browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
        context.startActivity(intent)
    }
}
