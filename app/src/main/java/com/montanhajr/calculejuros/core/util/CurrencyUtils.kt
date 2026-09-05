package com.montanhajr.calculejuros.core.util

import java.text.NumberFormat
import java.util.Locale

fun Double.toCurrency(currencySymbol: String = "R$"): String {
    val locale = when (currencySymbol) {
        "R$" -> Locale.forLanguageTag("pt-BR")
        "$" -> Locale.US
        "€" -> Locale.GERMANY
        "£" -> Locale.UK
        else -> Locale.forLanguageTag("pt-BR")
    }
    return try {
        NumberFormat.getCurrencyInstance(locale).format(this)
    } catch (_: Exception) {
        "$currencySymbol %.2f".format(this)
    }
}
