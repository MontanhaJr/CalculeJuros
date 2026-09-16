package com.montanhajr.calculejuros.feature.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.montanhajr.calculejuros.core.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProSubscriptionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(), PurchasesUpdatedListener {

    private val _uiState = MutableStateFlow(ProSubscriptionUiState())
    val uiState: StateFlow<ProSubscriptionUiState> = _uiState.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    init {
        startBillingConnection()
    }

    private fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Billing setup failed: ${billingResult.debugMessage}") }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Connection lost
            }
        })
    }

    private fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("pro_annual")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("pro_semiannual")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("pro_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val list = productDetailsResult.productDetailsList
                _uiState.update { 
                    it.copy(
                        products = list.sortedByDescending { p -> p.productId == "pro_annual" },
                        selectedProduct = list.find { p -> p.productId == "pro_annual" } ?: list.firstOrNull(),
                        isLoading = false
                    ) 
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Query failed: ${billingResult.debugMessage}") }
            }
        }
    }

    fun selectProduct(product: ProductDetails) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun launchBillingFlow(activity: Activity) {
        val product = uiState.value.selectedProduct ?: return
        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        viewModelScope.launch {
                            userPreferencesRepository.setProStatus(true)
                            _uiState.update { it.copy(isPurchaseSuccess = true) }
                        }
                    }
                }
            } else {
                viewModelScope.launch {
                    userPreferencesRepository.setProStatus(true)
                    _uiState.update { it.copy(isPurchaseSuccess = true) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingClient.endConnection()
    }
}
