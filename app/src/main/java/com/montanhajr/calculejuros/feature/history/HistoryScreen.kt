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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.core.ui.components.SimulationDetailModal
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToSimulator: (Long?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            HistoryHeader(onFilterClick = { showFilterSheet = true })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StatsRow(uiState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when(uiState.activeFilter) {
                        HistoryFilter.ALL -> "Todas as simulações"
                        HistoryFilter.INSTALLMENTS -> "Simulações (Parcelar)"
                        HistoryFilter.CASH -> "Simulações (À vista)"
                    },
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.recentSimulations.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhuma simulação encontrada", fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                uiState.recentSimulations.forEach { item ->
                    HistoryListItem(
                        item = item,
                        onClick = { viewModel.onSimulationClick(item.fullEntity) },
                        onDelete = { viewModel.requestDelete(item.fullEntity) },
                        onToggleFavorite = { viewModel.toggleFavorite(item.fullEntity) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ContinueSimulatingBanner { onNavigateToSimulator(null) }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        uiState.selectedSimulation?.let { simulation ->
            SimulationDetailModal(
                simulation = simulation,
                onDismissRequest = viewModel::onDismissModal,
                onReuse = { id -> onNavigateToSimulator(id) }
            )
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                currentFilter = uiState.activeFilter,
                onFilterSelected = {
                    viewModel.setFilter(it)
                    showFilterSheet = false
                },
                onDismissRequest = { showFilterSheet = false }
            )
        }

        uiState.pendingDelete?.let { _ ->
            AlertDialog(
                onDismissRequest = viewModel::dismissDeleteDialog,
                title = { Text("Excluir simulação?", fontFamily = SoraFont, fontWeight = FontWeight.Bold) },
                text = { Text("Esta ação não pode ser desfeita. A simulação será removida permanentemente do seu histórico.", fontFamily = DmSansFont) },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmDelete) {
                        Text("Excluir", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissDeleteDialog) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                "Filtrar por",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            FilterOption("Todos", currentFilter == HistoryFilter.ALL) { onFilterSelected(HistoryFilter.ALL) }
            FilterOption("Melhor parcelar", currentFilter == HistoryFilter.INSTALLMENTS) { onFilterSelected(HistoryFilter.INSTALLMENTS) }
            FilterOption("Melhor à vista", currentFilter == HistoryFilter.CASH) { onFilterSelected(HistoryFilter.CASH) }
        }
    }
}

@Composable
fun FilterOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontFamily = DmSansFont, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun HistoryHeader(onFilterClick: () -> Unit) {
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Acompanhe todas as suas simulações",
                fontFamily = DmSansFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        
        OutlinedButton(
            onClick = onFilterClick,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Filtrar", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontFamily = DmSansFont)
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
            iconColor = MaterialTheme.colorScheme.primary,
            iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            label = "Total de\nsimulações",
            value = uiState.totalSimulations,
            period = uiState.totalSimulationsPeriod,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            iconColor = MaterialTheme.colorScheme.secondary,
            iconBg = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            label = "Você pode\nganhar",
            value = uiState.potentialGain,
            period = uiState.potentialGainLabel,
            modifier = Modifier.weight(1.3f)
        )
        StatCard(
            icon = Icons.Default.Hexagon, // Placeholder for poly icon
            iconColor = MaterialTheme.colorScheme.tertiary,
            iconBg = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
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
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = DmSansFont, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = SoraFont, color = if (label.contains("ganhar")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface)
                Text(period, fontSize = 9.sp, color = if (label.contains("simulações")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = DmSansFont)
            }
        }
    }
}

@Composable
fun HistoryListItem(
    item: HistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.description, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(item.timestamp, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = (if (item.resultType == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.resultType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontFamily = SoraFont,
                            color = if (item.resultType == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (item.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (item.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.resultLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(
                    item.resultValue,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (item.resultType == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun ContinueSimulatingBanner(onNavigateToSimulator: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
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
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Continue simulando e\nfaça sempre a melhor escolha!",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = onNavigateToSimulator,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Nova simulação", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontFamily = SoraFont, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            }
        }
    }
}
