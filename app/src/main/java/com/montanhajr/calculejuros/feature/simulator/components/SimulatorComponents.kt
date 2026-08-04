package com.montanhajr.calculejuros.feature.simulator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun ModeSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.2f))
            .padding(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontFamily = SoraFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) BrandPurple else Color.Gray
                )
            }
        }
    }
}

@Composable
fun InstallmentSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    onTextChange: (String) -> Unit
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
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Número de parcelas", color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = if (value == 0) "" else value.toString(),
                            onValueChange = onTextChange,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SoraFont,
                                color = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.widthIn(min = 32.dp)
                        )
                        Text("x", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = SoraFont)
                    }
                }
            }
            
            Slider(
                value = value.toFloat().coerceIn(1f, 60f),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 1f..60f,
                steps = 58, // 1 to 60 has 58 steps in between if we want integers
                colors = SliderDefaults.colors(
                    thumbColor = BrandPurple,
                    activeTrackColor = BrandPurple,
                    inactiveTrackColor = BrandPurple.copy(alpha = 0.2f)
                )
            )
        }
    }
}


