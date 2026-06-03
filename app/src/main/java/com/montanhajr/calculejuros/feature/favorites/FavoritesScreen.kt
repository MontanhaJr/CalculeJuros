package com.montanhajr.calculejuros.feature.favorites

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.montanhajr.calculejuros.ui.theme.*

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToSimulator: () -> Unit
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
            FavoritesHeader(onNavigateToSimulator)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            QuickAccessCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Minhas simulações favoritas", fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TextButton(onClick = { /* TODO */ }) {
                    Text("Editar", fontFamily = SoraFont, color = BrandPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(14.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            uiState.favoriteSimulations.forEach { item ->
                FavoriteListItem(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TipCard()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FavoritesHeader(onNavigateToSimulator: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Favoritos",
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "Suas simulações favoritas para\nacompanhar sempre 💜",
                fontFamily = DmSansFont,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        Surface(
            onClick = onNavigateToSimulator,
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
            modifier = Modifier.height(56.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Nova\nsimulação",
                    color = BrandPurple,
                    fontSize = 10.sp,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickAccessCard() {
    Surface(
        color = Color(0xFFF8F9FE),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BrandPurple.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = BrandYellow, modifier = Modifier.padding(12.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Acesso rápido", fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    "Salve suas simulações favoritas\ne compare sempre que quiser.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont
                )
            }
            
            // Placeholder for folder/star illustration
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = BrandPurple.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Composable
fun FavoriteListItem(item: FavoriteItem) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = when(item.iconType) {
                        "laptop" -> Icons.Default.Laptop
                        "phone" -> Icons.Default.Smartphone
                        "tv" -> Icons.Default.Tv
                        "watch" -> Icons.Default.Watch
                        "ps5" -> Icons.Default.Gamepad
                        else -> Icons.Default.ShoppingBag
                    },
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.description, fontFamily = DmSansFont, color = Color.Gray, fontSize = 11.sp)
                Text(item.creationDate, fontFamily = DmSansFont, color = Color.LightGray, fontSize = 10.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = (if (item.resultType == "Parcelar") BrandGreen else Color.Red).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.resultType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontFamily = SoraFont,
                            color = if (item.resultType == "Parcelar") BrandGreen else Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = BrandPurple, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.resultLabel, color = Color.Gray, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(
                    item.resultValue,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (item.resultType == "Parcelar") BrandGreen else Color.Red
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun TipCard() {
    Surface(
        color = Color(0xFFF8F9FE),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BrandPurple,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Dica", fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    "Compare suas simulações favoritas sempre que houver mudança nos juros ou descontos!",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontFamily = DmSansFont
                )
            }
            
            // Placeholder for chart icon
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(48.dp))
        }
    }
}
