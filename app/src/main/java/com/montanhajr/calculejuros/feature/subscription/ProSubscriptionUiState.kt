package com.montanhajr.calculejuros.feature.subscription

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetails.SubscriptionOfferDetails

data class SubscriptionPlan(
    val productDetails: ProductDetails,
    val offerDetails: SubscriptionOfferDetails,
    val basePlanId: String
)

data class ProSubscriptionUiState(
    val plans: List<SubscriptionPlan> = emptyList(),
    val selectedPlan: SubscriptionPlan? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isPurchaseSuccess: Boolean = false
)
