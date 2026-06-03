package com.montanhajr.calculejuros.feature.history

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToSimulator: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            HistoryHeader()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StatsRow(uiState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simulações recentes", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Ver todas >", fontFamily = SoraFont, color = BrandPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            uiState.recentSimulations.forEach { item ->
                HistoryListItem(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ContinueSimulatingBanner(onNavigateToSimulator)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HistoryHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Histórico",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "Acompanhe todas as suas simulações",
                fontFamily = DmSansFont,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        OutlinedButton(
            onClick = { /* TODO: Filter */ },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Filtrar", color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
        }
    }
}

@Composable
fun StatsRow(uiState: HistoryUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            iconColor = BrandPurple,
            iconBg = BrandPurple.copy(alpha = 0.1f),
            label = "Total de\nsimulações",
            value = uiState.totalSimulations,
            period = uiState.totalSimulationsPeriod,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            iconColor = BrandGreen,
            iconBg = BrandGreen.copy(alpha = 0.1f),
            label = "Você pode\nganhar",
            value = uiState.potentialGain,
            period = uiState.potentialGainLabel,
            modifier = Modifier.weight(1.3f)
        )
        StatCard(
            icon = Icons.Default.Hexagon, // Placeholder for poly icon
            iconColor = BrandYellow,
            iconBg = BrandYellow.copy(alpha = 0.1f),
            label = "Média por\nsimulação",
            value = uiState.averageGain,
            period = uiState.averageGainLabel,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    label: String,
    value: String,
    period: String,
    modifier: Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = iconBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(6.dp))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, fontSize = 9.sp, color = Color.Gray, fontFamily = DmSansFont, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = SoraFont, color = if (label.contains("ganhar")) BrandGreen else Color.Black)
                Text(period, fontSize = 9.sp, color = if (label.contains("simulações")) BrandPurple else Color.Gray, fontFamily = DmSansFont)
            }
        }
    }
}

@Composable
fun HistoryListItem(item: HistoryItem) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = when(item.iconType) {
                        "laptop" -> Icons.Default.Laptop
                        "phone" -> Icons.Default.Smartphone
                        "tv" -> Icons.Default.Tv
                        "watch" -> Icons.Default.Watch
                        "ps5" -> Icons.Default.Gamepad
                        else -> Icons.Default.ShoppingBag
                    },
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.description, fontFamily = DmSansFont, color = Color.Gray, fontSize = 11.sp)
                Text(item.timestamp, fontFamily = DmSansFont, color = Color.LightGray, fontSize = 10.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = (if (item.resultType == "Parcelar") BrandGreen else Color.Red).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.resultType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontFamily = SoraFont,
                        color = if (item.resultType == "Parcelar") BrandGreen else Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.resultLabel, color = Color.Gray, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(
                    item.resultValue,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (item.resultType == "Parcelar") BrandGreen else Color.Red
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ContinueSimulatingBanner(onNavigateToSimulator: () -> Unit) {
    Surface(
        color = BrandPurple,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = BrandYellow, modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Continue simulando e\nfaça sempre a melhor escolha!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = onNavigateToSimulator,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Nova simulação", color = BrandPurple, fontSize = 11.sp, fontFamily = SoraFont, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(14.dp))
            }
        }
    }
}
