package com.montanhajr.calculejuros.feature.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
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
            ProfileHeader()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            UserProfileCard(uiState)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SubscriptionCard(uiState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Resumo da sua conta",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SummaryGrid(uiState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsList()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LogoutButton()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Perfil",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "Gerencie sua conta, plano e preferências",
                fontFamily = DmSansFont,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        IconButton(
            onClick = { /* TODO: Settings */ },
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.Black)
        }
    }
}

@Composable
fun UserProfileCard(uiState: ProfileUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandPurple, Color(0xFF8B66FF))
                )
            )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(2.dp, BrandPurple)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    uiState.userName,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    uiState.userEmail,
                    fontFamily = DmSansFont,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        uiState.memberSince,
                        fontFamily = DmSansFont,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = BrandYellow,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PRO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                TextButton(onClick = { /* TODO */ }) {
                    Text("Ver plano >", color = Color.White, fontSize = 12.sp, fontFamily = SoraFont)
                }
            }
        }
    }
}

@Composable
fun SubscriptionCard(uiState: ProfileUiState) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFF3F0FF),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(12.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Plano Pro", fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 15.sp)
                Text("Aproveite todos os recursos\ne simule sem limites.", color = Color.Gray, fontSize = 12.sp, fontFamily = DmSansFont)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = BrandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Ativo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Próxima cobrança", color = Color.Gray, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(uiState.nextBillingDate, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = SoraFont)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun SummaryGrid(uiState: ProfileUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(Icons.Default.Calculate, BrandPurple, "Simulações\neste mês", uiState.totalSimulations, Modifier.weight(1f))
        SummaryCard(Icons.AutoMirrored.Filled.TrendingUp, BrandGreen, "Você pode ganhar\nno total", uiState.totalGain, Modifier.weight(1.2f))
        SummaryCard(Icons.Default.Star, BrandYellow, "Favoritas", uiState.totalFavorites, Modifier.weight(1f))
        SummaryCard(Icons.Default.History, Color.Blue, "Tempo\neconomizado", uiState.timeSaved, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(icon: ImageVector, iconColor: Color, label: String, value: String, modifier: Modifier) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = modifier.height(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = iconColor.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(6.dp))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = SoraFont, color = if (label.contains("ganhar")) BrandGreen else Color.Black)
                Text(
                    label,
                    fontSize = 9.sp,
                    color = if (label.contains("mês")) BrandPurple else if (label.contains("total")) BrandGreen else if (label.contains("economizado")) Color.Blue else Color.Gray,
                    fontFamily = DmSansFont,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun SettingsList() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            SettingsItem(Icons.Default.Person, "Editar perfil", "Altere suas informações pessoais")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
            SettingsItem(Icons.Default.Notifications, "Notificações", "Gerencie seus alertas e lembretes")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
            SettingsItem(Icons.Default.CreditCard, "Pagamento e faturamento", "Gerencie seu plano e métodos de pagamento")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
            SettingsItem(Icons.Default.Shield, "Privacidade e segurança", "Seus dados e permissões")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
            SettingsItem(Icons.Default.Help, "Central de ajuda", "Tire dúvidas e veja tutoriais")
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.2f))
            SettingsItem(Icons.Default.Info, "Sobre o app", "Versão 1.0.0", isLast = true)
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = BrandPurple.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(icon, contentDescription = null, tint = BrandPurple, modifier = Modifier.padding(10.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
            Text(subtitle, color = Color.Gray, fontSize = 11.sp, fontFamily = DmSansFont)
        }
        
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun LogoutButton() {
    OutlinedButton(
        onClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.1f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sair da conta", fontFamily = SoraFont, fontWeight = FontWeight.SemiBold)
    }
}
