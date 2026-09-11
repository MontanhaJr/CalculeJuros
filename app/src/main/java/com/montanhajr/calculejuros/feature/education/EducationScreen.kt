package com.montanhajr.calculejuros.feature.education

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.ui.theme.DmSansFont
import com.montanhajr.calculejuros.ui.theme.SoraFont

@Composable
fun EducationScreen(
    onNavigateBack: () -> Unit
) {
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
            
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        stringResource(R.string.education_title),
                        fontFamily = SoraFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(R.string.education_subtitle),
                        fontFamily = DmSansFont,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // What is this app
            EducationCard(
                title = stringResource(R.string.education_what_is_title),
                description = stringResource(R.string.education_what_is_desc),
                icon = Icons.Default.QuestionMark,
                accentColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // How to fill
            Text(
                stringResource(R.string.education_how_to_fill_title),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            EducationStepItem(
                title = stringResource(R.string.education_fill_price_title),
                description = stringResource(R.string.education_fill_price_desc),
                icon = Icons.Default.ShoppingBag
            )
            EducationStepItem(
                title = stringResource(R.string.education_fill_cash_title),
                description = stringResource(R.string.education_fill_cash_desc),
                icon = Icons.Default.LocalOffer
            )
            EducationStepItem(
                title = stringResource(R.string.education_fill_installment_title),
                description = stringResource(R.string.education_fill_installment_desc),
                icon = Icons.Default.Payments
            )
            EducationStepItem(
                title = stringResource(R.string.education_fill_investment_title),
                description = stringResource(R.string.education_fill_investment_desc),
                icon = Icons.AutoMirrored.Filled.TrendingUp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // When to use
            EducationCard(
                title = stringResource(R.string.education_when_to_use_title),
                description = stringResource(R.string.education_when_to_use_desc),
                icon = Icons.Default.EventAvailable,
                accentColor = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Understanding results
            EducationCard(
                title = stringResource(R.string.education_result_title),
                description = stringResource(R.string.education_result_desc),
                icon = Icons.Default.Analytics,
                accentColor = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    stringResource(R.string.btn_got_it),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EducationCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    fontFamily = DmSansFont,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EducationStepItem(
    title: String,
    description: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                title,
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                fontFamily = DmSansFont,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
