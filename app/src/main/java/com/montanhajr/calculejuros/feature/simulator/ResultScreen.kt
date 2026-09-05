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
    onNavigateBack: () -> Unit,
    onLearnToInvestClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_simulation_result), fontFamily = SoraFont, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
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
                        .padding(16.dp)
                ) {
                    RecommendationHeader(result, onLearnToInvestClick)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    FavoriteOption(
                        isFavorite = uiState.isFavorite,
                        onClick = viewModel::onFavoriteClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ComparisonCards(result)
                    
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
                    
                    DetailedTable(result)

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
fun RecommendationHeader(result: SimulationResult, onLearnToInvestClick: () -> Unit) {
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
        RecommendationType.PARCELADO -> stringResource(R.string.rec_label_installment)
        RecommendationType.EMPATE -> stringResource(R.string.rec_label_tie)
    }

    var showLearnToInvestDialog by remember { mutableStateOf(false) }

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
                    text = stringResource(R.string.rec_savings_desc, result.difference.toCurrency()),
                    fontFamily = DmSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textColor
                )
            }
        }

        if (result.recommendation == RecommendationType.PARCELADO) {
            IconButton(
                onClick = { showLearnToInvestDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.content_desc_learn_to_invest_info),
                    tint = textColor
                )
            }
        }
    }

    if (showLearnToInvestDialog) {
        LearnToInvestDialog(
            onDismiss = { showLearnToInvestDialog = false },
            onConfirm = {
                showLearnToInvestDialog = false
                onLearnToInvestClick()
            }
        )
    }
}

@Composable
fun LearnToInvestDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.dialog_learn_to_invest_title),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_learn_to_invest_message),
                fontFamily = DmSansFont,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.dialog_btn_learn_to_invest), fontFamily = SoraFont, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_btn_close), fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun ComparisonCards(result: SimulationResult) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.summary_card_cash),
            value = result.netGainCash.toCurrency(),
            subValue = stringResource(R.string.summary_sub_interest, result.interestGainedCash.toCurrency()),
            color = Color(0xFF2E7D32)
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.summary_card_installment),
            value = result.netGainInstallment.toCurrency(),
            subValue = stringResource(R.string.summary_sub_installments, result.installmentsCount),
            color = Color(0xFF1565C0)
        )
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
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(shader = DynamicShader.color(Color(0xFF2E7D32))),
                    rememberLineSpec(shader = DynamicShader.color(Color(0xFF1565C0)))
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
fun DetailedTable(result: SimulationResult) {
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
            Text(stringResource(R.string.table_month), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(stringResource(R.string.summary_card_cash), modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
            Text(stringResource(R.string.summary_card_installment), modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
        }
        
        result.monthlyDetails.forEach { detail ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            val isCurrentWinnerParcelado = detail.installmentBalance > detail.cashBalance
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(detail.month.toString(), modifier = Modifier.weight(1f), fontSize = 12.sp)
                Text(
                    text = detail.cashBalance.toCurrency(),
                    modifier = Modifier.weight(2f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = if (!isCurrentWinnerParcelado) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detail.installmentBalance.toCurrency(),
                    modifier = Modifier.weight(2f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = if (isCurrentWinnerParcelado) Color(0xFF1565C0) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

fun Double.toCurrency(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
}

fun Double.toCompactCurrency(): String {
    return if (this >= 1000) {
        String.format("%.1fk", this / 1000)
    } else {
        this.toInt().toString()
    }
}
