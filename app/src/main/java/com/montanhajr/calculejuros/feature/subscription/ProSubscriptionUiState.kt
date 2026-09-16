package com.montanhajr.calculejuros.feature.subscription

import com.android.billingclient.api.ProductDetails

data class ProSubscriptionUiState(
    val products: List<ProductDetails> = emptyList(),
    val selectedProduct: ProductDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isPurchaseSuccess: Boolean = false
)
