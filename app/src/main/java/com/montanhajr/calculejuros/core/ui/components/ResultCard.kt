package com.montanhajr.calculejuros.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.domain.model.SimulationResult
import com.montanhajr.calculejuros.core.domain.model.RecommendationType
import com.montanhajr.calculejuros.ui.theme.MonoValueStyle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultCard(
    result: SimulationResult,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    
    val containerColor = when (result.recommendation) {
        RecommendationType.A_VISTA -> MaterialTheme.colorScheme.primary
        RecommendationType.PARCELADO -> MaterialTheme.colorScheme.secondary
        RecommendationType.EMPATE -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (result.recommendation) {
        RecommendationType.A_VISTA -> MaterialTheme.colorScheme.onPrimary
        RecommendationType.PARCELADO -> MaterialTheme.colorScheme.onSecondary
        RecommendationType.EMPATE -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val recommendationText = when (result.recommendation) {
                RecommendationType.A_VISTA -> stringResource(R.string.result_cash_better)
                RecommendationType.PARCELADO -> {
                    if (result.prepaidInstallmentsCount > 0 && result.prepaymentDiscountValue > 0) {
                        stringResource(R.string.result_installment_prepayment_better)
                    } else {
                        stringResource(R.string.result_installment_better)
                    }
                }
                RecommendationType.EMPATE -> stringResource(R.string.result_tie)
            }

            Text(
                text = recommendationText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.label_gain_cash), style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.netGainCash), style = MonoValueStyle)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.label_gain_installment), style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.netGainInstallment), style = MonoValueStyle)
            }

            if (result.prepaidInstallmentsCount > 0 && result.prepaymentDiscountValue > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = contentColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.label_standard_installment_gain), style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
                            Text(currencyFormat.format(result.netGainStandardInstallment), style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val benefit = result.netGainInstallment - result.netGainStandardInstallment
                            Text(stringResource(R.string.label_prepayment_benefit), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(currencyFormat.format(benefit), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = contentColor.copy(alpha = 0.2f)
            )
            
            Text(
                text = stringResource(R.string.label_difference, currencyFormat.format(result.difference)),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
