package com.montanhajr.calculejuros.feature.simulator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.domain.model.RecommendationType
import com.montanhajr.calculejuros.core.domain.model.SimulationResult
import com.montanhajr.calculejuros.core.util.toCurrency
import com.montanhajr.calculejuros.feature.simulator.components.FavoriteOption
import com.montanhajr.calculejuros.feature.simulator.components.SaveFavoriteDialog
import com.montanhajr.calculejuros.ui.theme.*
import java.text.NumberFormat
import java.util.*
import com.patrykandpatrick.vico.compose.cartesian.*
import com.patrykandpatrick.vico.compose.cartesian.axis.*
import com.patrykandpatrick.vico.compose.cartesian.layer.*
import com.patrykandpatrick.vico.compose.common.shader.*
import com.patrykandpatrick.vico.core.cartesian.data.*
import com.patrykandpatrick.vico.core.cartesian.layer.*
import com.patrykandpatrick.vico.core.common.shader.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ResultViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            uiState.result?.let { result ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    // Custom Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            stringResource(R.string.title_simulation_result),
                            fontFamily = SoraFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    RecommendationHeader(result, uiState.currencySymbol)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    FavoriteOption(
                        isFavorite = uiState.isFavorite,
                        onClick = viewModel::onFavoriteClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ComparisonCards(result, uiState.currencySymbol)
                    
                    if (result.prepaidInstallmentsCount > 0 && result.prepaymentDiscountValue > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PrepaymentBenefitSection(result, uiState.currencySymbol)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        stringResource(R.string.label_patrimonial_evolution),
                        fontFamily = SoraFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    EvolutionChart(result)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    DetailedTable(result, uiState.currencySymbol)

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Result not found")
                }
            }
        }
    }

    if (uiState.showSaveDialog) {
        SaveFavoriteDialog(
            onDismiss = viewModel::onDismissSaveDialog,
            onConfirm = viewModel::onConfirmSaveFavorite
        )
    }
}

@Composable
fun RecommendationHeader(result: SimulationResult, currencySymbol: String) {
    val bgColor = when (result.recommendation) {
        RecommendationType.A_VISTA -> Color(0xFFE8F5E9)
        RecommendationType.PARCELADO -> Color(0xFFE3F2FD)
        RecommendationType.EMPATE -> Color(0xFFFFF3E0)
    }
    val textColor = when (result.recommendation) {
        RecommendationType.A_VISTA -> Color(0xFF2E7D32)
        RecommendationType.PARCELADO -> Color(0xFF1565C0)
        RecommendationType.EMPATE -> Color(0xFFEF6C00)
    }
    val label = when (result.recommendation) {
        RecommendationType.A_VISTA -> stringResource(R.string.rec_label_cash)
        RecommendationType.PARCELADO -> {
            val isPrepaymentBetter = result.netGainInstallment > result.netGainStandardInstallment + 0.005
            if (result.prepaidInstallmentsCount > 0 && result.prepaymentDiscountValue > 0 && isPrepaymentBetter) {
                stringResource(R.string.result_installment_prepayment_better)
            } else {
                stringResource(R.string.rec_label_installment)
            }
        }
        RecommendationType.EMPATE -> stringResource(R.string.rec_label_tie)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rec_title),
                fontFamily = DmSansFont,
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.8f)
            )
            Text(
                text = label,
                fontFamily = SoraFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
            
            if (result.recommendation != RecommendationType.EMPATE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.rec_savings_desc, result.difference.toCurrency(currencySymbol)),
                    fontFamily = DmSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun ComparisonCards(result: SimulationResult, currencySymbol: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.summary_card_cash),
            value = result.netGainCash.toCurrency(currencySymbol),
            subValue = stringResource(R.string.summary_sub_interest, result.interestGainedCash.toCurrency(currencySymbol)),
            color = Color(0xFF2E7D32)
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.summary_card_installment),
            value = result.netGainStandardInstallment.toCurrency(currencySymbol),
            subValue = stringResource(R.string.summary_sub_installments, result.installmentsCount),
            color = Color(0xFF1565C0)
        )
    }
}

@Composable
fun PrepaymentBenefitSection(result: SimulationResult, currencySymbol: String) {
    val benefit = result.netGainPrepaidInstallment - result.netGainStandardInstallment
    val isPositive = benefit > 0.005
    val containerColor = if (isPositive) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
    }
    val contentColor = if (isPositive) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.error
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isPositive) Icons.Default.Lightbulb else Icons.Default.Info,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(if (isPositive) R.string.title_prepayment_positive else R.string.title_prepayment_negative),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = contentColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.label_standard_installment_gain),
                    fontFamily = DmSansFont,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    result.netGainStandardInstallment.toCurrency(currencySymbol),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(if (isPositive) R.string.label_prepayment_benefit else R.string.label_prepayment_loss),
                    fontFamily = DmSansFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    benefit.toCurrency(currencySymbol),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = contentColor.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(if (isPositive) R.string.desc_prepayment_analysis_positive else R.string.desc_prepayment_analysis_negative),
                fontFamily = DmSansFont,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = contentColor.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_total_prepayment_gain),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = contentColor
                )
                Text(
                    result.netGainPrepaidInstallment.toCurrency(currencySymbol),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subValue: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontFamily = DmSansFont, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subValue, fontFamily = DmSansFont, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EvolutionChart(result: SimulationResult) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(result) {
        modelProducer.runTransaction {
            lineSeries {
                series(result.monthlyDetails.map { it.cashBalance })
                series(result.monthlyDetails.map { it.installmentBalance })
                result.monthlyDetails.firstOrNull()?.prepaymentInstallmentBalance?.let {
                    series(result.monthlyDetails.map { it.prepaymentInstallmentBalance ?: 0.0 })
                }
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(shader = DynamicShader.color(Color(0xFF2E7D32))), // Cash
                    rememberLineSpec(shader = DynamicShader.color(Color(0xFF1565C0))), // Standard
                    rememberLineSpec(shader = DynamicShader.color(Color(0xFFFF9800)))  // Prepayment (Orange)
                )
            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 10.sp),
                valueFormatter = { value, _, _ -> value.toDouble().toCompactCurrency() }
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 10.sp),
                valueFormatter = { value, _, _ -> "M${value.toInt()}" }
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(Color(0xFF2E7D32), stringResource(R.string.legend_cash))
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(Color(0xFF1565C0), stringResource(R.string.legend_installment))
        if (result.monthlyDetails.any { it.prepaymentInstallmentBalance != null }) {
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(Color(0xFFFF9800), stringResource(R.string.label_prepayment_legend))
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontFamily = DmSansFont, fontSize = 12.sp)
    }
}

@Composable
fun DetailedTable(result: SimulationResult, currencySymbol: String) {
    val hasPrepayment = result.monthlyDetails.any { it.prepaymentInstallmentBalance != null }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Text(stringResource(R.string.table_month), modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text(stringResource(R.string.summary_card_cash), modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End)
            Text(stringResource(R.string.label_standard_short), modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End)
            if (hasPrepayment) {
                Text(stringResource(R.string.label_prepayment_short), modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End)
            }
        }
        
        result.monthlyDetails.forEach { detail ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(detail.month.toString(), modifier = Modifier.weight(0.8f), fontSize = 11.sp)
                
                // À Vista
                Text(
                    text = detail.cashBalance.toCurrency(currencySymbol),
                    modifier = Modifier.weight(1.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.End,
                    color = Color(0xFF2E7D32)
                )
                
                // Padrão
                Text(
                    text = detail.installmentBalance.toCurrency(currencySymbol),
                    modifier = Modifier.weight(1.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.End,
                    color = Color(0xFF1565C0)
                )

                // Antecipado (Opcional)
                if (hasPrepayment && detail.prepaymentInstallmentBalance != null) {
                    Text(
                        text = detail.prepaymentInstallmentBalance.toCurrency(currencySymbol),
                        modifier = Modifier.weight(1.5f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.End,
                        color = Color(0xFFFF9800)
                    )
                } else if (hasPrepayment) {
                    Spacer(modifier = Modifier.weight(1.5f))
                }
            }
        }
    }
}

fun Double.toCompactCurrency(): String {
    return if (this >= 1000) {
        String.format("%.1fk", this / 1000)
    } else {
        this.toInt().toString()
    }
}
