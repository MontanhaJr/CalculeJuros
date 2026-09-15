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
import androidx.compose.runtime.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.ui.components.ProBadge
import com.montanhajr.calculejuros.core.util.toCurrency
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSimulator: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSimulationDetail: (String) -> Unit,
    onLearnToInvestClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    HomeHeader()
                    Spacer(modifier = Modifier.height(24.dp))
                    NewSimulationCard(onClick = onNavigateToSimulator)
                }
                
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    ProBadge(
                        isPro = uiState.isPro,
                        onClick = viewModel::togglePro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.calc_coin_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(130.dp)
                            .offset(x = 10.dp, y = (-20).dp) // "Invade" the badge area
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            SuggestedResultCard(
                result = uiState.suggestedResult,
                currencySymbol = uiState.currencySymbol,
                onClick = { uiState.suggestedResult?.id?.let(onNavigateToSimulationDetail) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsGrid(
                onRecentClick = onNavigateToHistory,
                onScenariosClick = onNavigateToFavorites,
                onLearnClick = onLearnToInvestClick
            )
            Spacer(modifier = Modifier.height(24.dp))
            RecentSimulationsHeader(onViewAllClick = onNavigateToHistory)
            Spacer(modifier = Modifier.height(12.dp))
            uiState.recentSimulations.forEach { simulation ->
                RecentSimulationItem(
                    simulation = simulation,
                    currencySymbol = uiState.currencySymbol,
                    onClick = { onNavigateToSimulationDetail(simulation.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 145.dp)
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurface)) {
                    append(stringResource(R.string.home_title_part1))
                }
                withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Black, fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(R.string.home_title_part2))
                }
            },
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.home_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
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
                    stringResource(R.string.home_card_new_sim_title),
                    fontFamily = SoraFont,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    stringResource(R.string.home_card_new_sim_desc),
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
fun SuggestedResultCard(result: RecentSimulation?, currencySymbol: String, onClick: () -> Unit) {
    if (result == null) return
    
    val isInstallment = result.type == "Parcelar"
    val accentColor = if (isInstallment) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = accentColor,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.home_suggested_result_label),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            buildAnnotatedString {
                                val prefix = stringResource(R.string.home_worth_it_prefix)
                                append(prefix)
                                if (!prefix.endsWith(" ")) append(" ")
                                val resultLabel = if (isInstallment) stringResource(R.string.home_worth_it_installment) else stringResource(R.string.home_worth_it_cash)
                                withStyle(SpanStyle(fontFamily = SoraFont, fontWeight = FontWeight.Bold, color = accentColor)) {
                                    append(resultLabel)
                                }
                            },
                            fontFamily = SoraFont,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.home_view_details), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(modifier = Modifier.fillMaxWidth().padding(end = 100.dp)) {
                    Text(stringResource(R.string.home_suggested_details), fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Text(
                        result.difference.toCurrency(currencySymbol),
                        fontFamily = SoraFont,
                        color = accentColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        stringResource(R.string.home_suggested_yield),
                        fontFamily = DmSansFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Image(
                painter = painterResource(id = R.drawable.cash_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
            )
        }
    }
}

@Composable
fun QuickActionsGrid(
    onRecentClick: () -> Unit,
    onScenariosClick: () -> Unit,
    onLearnClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.History,
            title = stringResource(R.string.home_action_recent_title),
            subtitle = stringResource(R.string.home_action_recent_desc),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onClick = onRecentClick
        )
        QuickActionCard(
            icon = Icons.Default.Star,
            title = stringResource(R.string.home_action_scenarios_title),
            subtitle = stringResource(R.string.home_action_scenarios_desc),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onClick = onScenariosClick
        )
        QuickActionCard(
            icon = Icons.Default.Lightbulb,
            title = stringResource(R.string.home_action_learn_title),
            subtitle = stringResource(R.string.home_action_learn_desc),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onClick = onLearnClick
        )
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontFamily = DmSansFont,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
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
        Text(
            stringResource(R.string.home_recent_header),
            fontFamily = SoraFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = onViewAllClick) {
            Text(stringResource(R.string.home_view_all), fontFamily = SoraFont, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        }
    }
}

@Composable
fun RecentSimulationItem(simulation: RecentSimulation, currencySymbol: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = (if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    if (simulation.type == "Parcelar") Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.Savings,
                    contentDescription = null,
                    tint = if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(10.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                val displayTitle = if (simulation.title.isEmpty() || simulation.title == "Simulação") stringResource(R.string.default_simulation_name) else simulation.title
                Text(displayTitle, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(stringResource(R.string.label_installments_card, simulation.installmentsCount), fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(stringResource(R.string.home_recent_yield_format, simulation.annualProfitability), fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                val typeLabel = if (simulation.type == "Parcelar") stringResource(R.string.winner_installments) else stringResource(R.string.winner_cash)
                Surface(
                    color = (if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        typeLabel,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = SoraFont,
                        color = if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(simulation.difference.toCurrency(currencySymbol), fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (simulation.type == "Parcelar") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
