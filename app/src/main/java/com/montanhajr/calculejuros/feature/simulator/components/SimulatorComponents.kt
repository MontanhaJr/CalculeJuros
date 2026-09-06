package com.montanhajr.calculejuros.feature.simulator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import com.montanhajr.calculejuros.ui.theme.SoraFont

@Composable
fun FavoriteOption(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (isFavorite) Color(0xFFFFB800) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isFavorite) stringResource(R.string.status_saved_favorite) else stringResource(R.string.status_add_favorite),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isFavorite) Color(0xFFFFB800) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SaveFavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.dialog_save_favorite_title),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.dialog_save_favorite_desc),
                    fontFamily = DmSansFont,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(stringResource(R.string.dialog_save_favorite_hint), fontFamily = DmSansFont) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.btn_save), fontFamily = SoraFont, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel), fontFamily = SoraFont)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

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
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontFamily = DmSansFont)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val currencySymbols = listOf("R$", "$", "€", "£")
                        val (visualPrefix, visualSuffix) = when {
                            suffix == null -> "" to ""
                            suffix in currencySymbols -> "$suffix " to ""
                            else -> "" to " $suffix"
                        }
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
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                            visualTransformation = DecimalVisualTransformation(
                                prefix = visualPrefix,
                                suffix = visualSuffix
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (trailingIcon != null) {
                    Icon(trailingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            if (helperText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(helperText, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontFamily = DmSansFont)
            }
        }
    }
}

class DecimalVisualTransformation(
    private val prefix: String = "",
    private val suffix: String = ""
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val digits = originalText.filter { it.isDigit() }
        
        val formatted = if (digits.isEmpty()) {
            "0,00"
        } else {
            val longValue = digits.toLongOrNull() ?: 0L
            val padded = longValue.toString().padStart(3, '0')
            val integerPart = padded.substring(0, padded.length - 2)
            val decimalPart = padded.substring(padded.length - 2)
            "$integerPart,$decimalPart"
        }
        
        val out = "$prefix$formatted$suffix"
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (digits.isEmpty()) return prefix.length + 4
                val formattedLength = formatted.length
                val diff = formattedLength - digits.length
                val transformedOffset = if (offset == 0) {
                    prefix.length
                } else {
                    prefix.length + offset + diff
                }
                return transformedOffset.coerceIn(0, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (digits.isEmpty()) return 0
                val formattedLength = formatted.length
                val diff = formattedLength - digits.length
                val originalOffset = offset - prefix.length - diff
                return originalOffset.coerceIn(0, originalText.length)
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
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .padding(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontFamily = SoraFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.label_installments_count), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontFamily = DmSansFont)
                    BasicTextField(
                        value = if (value == 0) "" else value.toString(),
                        onValueChange = onTextChange,
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SoraFont,
                            color = MaterialTheme.colorScheme.onSurface
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
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        shape = RoundedCornerShape(8.dp),
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SoraFont,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OptionalSection(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        
        if (checked) {
            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SimulatorSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title.uppercase(),
            fontFamily = SoraFont,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimulatorInputFieldCurrencyPreview() {
    SimulatorInputField(
        label = "Valor do produto",
        value = "150000",
        onValueChange = {},
        icon = Icons.Default.ShoppingBag,
        suffix = "R$"
    )
}

@Preview(showBackground = true)
@Composable
fun SimulatorInputFieldPercentagePreview() {
    SimulatorInputField(
        label = "Desconto",
        value = "1000",
        onValueChange = {},
        icon = Icons.Default.LocalOffer,
        suffix = "%"
    )
}

@Preview(showBackground = true)
@Composable
fun SimulatorSectionPreview() {
    SimulatorSection(title = "Pagamento à Vista") {
        SimulatorInputField(
            label = "Desconto",
            value = "1000",
            onValueChange = {},
            icon = Icons.Default.LocalOffer,
            suffix = "%"
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
