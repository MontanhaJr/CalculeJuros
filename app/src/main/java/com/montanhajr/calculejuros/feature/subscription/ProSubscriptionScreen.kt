package com.montanhajr.calculejuros.feature.subscription

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun ProSubscriptionScreen(
    viewModel: ProSubscriptionViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isPurchaseSuccess) {
        if (uiState.isPurchaseSuccess) {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
                
                Surface(
                    color = BrandYellow.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = BrandYellow, modifier = Modifier.size(16.dp))
                        Text("PRO", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandYellow)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(end = 120.dp)) {
                    Text(
                        stringResource(R.string.pro_title),
                        fontFamily = SoraFont,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.pro_subtitle),
                        fontFamily = DmSansFont,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.premium_screen_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 20.dp, y = (-20).dp)
                )
            }

            // Benefits List
            BenefitItem(
                icon = Icons.Default.BarChart,
                title = stringResource(R.string.pro_benefit_reports_title),
                description = stringResource(R.string.pro_benefit_reports_desc)
            )
            BenefitItem(
                icon = Icons.Default.History,
                title = stringResource(R.string.pro_benefit_unlimited_sims_title),
                description = stringResource(R.string.pro_benefit_unlimited_sims_desc)
            )
            BenefitItem(
                icon = Icons.Default.Star,
                title = stringResource(R.string.pro_benefit_unlimited_favs_title),
                description = stringResource(R.string.pro_benefit_unlimited_favs_desc)
            )
            BenefitItem(
                icon = Icons.Default.PlayCircle,
                title = stringResource(R.string.pro_benefit_pdf_title),
                description = stringResource(R.string.pro_benefit_pdf_desc)
            )
            BenefitItem(
                icon = Icons.Default.Block,
                title = stringResource(R.string.pro_benefit_no_ads_title),
                description = stringResource(R.string.pro_benefit_no_ads_desc)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Plan Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.pro_choose_plan), fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            uiState.products.forEach { product ->
                PlanCard(
                    product = product,
                    isSelected = uiState.selectedProduct?.productId == product.productId,
                    onClick = { viewModel.selectProduct(product) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (uiState.isLoading && uiState.products.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPurple)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrandPurple.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.pro_google_play_secure), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text("|", color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Sync, contentDescription = null, tint = BrandPurple.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.pro_cancel_anytime), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Main Button
            Button(
                onClick = { (context as? Activity)?.let { viewModel.launchBillingFlow(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                enabled = uiState.selectedProduct != null
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                val productName = when (uiState.selectedProduct?.productId) {
                    "pro_annual" -> stringResource(R.string.pro_annual)
                    "pro_semiannual" -> stringResource(R.string.pro_semiannual)
                    "pro_monthly" -> stringResource(R.string.pro_monthly)
                    else -> ""
                }
                val price = uiState.selectedProduct?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: ""
                Text(
                    stringResource(R.string.pro_subscribe_button_format, productName, price),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun BenefitItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = BrandPurple,
            shape = CircleShape,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(description, fontFamily = DmSansFont, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun PlanCard(
    product: ProductDetails,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BrandPurple else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val offer = product.subscriptionOfferDetails?.firstOrNull()
    val pricingPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
    val formattedPrice = pricingPhase?.formattedPrice ?: ""
    
    val title = when (product.productId) {
        "pro_annual" -> stringResource(R.string.pro_annual)
        "pro_semiannual" -> stringResource(R.string.pro_semiannual)
        "pro_monthly" -> stringResource(R.string.pro_monthly)
        else -> product.name
    }
    
    val period = when (product.productId) {
        "pro_annual" -> "ano"
        "pro_semiannual" -> "6 meses"
        "pro_monthly" -> "mês"
        else -> ""
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(borderWidth, borderColor),
        color = if (isSelected) BrandPurple.copy(alpha = 0.02f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = BrandPurple)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (product.productId == "pro_annual") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = BrandPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.pro_best_value),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPurple
                            )
                        }
                    }
                }
                
                if (product.productId == "pro_monthly") {
                    Text(stringResource(R.string.pro_ideal_start), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val micros = pricingPhase?.priceAmountMicros ?: 0L
                    val months = if (product.productId == "pro_annual") 12 else 6
                    val monthlyMicros = micros / months
                    val currencyCode = pricingPhase?.priceCurrencyCode ?: ""
                    val monthlyFormatted = when(currencyCode) {
                        "BRL" -> "R$ ${String.format("%.2f", monthlyMicros / 1000000.0)}"
                        "USD" -> "$ ${String.format("%.2f", monthlyMicros / 1000000.0)}"
                        else -> "${String.format("%.2f", monthlyMicros / 1000000.0)} $currencyCode"
                    }
                    Text(stringResource(R.string.pro_price_equivalent_format, monthlyFormatted), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                if (product.productId != "pro_monthly") {
                    val savePercentage = if (product.productId == "pro_annual") 60 else 33
                    Surface(
                        color = BrandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.pro_save_percentage, savePercentage),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    stringResource(R.string.pro_price_format, formattedPrice, period),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandPurple
                )
            }
        }
    }
}
