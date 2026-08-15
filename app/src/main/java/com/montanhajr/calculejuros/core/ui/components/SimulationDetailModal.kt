package com.montanhajr.calculejuros.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import com.montanhajr.calculejuros.ui.theme.SoraFont
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationDetailModal(
    simulation: SimulationEntity,
    onDismissRequest: () -> Unit,
    onReuse: (Long) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
        ) {
            Text(
                text = simulation.scenarioName ?: "Detalhes da Simulação",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault()).format(Date(simulation.date)),
                fontFamily = DmSansFont,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DetailItem(label = "Valor do Produto", value = "R$ ${String.format("%.2f", simulation.productPrice)}")
            DetailItem(label = "Valor à Vista", value = "R$ ${String.format("%.2f", simulation.cashPrice)}")
            DetailItem(label = "Parcelamento", value = "${simulation.installmentsCount}x de R$ ${String.format("%.2f", simulation.installmentValue)}")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = (if (simulation.winner == "PARCELADO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (simulation.winner == "PARCELADO") Icons.Default.TrendingUp else Icons.Default.Savings,
                        contentDescription = null,
                        tint = if (simulation.winner == "PARCELADO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            if (simulation.winner == "PARCELADO") "Melhor: Parcelado" else "Melhor: À Vista",
                            fontWeight = FontWeight.Bold,
                            fontFamily = SoraFont,
                            fontSize = 14.sp,
                            color = if (simulation.winner == "PARCELADO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Vantagem de R$ ${String.format("%.2f", simulation.difference)}",
                            fontFamily = DmSansFont,
                            fontSize = 12.sp,
                            color = if (simulation.winner == "PARCELADO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    onDismissRequest()
                    onReuse(simulation.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reutilizar Simulação", fontFamily = SoraFont, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontFamily = SoraFont, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
