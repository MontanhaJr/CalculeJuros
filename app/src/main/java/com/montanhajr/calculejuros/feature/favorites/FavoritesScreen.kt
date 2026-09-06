package com.montanhajr.calculejuros.feature.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.ui.components.SimulationDetailModal
import com.montanhajr.calculejuros.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToSimulator: (Long?) -> Unit,
    onNavigateToResult: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveAndExitEditMode()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            FavoritesHeader { onNavigateToSimulator(null) }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            QuickAccessCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.title_my_favorites),
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                if (uiState.favoriteSimulations.isNotEmpty()) {
                    TextButton(onClick = viewModel::toggleEditMode) {
                        Text(
                            stringResource(if (uiState.isEditMode) R.string.btn_done else R.string.btn_edit),
                            fontFamily = SoraFont,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (uiState.isEditMode) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            var activeDraggingIndex by remember { mutableStateOf<Int?>(null) }
            var dragOffsetY by remember { mutableFloatStateOf(0f) }
            val density = LocalDensity.current
            val itemSlotHeightPx = with(density) { (80.dp + 12.dp).toPx() }
            val totalItems = uiState.favoriteSimulations.size

            uiState.favoriteSimulations.forEachIndexed { index, item ->
                key(item.id) {
                    val isBeingDragged = activeDraggingIndex == index

                    val targetTranslationY = when {
                        isBeingDragged -> dragOffsetY
                        activeDraggingIndex != null -> {
                            val fromIndex = activeDraggingIndex!!
                            val calculatedTargetIndex = (fromIndex + (dragOffsetY / itemSlotHeightPx).roundToInt())
                                .coerceIn(0, totalItems - 1)
                            when {
                                fromIndex < calculatedTargetIndex && index in (fromIndex + 1)..calculatedTargetIndex -> -itemSlotHeightPx
                                fromIndex > calculatedTargetIndex && index in calculatedTargetIndex until fromIndex -> itemSlotHeightPx
                                else -> 0f
                            }
                        }
                        else -> 0f
                    }

                    val animatedTranslationY by animateFloatAsState(
                        targetValue = if (activeDraggingIndex != null) targetTranslationY else 0f,
                        animationSpec = if (activeDraggingIndex != null) androidx.compose.animation.core.tween(150) else androidx.compose.animation.core.snap(),
                        label = "itemTranslationY"
                    )

                    val effectiveTranslationY = when {
                        activeDraggingIndex == null -> 0f
                        isBeingDragged -> dragOffsetY
                        else -> animatedTranslationY
                    }

                    FavoriteListItem(
                        item = item,
                        index = index,
                        totalItems = totalItems,
                        isEditMode = uiState.isEditMode,
                        isBeingDragged = isBeingDragged,
                        translationY = effectiveTranslationY,
                        onDragStart = {
                            activeDraggingIndex = index
                            dragOffsetY = 0f
                        },
                        onDrag = { dragAmountY ->
                            dragOffsetY += dragAmountY
                        },
                        onDragEnd = {
                            if (activeDraggingIndex != null) {
                                val fromIndex = activeDraggingIndex!!
                                val finalTargetIndex = (fromIndex + (dragOffsetY / itemSlotHeightPx).roundToInt())
                                    .coerceIn(0, totalItems - 1)
                                activeDraggingIndex = null
                                dragOffsetY = 0f
                                if (fromIndex != finalTargetIndex) {
                                    viewModel.onMoveItem(fromIndex, finalTargetIndex)
                                }
                            }
                        },
                        onClick = { viewModel.onSimulationClick(item.fullEntity) },
                        onToggleFavorite = { viewModel.requestUnfavorite(item.fullEntity) },
                        onMoveItem = { fromIndex, toIndex -> viewModel.onMoveItem(fromIndex, toIndex) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TipCard()
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        uiState.selectedSimulation?.let { simulation ->
            SimulationDetailModal(
                simulation = simulation,
                onDismissRequest = viewModel::onDismissModal,
                onReuse = { id -> onNavigateToSimulator(id) },
                onViewResult = { id -> onNavigateToResult(id) }
            )
        }

        uiState.pendingUnfavorite?.let { simulation ->
            AlertDialog(
                onDismissRequest = viewModel::dismissUnfavoriteDialog,
                title = { Text(stringResource(R.string.dialog_unfavorite_title), fontFamily = SoraFont, fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(R.string.dialog_unfavorite_desc), fontFamily = DmSansFont) },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmUnfavorite) {
                        Text(stringResource(R.string.btn_remove), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissUnfavoriteDialog) {
                        Text(stringResource(R.string.btn_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
fun FavoritesHeader(onNavigateToSimulator: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.title_favorites),
                fontFamily = SoraFont,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.subtitle_favorites),
                fontFamily = DmSansFont,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        
        Surface(
            onClick = onNavigateToSimulator,
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.height(56.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.btn_new_simulation).replace(" ", "\n"),
                    color = MaterialTheme.colorScheme.primary,
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(12.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.card_quick_access_title), fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    stringResource(R.string.card_quick_access_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontFamily = DmSansFont
                )
            }
            
            Image(
                painter = painterResource(id = R.drawable.fav_file_icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun FavoriteListItem(
    item: FavoriteItem,
    index: Int,
    totalItems: Int,
    isEditMode: Boolean,
    isBeingDragged: Boolean,
    translationY: Float,
    onDragStart: () -> Unit,
    onDrag: (dragAmountY: Float) -> Unit,
    onDragEnd: () -> Unit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMoveItem: (fromIndex: Int, toIndex: Int) -> Unit
) {
    Surface(
        onClick = { if (!isEditMode) onClick() },
        color = if (isBeingDragged) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            if (isBeingDragged || isEditMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        shadowElevation = if (isBeingDragged) 8.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (isBeingDragged) 10f else 0f)
            .graphicsLayer {
                this.translationY = translationY
                scaleX = if (isBeingDragged) 1.02f else 1f
                scaleY = if (isBeingDragged) 1.02f else 1f
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isEditMode,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .pointerInput(isEditMode, index, totalItems) {
                                if (!isEditMode) return@pointerInput
                                detectDragGestures(
                                    onDragStart = { onDragStart() },
                                    onDragEnd = { onDragEnd() },
                                    onDragCancel = { onDragEnd() },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onDrag(dragAmount.y)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reorder,
                            contentDescription = "Reordenar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        if (index > 0) {
                            IconButton(
                                onClick = { onMoveItem(index, index - 1) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Mover para cima",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        if (index < totalItems - 1) {
                            IconButton(
                                onClick = { onMoveItem(index, index + 1) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Mover para baixo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontFamily = SoraFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.description, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(item.creationDate, fontFamily = DmSansFont, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = (if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.resultType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontFamily = SoraFont,
                            color = if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Star, contentDescription = stringResource(R.string.content_desc_remove_favorite), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.resultLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontFamily = DmSansFont)
                Text(
                    item.resultValue,
                    fontFamily = SoraFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (item.resultType == stringResource(R.string.winner_installments)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }
            
            if (!isEditMode) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun TipCard() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.card_tip_title), fontWeight = FontWeight.Bold, fontFamily = SoraFont, fontSize = 14.sp)
                Text(
                    stringResource(R.string.card_tip_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontFamily = DmSansFont
                )
            }
            
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
        }
    }
}
