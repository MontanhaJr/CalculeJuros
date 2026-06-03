package com.montanhajr.calculejuros.feature.simulator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.ui.theme.BrandPurple
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import com.montanhajr.calculejuros.ui.theme.SoraFont

@Composable
fun SimulatorInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    suffix: String? = null,
    helperText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Number,
    trailingIcon: ImageVector? = null
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BrandPurple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SoraFont,
                                color = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                            modifier = Modifier.weight(1f)
                        )
                        if (suffix != null) {
                            Text(suffix, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SoraFont)
                        }
                    }
                }
                if (trailingIcon != null) {
                    Icon(trailingIcon, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(20.dp))
                }
            }
            if (helperText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(helperText, color = Color.Gray, fontSize = 11.sp, fontFamily = DmSansFont)
            }
        }
    }
}

@Composable
fun SimulatorSliderField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    suffix: String,
    helperText: String? = null,
    range: ClosedFloatingPointRange<Float> = 0f..24f,
    steps: Int = 23
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFFFF3E0), // Light Orange
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${value.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = SoraFont)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(suffix, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SoraFont)
                    }
                }
            }
            if (helperText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(helperText, color = Color.Gray, fontSize = 11.sp, fontFamily = DmSansFont)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = BrandPurple,
                    activeTrackColor = BrandPurple,
                    inactiveTrackColor = BrandPurple.copy(alpha = 0.2f)
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(0, 6, 12, 18, 24).forEach { 
                    Text("${it}%", fontSize = 10.sp, color = if (value.toInt() == it) BrandPurple else Color.Gray, fontWeight = if (value.toInt() == it) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}
