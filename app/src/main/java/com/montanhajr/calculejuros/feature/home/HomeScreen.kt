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
    onNavigateToSimulator: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { HomeTopBar() },
        containerColor = BrandBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            HomeHeader()
            Spacer(modifier = Modifier.height(24.dp))
            NewSimulationCard(onClick = onNavigateToSimulator)
            Spacer(modifier = Modifier.height(24.dp))
            SuggestedResultCard(uiState.suggestedResult)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsGrid()
            Spacer(modifier = Modifier.height(24.dp))
            RecentSimulationsHeader()
            Spacer(modifier = Modifier.height(12.dp))
            uiState.recentSimulations.forEach { simulation ->
                RecentSimulationItem(simulation)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFFFF8E1),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star, // Placeholder for Crown
                    contentDescription = null,
                    tint = BrandYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "PRO",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandYellow,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
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
                    withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 36.sp, color = Color(0xFF1A1A1A))) {
                        append("Parcelar\n")
                    }
                    withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 36.sp, color = BrandPurple)) {
                        append("ou Investir?")
                    }
                },
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Descubra o que realmente vale mais a pena",
                color = Color.Gray,
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
                tint = BrandPurple.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun NewSimulationCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = BrandPurple,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = BrandPurple,
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Compare à vista vs parcelado\ne veja o melhor para você",
                    fontFamily = DmSansFont,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestedResultCard(result: RecentSimulation?) {
    if (result == null) return
    
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BrandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.padding(4.dp).size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Surface(
                        color = BrandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Resultado sugerido",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = BrandGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            append("Vale a pena ")
                            withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Bold, color = if (result.type == "Parcelar") BrandGreen else Color.Red)) {
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
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
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
                    Text(result.details, fontFamily = DmSansFont, color = Color.Gray, fontSize = 12.sp)
                    Text(result.value, fontFamily = SoraFont, color = BrandGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text(result.yield, fontFamily = DmSansFont, color = Color.Gray, fontSize = 11.sp)
                }
                // Placeholder for piggy bank illustration
                Icon(
                    Icons.Default.ShoppingCart, // Placeholder
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = BrandGreen.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun QuickActionsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionItem("Simulações\nRecentes", "Continue de onde\nparou", Icons.Default.Refresh, Modifier.weight(1f))
        QuickActionItem("Cenários\nProntos", "Exemplos para\nte ajudar", Icons.AutoMirrored.Filled.List, Modifier.weight(1f))
        QuickActionItem("Comparar\nCartões", "Veja taxas e escolha\no melhor", Icons.Default.Info, Modifier.weight(1f))
        QuickActionItem("Aprenda\nMais", "Entenda os\nconceitos", Icons.Default.Book, Modifier.weight(1f))
    }
}

@Composable
fun QuickActionItem(title: String, subtitle: String, icon: ImageVector, modifier: Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = modifier.height(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
             Surface(
                color = BrandPurple.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(8.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontFamily = DmSansFont, color = Color.Gray, fontSize = 9.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun RecentSimulationsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulações recentes", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Text("Ver todas >", fontFamily = SoraFont, color = BrandPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RecentSimulationItem(simulation: RecentSimulation) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.DesktopMac, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(simulation.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(simulation.details, fontFamily = DmSansFont, color = Color.Gray, fontSize = 11.sp)
                Text(simulation.yield, fontFamily = DmSansFont, color = Color.Gray, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = (if (simulation.type == "Parcelar") BrandGreen else Color.Red).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        simulation.type,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = SoraFont,
                        color = if (simulation.type == "Parcelar") BrandGreen else Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(simulation.value, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (simulation.type == "Parcelar") BrandGreen else Color.Red)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
