package com.montanhajr.calculejuros.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.ui.theme.SoraFont
import com.montanhajr.calculejuros.ui.theme.DmSansFont

data class CurrencyOption(
    val symbol: String,
    val name: String,
    val example: String
)

@Composable
fun CurrencySelectionDialog(
    currentSymbol: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currencies = listOf(
        CurrencyOption("R$", stringResource(R.string.name_brl), "R$ 1.234,56"),
        CurrencyOption("$", stringResource(R.string.name_usd), "$1,234.56"),
        CurrencyOption("€", stringResource(R.string.name_eur), "1.234,56 €"),
        CurrencyOption("£", stringResource(R.string.name_gbp), "£1,234.56")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.dialog_choose_currency_title),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.dialog_choose_currency_desc),
                    fontFamily = DmSansFont,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                currencies.forEach { option ->
                    val isSelected = option.symbol == currentSymbol
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCurrencySelected(option.symbol) },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = option.symbol,
                                    fontFamily = SoraFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.width(36.dp)
                                )
                                Column {
                                    Text(
                                        text = option.name,
                                        fontFamily = SoraFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = option.example,
                                        fontFamily = DmSansFont,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_btn_close), fontFamily = SoraFont, fontWeight = FontWeight.Bold)
            }
        }
    )
}
