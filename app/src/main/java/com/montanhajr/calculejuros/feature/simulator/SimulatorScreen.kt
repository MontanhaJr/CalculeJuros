package com.montanhajr.calculejuros.feature.simulator

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.core.ui.components.ResultCard
import com.montanhajr.calculejuros.feature.simulator.components.*
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun SimulatorScreen(
    viewModel: SimulatorViewModel,
    onNavigateBack: () -> Unit = {}
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            SimulatorHeader(onNavigateBack)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SimulatorInputField(
                label = "Valor do produto",
                value = uiState.productPrice,
                onValueChange = viewModel::onProductPriceChange,
                icon = Icons.Default.ShoppingBag,
                suffix = "R$",
                trailingIcon = Icons.Default.Edit
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ModeSelector(
                options = listOf("Desconto %", "Valor à vista"),
                selectedOption = if (uiState.useDiscount) "Desconto %" else "Valor à vista",
                onOptionSelected = { viewModel.onUseDiscountToggle(it == "Desconto %") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useDiscount) {
                SimulatorInputField(
                    label = "Porcentagem de Desconto",
                    value = uiState.discountPercentage,
                    onValueChange = viewModel::onDiscountChange,
                    icon = Icons.Default.LocalOffer,
                    suffix = "%",
                    helperText = "Informe o desconto (0 a 100)"
                )
            } else {
                SimulatorInputField(
                    label = "Valor do Preço à Vista",
                    value = uiState.cashPrice,
                    onValueChange = viewModel::onCashPriceChange,
                    icon = Icons.Default.LocalOffer,
                    suffix = "R$",
                    helperText = "Valor final com desconto aplicado"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InstallmentSelector(
                value = uiState.installmentsCount,
                onValueChange = viewModel::onInstallmentsChange,
                onTextChange = viewModel::onInstallmentsTextChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ModeSelector(
                options = listOf("Taxa mensal", "Valor total"),
                selectedOption = if (uiState.useMonthlyRate) "Taxa mensal" else "Valor total",
                onOptionSelected = { viewModel.onUseMonthlyRateToggle(it == "Taxa mensal") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useMonthlyRate) {
                SimulatorInputField(
                    label = "Taxa do cartão (ao mês)",
                    value = uiState.cardTaxRate,
                    onValueChange = viewModel::onCardTaxChange,
                    icon = Icons.Default.CreditCard,
                    suffix = "% a.m.",
                    helperText = "Informe a taxa de juros do seu cartão"
                )
            } else {
                SimulatorInputField(
                    label = "Valor total parcelado",
                    value = uiState.totalInstallmentValue,
                    onValueChange = viewModel::onTotalInstallmentValueChange,
                    icon = Icons.Default.CreditCard,
                    suffix = "R$",
                    helperText = "Soma de todas as parcelas"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            ModeSelector(
                options = listOf("Taxa Anual", "Taxa Mensal"),
                selectedOption = if (uiState.useAnnualProfitability) "Taxa Anual" else "Taxa Mensal",
                onOptionSelected = { viewModel.onUseAnnualProfitabilityToggle(it == "Taxa Anual") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useAnnualProfitability) {
                SimulatorInputField(
                    label = "Rentabilidade do investimento (ao ano)",
                    value = uiState.investmentAnnualRate,
                    onValueChange = viewModel::onInvestmentAnnualRateChange,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    suffix = "% a.a.",
                    helperText = "Digite a rentabilidade (ex: 12,75)"
                )
            } else {
                SimulatorInputField(
                    label = "Rentabilidade do investimento (ao mês)",
                    value = uiState.investmentMonthlyRate,
                    onValueChange = viewModel::onInvestmentMonthlyRateChange,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    suffix = "% a.m.",
                    helperText = "Digite a rentabilidade mensal"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HowItWorksSection(
                expanded = uiState.isHowItWorksExpanded,
                onToggle = viewModel::toggleHowItWorks
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = viewModel::onCalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calcular", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            TextButton(
                onClick = viewModel::onClearFields,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Limpar campos", color = MaterialTheme.colorScheme.primary, fontFamily = DmSansFont)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SaveScenarioCard(
                enabled = uiState.isSaveScenarioEnabled,
                onToggle = viewModel::onSaveScenarioToggle,
                scenarioName = uiState.scenarioName,
                onScenarioNameChange = viewModel::onScenarioNameChange
            )
            
            uiState.simulationResult?.let { result ->
                Spacer(modifier = Modifier.height(24.dp))
                ResultCard(result = result)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SimulatorHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nova Simulação",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Preencha os dados para descobrir o\nque vale mais a pena para você.",
                fontFamily = DmSansFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        // Illustration Placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Build, // Placeholder for calculator 3D
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HowItWorksSection(expanded: Boolean, onToggle: () -> Unit) {
    Surface(
        onClick = onToggle,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Como funciona?", fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                    Text(
                        "Comparamos o rendimento do seu investimento com o custo de parcelar para você decidir a melhor opção.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = DmSansFont,
                        maxLines = if (expanded) Int.MAX_VALUE else 1
                    )
                }
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SaveScenarioCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    scenarioName: String,
    onScenarioNameChange: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Salvar cenário (opcional)",
                        fontWeight = FontWeight.Bold,
                        fontFamily = SoraFont,
                        fontSize = 14.sp
                    )
                    Text(
                        "Dê um nome para esta simulação\ne encontre mais rápido depois.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontFamily = DmSansFont
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    OutlinedTextField(
                        value = scenarioName,
                        onValueChange = onScenarioNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Notebook Gamer", fontSize = 14.sp, fontFamily = DmSansFont) },
                        label = { Text("Nome do cenário", fontSize = 12.sp, fontFamily = DmSansFont) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )
                }
            }
        }
    }
}
