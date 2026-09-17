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
import com.montanhajr.calculejuros.BuildConfig
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.ui.theme.*
import java.util.Locale

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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main Button
                Button(
                    onClick = { (context as? Activity)?.let { viewModel.launchBillingFlow(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    enabled = uiState.selectedPlan != null
                ) {
                    val productName = when (uiState.selectedPlan?.basePlanId) {
                        "anual" -> stringResource(R.string.pro_annual)
                        "mensal" -> stringResource(R.string.pro_monthly)
                        else -> ""
                    }
                    val price = uiState.selectedPlan?.offerDetails?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: ""
                    Text(
                        stringResource(R.string.pro_subscribe_button_format, productName.uppercase(), price),
                        fontFamily = SoraFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
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

            Spacer(modifier = Modifier.height(8.dp))

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
            
            if (BuildConfig.DEBUG && uiState.error != null) {
                Text(
                    uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            uiState.plans.forEach { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = uiState.selectedPlan?.basePlanId == plan.basePlanId,
                    onClick = { viewModel.selectPlan(plan) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (uiState.isLoading && uiState.plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPurple)
                }
            } else if (BuildConfig.DEBUG && !uiState.isLoading && uiState.plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhum plano disponível no momento.\nVerifique sua conexão ou tente novamente mais tarde.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Footer Info
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val highlightColor = BrandPurple
    val borderColor = if (isSelected) highlightColor else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val pricingPhase = plan.offerDetails.pricingPhases.pricingPhaseList.firstOrNull()
    val formattedPrice = pricingPhase?.formattedPrice ?: ""
    
    val title = when (plan.basePlanId) {
        "anual" -> stringResource(R.string.pro_annual)
        "mensal" -> stringResource(R.string.pro_monthly)
        else -> plan.basePlanId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(borderWidth, borderColor),
        color = if (isSelected) highlightColor.copy(alpha = 0.02f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = highlightColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title, 
                        fontFamily = SoraFont, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (plan.basePlanId == "anual") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = highlightColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "economize 60%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MELHOR OFERTA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = highlightColor,
                            fontFamily = SoraFont
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(2.dp))

                // Price line: R$ 4,99/mês
                val micros = pricingPhase?.priceAmountMicros ?: 0L
                val months = if (plan.basePlanId == "anual") 12 else 1
                val currencyCode = pricingPhase?.priceCurrencyCode ?: ""
                
                val monthlyFormatted = if (months > 1) {
                    val monthlyMicros = micros / months
                    when(currencyCode) {
                        "BRL" -> "R$ ${String.format(Locale.getDefault(), "%.2f", monthlyMicros / 1000000.0)}"
                        "USD" -> "$ ${String.format(Locale.getDefault(), "%.2f", monthlyMicros / 1000000.0)}"
                        else -> "${String.format(Locale.getDefault(), "%.2f", monthlyMicros / 1000000.0)} $currencyCode"
                    }
                } else {
                    formattedPrice
                }
                
                Text(
                    text = "$monthlyFormatted/mês",
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Detailed billing info: Cobrado R$ 59,90/ano
                val subtext = if (plan.basePlanId == "anual") {
                    "Cobrado $formattedPrice/ano"
                } else {
                    "Cobrado mensalmente"
                }
                
                Text(
                    text = subtext,
                    fontFamily = DmSansFont,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
