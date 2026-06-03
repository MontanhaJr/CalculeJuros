package com.montanhajr.calculejuros.feature.simulator

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
        containerColor = Color(0xFFFBFBFB)
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
                trailingIcon = Icons.Default.Edit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SimulatorInputField(
                label = "Desconto à vista (opcional)",
                value = uiState.discountPercentage,
                onValueChange = viewModel::onDiscountChange,
                icon = Icons.Default.LocalOffer,
                suffix = "%",
                helperText = "Ex.: 10 para 10% de desconto"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SimulatorInputField(
                label = "Número de parcelas",
                value = "${uiState.installmentsCount}x",
                onValueChange = { /* TODO: Dropdown */ },
                icon = Icons.Default.CreditCard,
                trailingIcon = Icons.Default.KeyboardArrowDown
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SimulatorInputField(
                label = "Taxa do cartão (ao mês)",
                value = uiState.cardTaxRate,
                onValueChange = viewModel::onCardTaxChange,
                icon = Icons.Default.CreditCard,
                suffix = "% a.m.",
                helperText = "Informe a taxa de juros do seu cartão"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SimulatorSliderField(
                label = "Rentabilidade do investimento (ao ano)",
                value = uiState.investmentAnnualRate,
                onValueChange = viewModel::onInvestmentRateChange,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                suffix = "% a.a.",
                helperText = "Digite a rentabilidade esperada do seu investimento"
            )
            
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
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
                Text("Limpar campos", color = BrandPurple, fontFamily = DmSansFont)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SaveScenarioCard(
                enabled = uiState.isSaveScenarioEnabled,
                onToggle = viewModel::onSaveScenarioToggle
            )
            
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
                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = BrandPurple)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nova Simulação",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "Preencha os dados para descobrir o\nque vale mais a pena para você.",
                fontFamily = DmSansFont,
                color = Color.Gray,
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
                tint = BrandPurple.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HowItWorksSection(expanded: Boolean, onToggle: () -> Unit) {
    Surface(
        onClick = onToggle,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BrandPurple,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Como funciona?", fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                    Text(
                        "Comparamos o rendimento do seu investimento com o custo de parcelar para você decidir a melhor opção.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = DmSansFont,
                        maxLines = if (expanded) Int.MAX_VALUE else 1
                    )
                }
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = BrandPurple
                )
            }
        }
    }
}

@Composable
fun SaveScenarioCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BrandPurple.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(8.dp))
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
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BrandPurple,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}
