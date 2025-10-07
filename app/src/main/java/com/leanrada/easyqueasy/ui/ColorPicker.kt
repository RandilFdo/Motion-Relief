package com.leanrada.easyqueasy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    title: String,
    description: String,
    selectedColor: Int,
    onColorChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdvancedPicker by remember { mutableStateOf(false) }
    
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick color selection
            QuickColorPicker(
                selectedColor = selectedColor,
                onColorChange = onColorChange
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Advanced picker toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedPicker = !showAdvancedPicker },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showAdvancedPicker) "Hide Advanced" else "Show Advanced",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Advanced color picker
            if (showAdvancedPicker) {
                Spacer(modifier = Modifier.height(16.dp))
                AdvancedColorPicker(
                    selectedColor = selectedColor,
                    onColorChange = onColorChange
                )
            }
        }
    }
}

@Composable
private fun QuickColorPicker(
    selectedColor: Int,
    onColorChange: (Int) -> Unit
) {
    val quickColors = listOf(
        Color(0xFF2E7D32) to "Green",
        Color(0xFF1976D2) to "Blue", 
        Color(0xFF7B1FA2) to "Purple",
        Color(0xFFD32F2F) to "Red",
        Color(0xFFFF8F00) to "Orange",
        Color(0xFF00ACC1) to "Cyan",
        Color(0xFF8BC34A) to "Light Green",
        Color(0xFFE91E63) to "Pink",
        Color(0xFF607D8B) to "Blue Grey",
        Color(0xFF795548) to "Brown",
        Color(0xFF000000) to "Black",
        Color(0xFFFFFFFF) to "White"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickColors.size) { index ->
            val (color, name) = quickColors[index]
            val isSelected = selectedColor == color.toArgb()
            
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.9f else 1f,
                animationSpec = tween(150),
                label = "colorScale"
            )
            
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { 
                        isPressed = true
                        onColorChange(color.toArgb())
                        // Reset press state
                        kotlinx.coroutines.GlobalScope.launch {
                            kotlinx.coroutines.delay(150)
                            isPressed = false
                        }
                    }
            )
        }
    }
}

@Composable
private fun AdvancedColorPicker(
    selectedColor: Int,
    onColorChange: (Int) -> Unit
) {
    val color = Color(selectedColor)
    
    // Convert RGB to HSV manually
    val rgb = color.toArgb()
    val r = ((rgb shr 16) and 0xFF) / 255f
    val g = ((rgb shr 8) and 0xFF) / 255f
    val b = (rgb and 0xFF) / 255f
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    
    val hue = when {
        delta == 0f -> 0f
        max == r -> ((g - b) / delta) % 6f * 60f
        max == g -> ((b - r) / delta + 2f) * 60f
        else -> ((r - g) / delta + 4f) * 60f
    }.let { if (it < 0) it + 360f else it }
    
    val saturation = if (max == 0f) 0f else delta / max
    val value = max
    
    var hueState by remember { mutableStateOf(hue) }
    var saturationState by remember { mutableStateOf(saturation) }
    var valueState by remember { mutableStateOf(value) }
    
    // Convert HSV back to RGB
    fun hsvToRgb(h: Float, s: Float, v: Float): Color {
        val c = v * s
        val x = c * (1 - kotlin.math.abs((h / 60f) % 2f - 1))
        val m = v - c
        
        val (r, g, b) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        
        return Color(
            red = (r + m).coerceIn(0f, 1f),
            green = (g + m).coerceIn(0f, 1f),
            blue = (b + m).coerceIn(0f, 1f)
        )
    }
    
    // Update color when sliders change
    val newColor = hsvToRgb(hueState, saturationState, valueState)
    
    Column {
        // Color preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(newColor)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hue slider
        Text(
            text = "Hue",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = hueState,
            onValueChange = { hueState = it },
            valueRange = 0f..360f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
        
        // Saturation slider
        Text(
            text = "Saturation",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = saturationState,
            onValueChange = { saturationState = it },
            valueRange = 0f..1f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
        
        // Value slider
        Text(
            text = "Brightness",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = valueState,
            onValueChange = { valueState = it },
            valueRange = 0f..1f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Apply button
        androidx.compose.material3.Button(
            onClick = { onColorChange(newColor.toArgb()) },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = newColor,
                contentColor = if (newColor.luminance() > 0.5f) Color.Black else Color.White
            )
        ) {
            Text("Apply Color")
        }
    }
}