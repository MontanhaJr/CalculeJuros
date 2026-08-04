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
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    val containerColor = when (result.recommendation) {
        RecommendationType.A_VISTA -> MaterialTheme.colorScheme.primary
        RecommendationType.PARCELADO -> MaterialTheme.colorScheme.secondary
        RecommendationType.EMPATE -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (result.recommendation == RecommendationType.EMPATE) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onPrimary
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
            Text(
                text = when (result.recommendation) {
                    RecommendationType.A_VISTA -> "PAGAR À VISTA COMPENSA MAIS!"
                    RecommendationType.PARCELADO -> "PARCELAR E INVESTIR COMPENSA MAIS!"
                    RecommendationType.EMPATE -> "PRATICAMENTE EMPATE"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ganho à vista:", style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.netGainCash), style = MonoValueStyle)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ganho parcelado:", style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.netGainInstallment), style = MonoValueStyle)
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = contentColor.copy(alpha = 0.2f)
            )
            
            Text(
                text = "Diferença de ${currencyFormat.format(result.difference)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
