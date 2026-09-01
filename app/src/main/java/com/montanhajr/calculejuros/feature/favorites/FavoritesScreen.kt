package com.montanhajr.calculejuros.feature.favorites

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.ui.components.SimulationDetailModal
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToSimulator: (Long?) -> Unit,
    onNavigateToResult: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            FavoritesHeader { onNavigateToSimulator(null) }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            QuickAccessCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.title_my_favorites), fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TextButton(onClick = { /* TODO */ }) {
                    Text(stringResource(R.string.btn_edit), fontFamily = SoraFont, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            uiState.favoriteSimulations.forEach { item ->
                FavoriteListItem(
                    item = item,
                    onClick = { viewModel.onSimulationClick(item.fullEntity) },
                    onToggleFavorite = { viewModel.requestUnfavorite(item.fullEntity) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TipCard()
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        uiState.selectedSimulation?.let { simulation ->
            SimulationDetailModal(
                simulation = simulation,
                onDismissRequest = viewModel::onDismissModal,
                onReuse = { id -> onNavigateToSimulator(id) },
                onViewResult = { id -> onNavigateToResult(id) }
            )
        }

        uiState.pendingUnfavorite?.let { simulation ->
            AlertDialog(
                onDismissRequest = viewModel::dismissUnfavoriteDialog,
                title = { Text(stringResource(R.string.dialog_unfavorite_title), fontFamily = SoraFont, fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(R.string.dialog_unfavorite_desc), fontFamily = DmSansFont) },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmUnfavorite) {
                        Text(stringResource(R.string.btn_remove), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissUnfavoriteDialog) {
                        Text(stringResource(R.string.btn_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
fun FavoritesHeader(onNavigateToSimulator: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.title_favorites),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.subtitle_favorites),
                fontFamily = DmSansFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        
        Surface(
            onClick = onNavigateToSimulator,
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.height(56.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.btn_new_simulation).replace(" ", "\n"),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickAccessCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(12.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.card_quick_access_title), fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    stringResource(R.string.card_quick_access_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont
                )
            }
            
            Image(
                painter = painterResource(id = R.drawable.fav_file_icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun FavoriteListItem(item: FavoriteItem, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
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
                Text(item.creationDate, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = (if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.resultType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontFamily = SoraFont,
                            color = if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Star, contentDescription = stringResource(R.string.content_desc_remove_favorite), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.resultLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(
                    item.resultValue,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun TipCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.card_tip_title), fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    stringResource(R.string.card_tip_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = DmSansFont
                )
            }
            
            // Placeholder for chart icon
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
        }
    }
}
