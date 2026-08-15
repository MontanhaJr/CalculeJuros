package com.montanhajr.calculejuros.feature.home

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSimulator: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSimulationDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            HomeHeader()
            Spacer(modifier = Modifier.height(24.dp))
            NewSimulationCard(onClick = onNavigateToSimulator)
            Spacer(modifier = Modifier.height(24.dp))
            SuggestedResultCard(
                result = uiState.suggestedResult,
                onClick = { uiState.suggestedResult?.id?.let(onNavigateToSimulationDetail) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsGrid(
                onRecentClick = onNavigateToHistory,
                onScenariosClick = onNavigateToFavorites
            )
            Spacer(modifier = Modifier.height(24.dp))
            RecentSimulationsHeader(onViewAllClick = onNavigateToHistory)
            Spacer(modifier = Modifier.height(12.dp))
            uiState.recentSimulations.forEach { simulation ->
                RecentSimulationItem(
                    simulation = simulation,
                    onClick = { onNavigateToSimulationDetail(simulation.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 36.sp, color = MaterialTheme.colorScheme.onSurface)) {
                        append("Parcelar\n")
                    }
                    withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)) {
                        append("ou Investir?")
                    }
                },
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Descubra o que realmente vale mais a pena",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        // Placeholder for Calculator Illustration
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
             Icon(
                Icons.Default.Build, // Placeholder for calculator icon
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun NewSimulationCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Nova Simulação",
                    fontFamily = SoraFont,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Compare à vista vs parcelado\ne veja o melhor para você",
                    fontFamily = DmSansFont,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestedResultCard(result: RecentSimulation?, onClick: () -> Unit) {
    if (result == null) return
    
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(4.dp).size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Resultado sugerido",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            append("Vale a pena ")
                            withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Bold, color = if (result.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)) {
                                append(if (result.type == "Parcelar") "PARCELAR!" else "À VISTA!")
                            }
                        },
                        fontFamily = SoraFont,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ver detalhes", fontSize = 11.sp)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(result.details, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text(result.value, fontFamily = SoraFont, color = MaterialTheme.colorScheme.secondary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text(result.yield, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
                // Placeholder for piggy bank illustration
                Icon(
                    Icons.Default.ShoppingCart, // Placeholder
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    onRecentClick: () -> Unit,
    onScenariosClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionItem(
            title = "Simulações\nRecentes",
            subtitle = "Continue de onde\nparou",
            icon = Icons.Default.Refresh,
            modifier = Modifier.weight(1f),
            onClick = onRecentClick
        )
        QuickActionItem(
            title = "Cenários\nProntos",
            subtitle = "Exemplos para\nte ajudar",
            icon = Icons.AutoMirrored.Filled.List,
            modifier = Modifier.weight(1f),
            onClick = onScenariosClick
        )
        QuickActionItem(
            title = "Aprenda\nMais",
            subtitle = "Entenda os\nconceitos",
            icon = Icons.Default.Book,
            modifier = Modifier.weight(1f),
            onClick = { /* TODO: Open Learn More */ }
        )
    }
}

@Composable
fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.height(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
             Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun RecentSimulationsHeader(onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulações recentes", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        TextButton(onClick = onViewAllClick) {
            Text("Ver todas >", fontFamily = SoraFont, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecentSimulationItem(simulation: RecentSimulation, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.DesktopMac, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(simulation.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(simulation.details, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(simulation.yield, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = (if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        simulation.type,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = SoraFont,
                        color = if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(simulation.value, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
