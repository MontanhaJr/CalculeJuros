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
import com.montanhajr.calculejuros.core.domain.model.WinnerType
import com.montanhajr.calculejuros.ui.theme.MonoValueStyle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultCard(
    result: SimulationResult,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    val containerColor = when (result.winner) {
        WinnerType.CASH -> MaterialTheme.colorScheme.primary
        WinnerType.INSTALLMENT -> MaterialTheme.colorScheme.secondary
        WinnerType.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (result.winner == WinnerType.NEUTRAL) {
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
                text = when (result.winner) {
                    WinnerType.CASH -> "PAGAR À VISTA COMPENSA MAIS!"
                    WinnerType.INSTALLMENT -> "PARCELAR E INVESTIR COMPENSA MAIS!"
                    WinnerType.NEUTRAL -> "PRATICAMENTE EMPATE"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Custo à vista:", style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.cashTotalCost), style = MonoValueStyle)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Custo parcelado:", style = MaterialTheme.typography.bodyMedium)
                Text(currencyFormat.format(result.installmentTotalCost), style = MonoValueStyle)
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = contentColor.copy(alpha = 0.2f)
            )
            
            Text(
                text = "Economia de ${currencyFormat.format(result.difference)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
