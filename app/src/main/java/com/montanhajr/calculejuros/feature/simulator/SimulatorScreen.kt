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
import androidx.compose.ui.res.stringResource
import com.montanhajr.calculejuros.R
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
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            SimulatorHeader(onNavigateBack)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val currencyLabel = stringResource(R.string.label_currency)
            SimulatorInputField(
                label = stringResource(R.string.label_product_price),
                value = uiState.productPrice,
                onValueChange = viewModel::onProductPriceChange,
                icon = Icons.Default.ShoppingBag,
                suffix = currencyLabel,
                trailingIcon = Icons.Default.Edit
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            val discountOptions = listOf(
                stringResource(R.string.label_discount_percentage_mode),
                stringResource(R.string.label_cash_value_mode)
            )
            ModeSelector(
                options = discountOptions,
                selectedOption = if (uiState.useDiscount) discountOptions[0] else discountOptions[1],
                onOptionSelected = { viewModel.onUseDiscountToggle(it == discountOptions[0]) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useDiscount) {
                SimulatorInputField(
                    label = stringResource(R.string.label_discount_percentage),
                    value = uiState.discountPercentage,
                    onValueChange = viewModel::onDiscountChange,
                    icon = Icons.Default.LocalOffer,
                    suffix = "%",
                    helperText = stringResource(R.string.helper_discount_percentage)
                )
            } else {
                SimulatorInputField(
                    label = stringResource(R.string.label_cash_price),
                    value = uiState.cashPrice,
                    onValueChange = viewModel::onCashPriceChange,
                    icon = Icons.Default.LocalOffer,
                    suffix = currencyLabel,
                    helperText = stringResource(R.string.helper_cash_price)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InstallmentSelector(
                value = uiState.installmentsCount,
                onValueChange = viewModel::onInstallmentsChange,
                onTextChange = viewModel::onInstallmentsTextChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            val cardRateOptions = listOf(
                stringResource(R.string.label_monthly_rate_mode),
                stringResource(R.string.label_total_value_mode)
            )
            ModeSelector(
                options = cardRateOptions,
                selectedOption = if (uiState.useMonthlyRate) cardRateOptions[0] else cardRateOptions[1],
                onOptionSelected = { viewModel.onUseMonthlyRateToggle(it == cardRateOptions[0]) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useMonthlyRate) {
                SimulatorInputField(
                    label = stringResource(R.string.label_card_tax_rate),
                    value = uiState.cardTaxRate,
                    onValueChange = viewModel::onCardTaxChange,
                    icon = Icons.Default.CreditCard,
                    suffix = "% a.m.",
                    helperText = stringResource(R.string.helper_card_tax_rate)
                )
            } else {
                SimulatorInputField(
                    label = stringResource(R.string.label_total_installment_value),
                    value = uiState.totalInstallmentValue,
                    onValueChange = viewModel::onTotalInstallmentValueChange,
                    icon = Icons.Default.CreditCard,
                    suffix = currencyLabel,
                    helperText = stringResource(R.string.helper_total_installment_value)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            val profitabilityOptions = listOf(
                stringResource(R.string.label_annual_rate_mode),
                stringResource(R.string.label_monthly_rate_mode_profitability)
            )
            ModeSelector(
                options = profitabilityOptions,
                selectedOption = if (uiState.useAnnualProfitability) profitabilityOptions[0] else profitabilityOptions[1],
                onOptionSelected = { viewModel.onUseAnnualProfitabilityToggle(it == profitabilityOptions[0]) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.useAnnualProfitability) {
                SimulatorInputField(
                    label = stringResource(R.string.label_investment_annual_rate),
                    value = uiState.investmentAnnualRate,
                    onValueChange = viewModel::onInvestmentAnnualRateChange,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    suffix = "% a.a.",
                    helperText = stringResource(R.string.helper_investment_annual_rate)
                )
            } else {
                SimulatorInputField(
                    label = stringResource(R.string.label_investment_monthly_rate),
                    value = uiState.investmentMonthlyRate,
                    onValueChange = viewModel::onInvestmentMonthlyRateChange,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    suffix = "% a.m.",
                    helperText = stringResource(R.string.helper_investment_monthly_rate)
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
                Text(stringResource(R.string.btn_calculate), fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            TextButton(
                onClick = viewModel::onClearFields,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_clear_fields), color = MaterialTheme.colorScheme.primary, fontFamily = DmSansFont)
            }
            
            uiState.simulationResult?.let { result ->
                Spacer(modifier = Modifier.height(24.dp))
                
                FavoriteOption(
                    isFavorite = uiState.isSavedAsFavorite,
                    onClick = viewModel::onFavoriteClick
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                ResultCard(result = result)
            }

            Spacer(modifier = Modifier.height(32.dp))
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_cancel), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.title_new_simulation),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.subtitle_new_simulation),
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
fun FavoriteOption(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = null,
            tint = if (isFavorite) Color(0xFFFFB800) else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isFavorite) stringResource(R.string.status_saved_favorite) else stringResource(R.string.status_add_favorite),
            fontFamily = SoraFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (isFavorite) Color(0xFFFFB800) else MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SaveFavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.dialog_save_favorite_title),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.dialog_save_favorite_desc),
                    fontFamily = DmSansFont,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(stringResource(R.string.dialog_save_favorite_hint), fontFamily = DmSansFont) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.btn_save), fontFamily = SoraFont, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel), fontFamily = SoraFont)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
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
                    Text(stringResource(R.string.section_how_it_works_title), fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                    Text(
                        stringResource(R.string.section_how_it_works_desc),
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

