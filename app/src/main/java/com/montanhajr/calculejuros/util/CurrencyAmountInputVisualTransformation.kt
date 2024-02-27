package com.montanhajr.calculejuros.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import kotlin.math.max

class CurrencyAmountInputVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {

        val inputText = text.text

        val symbols = DecimalFormat().decimalFormatSymbols
        val thousandsSeparator = symbols.decimalSeparator
        val decimalSeparator = symbols.groupingSeparator

        val thousands = if (inputText.length > 2) inputText.dropLast(2) else "0"
        var decimals = if (inputText.length >= 2) inputText.takeLast(2) else text
        if (decimals.length < 2) {
            decimals = decimals.padStart(2, '0')
        }

        val thousandsReplacementPattern = Regex("\\B(?=(?:\\d{3})+(?!\\d))")
        val formattedIntWithThousandsSeparator = thousands.replace(
            thousandsReplacementPattern, thousandsSeparator.toString()
        )

        val formattedText = AnnotatedString(
            formattedIntWithThousandsSeparator + decimalSeparator + decimals
        )

        val offsetMapping = ThousandSeparatorOffsetMapping(
            unmaskedText = text.toString(),
            maskedText = formattedText.toString(),
            decimalDigits = 2
        )

        return TransformedText(formattedText, offsetMapping)
    }

}

class ThousandSeparatorOffsetMapping(
    private val unmaskedText: String,
    private val maskedText: String,
    private val decimalDigits: Int
) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int =
        when {
            unmaskedText.length <= decimalDigits -> {
                maskedText.length - (unmaskedText.length - offset)
            }

            else -> {
                offset + offsetMaskCount(offset, maskedText)
            }
        }

    override fun transformedToOriginal(offset: Int): Int =
        when {
            unmaskedText.length <= decimalDigits -> {
                max(unmaskedText.length - (maskedText.length - offset), 0)
            }

            else -> {
                offset - maskedText.take(offset).count { !it.isDigit() }
            }
        }

    private fun offsetMaskCount(offset: Int, maskedText: String): Int {
        var maskOffsetCount = 0
        var dataCount = 0
        for (maskChar in maskedText) {
            if (!maskChar.isDigit()) {
                maskOffsetCount++
            } else if (++dataCount > offset) {
                break
            }
        }
        return maskOffsetCount
    }
}
