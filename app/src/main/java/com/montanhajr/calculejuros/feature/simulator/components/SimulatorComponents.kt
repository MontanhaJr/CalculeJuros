package com.montanhajr.calculejuros.feature.simulator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
                            onValueChange = { newValue ->
                                val digitsOnly = newValue.filter { it.isDigit() }
                                onValueChange(digitsOnly)
                            },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SoraFont,
                                color = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                            visualTransformation = DecimalVisualTransformation(),
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

class DecimalVisualTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val digits = originalText.filter { it.isDigit() }
        
        val out = if (digits.isEmpty()) {
            "0,00"
        } else {
            val longValue = digits.toLongOrNull() ?: 0L
            val padded = longValue.toString().padStart(3, '0')
            val integerPart = padded.substring(0, padded.length - 2)
            val decimalPart = padded.substring(padded.length - 2)
            "$integerPart,$decimalPart"
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (digits.isEmpty()) return 4 // End of "0,00"
                
                val diff = out.length - originalText.length
                return (offset + diff).coerceIn(0, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val diff = out.length - originalText.length
                return (offset - diff).coerceIn(0, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class SuffixVisualTransformation(private val suffix: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val out = if (originalText.isEmpty()) "" else "$originalText$suffix"
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset
            override fun transformedToOriginal(offset: Int): Int {
                if (offset > originalText.length) return originalText.length
                return offset
            }
        }
        
        return TransformedText(AnnotatedString(out), offsetMapping)
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
    val suggestions = listOf(1, 6, 12, 24, 36, 48)

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
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Número de parcelas", color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
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
                        visualTransformation = SuffixVisualTransformation("x"),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        label = "${suggestion}x",
                        selected = value == suggestion,
                        onClick = { onValueChange(suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) BrandPurple else BrandPurple.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        border = if (selected) null else BorderStroke(1.dp, BrandPurple.copy(alpha = 0.1f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) Color.White else BrandPurple,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SoraFont,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstallmentSelectorPreview() {
    InstallmentSelector(
        value = 12,
        onValueChange = {},
        onTextChange = {}
    )
}
