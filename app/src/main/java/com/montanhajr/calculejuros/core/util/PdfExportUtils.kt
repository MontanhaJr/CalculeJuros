package com.montanhajr.calculejuros.core.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import com.montanhajr.calculejuros.R
import com.montanhajr.calculejuros.core.domain.model.RecommendationType
import com.montanhajr.calculejuros.core.domain.model.SimulationResult
import java.io.File
import java.io.FileOutputStream

object PdfExportUtils {

    suspend fun sharePdf(
        context: Context,
        result: SimulationResult,
        currencySymbol: String,
        chartGraphicsLayer: GraphicsLayer,
        scenarioName: String? = null
    ) {
        try {
            val chartBitmap = chartGraphicsLayer.toImageBitmap().asAndroidBitmap()
            val pdfFile = createPdf(context, result, currencySymbol, chartBitmap, scenarioName)
            if (pdfFile != null) {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                shareFile(context, uri, scenarioName ?: "Simulation")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createPdf(
        context: Context,
        result: SimulationResult,
        currencySymbol: String,
        chartBitmap: Bitmap,
        scenarioName: String?
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        
        // Colors
        val primaryColor = 0xFF1565C0.toInt()
        val textColor = 0xFF212121.toInt()
        val secondaryTextColor = 0xFF757575.toInt()
        val cashColor = 0xFF2E7D32.toInt()
        val installmentColor = 0xFF1565C0.toInt()

        // Page 1: Header, Summary, Recommendation and Chart
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = 40f

        val paint = Paint()
        
        // Title
        paint.color = primaryColor
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(context.getString(R.string.title_simulation_result), 40f, yPos, paint)
        yPos += 30f
        
        if (!scenarioName.isNullOrBlank()) {
            paint.color = secondaryTextColor
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(scenarioName, 40f, yPos, paint)
            yPos += 20f
        }

        yPos += 20f
        
        // Recommendation Box
        val recColor = when (result.recommendation) {
            RecommendationType.A_VISTA -> 0xFFE8F5E9.toInt()
            RecommendationType.PARCELADO -> 0xFFE3F2FD.toInt()
            RecommendationType.EMPATE -> 0xFFFFF3E0.toInt()
        }
        val recTextColor = when (result.recommendation) {
            RecommendationType.A_VISTA -> 0xFF2E7D32.toInt()
            RecommendationType.PARCELADO -> 0xFF1565C0.toInt()
            RecommendationType.EMPATE -> 0xFFEF6C00.toInt()
        }
        
        paint.color = recColor
        canvas.drawRoundRect(40f, yPos, 555f, yPos + 80f, 16f, 16f, paint)
        
        paint.color = recTextColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(context.getString(R.string.rec_title), 60f, yPos + 25f, paint)
        
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val recLabel = when (result.recommendation) {
            RecommendationType.A_VISTA -> context.getString(R.string.rec_label_cash)
            RecommendationType.PARCELADO -> context.getString(R.string.rec_label_installment)
            RecommendationType.EMPATE -> context.getString(R.string.rec_label_tie)
        }
        canvas.drawText(recLabel, 60f, yPos + 50f, paint)
        
        if (result.recommendation != RecommendationType.EMPATE) {
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(context.getString(R.string.rec_savings_desc, result.difference.toCurrency(currencySymbol)), 60f, yPos + 70f, paint)
        }
        
        yPos += 110f
        
        // Summary Cards (Values)
        paint.color = textColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(context.getString(R.string.summary_card_cash), 40f, yPos, paint)
        canvas.drawText(context.getString(R.string.summary_card_installment), 300f, yPos, paint)
        
        yPos += 20f
        paint.textSize = 16f
        paint.color = cashColor
        canvas.drawText(result.netGainCash.toCurrency(currencySymbol), 40f, yPos, paint)
        paint.color = installmentColor
        canvas.drawText(result.netGainStandardInstallment.toCurrency(currencySymbol), 300f, yPos, paint)
        
        yPos += 30f

        // Prepayment Benefit Section (if applicable)
        if (result.prepaidInstallmentsCount > 0 && result.prepaymentDiscountValue > 0) {
            val benefit = result.netGainPrepaidInstallment - result.netGainStandardInstallment
            val isPositive = benefit > 0.005
            
            val boxColor = if (isPositive) 0xFFE8F5E9.toInt() else 0xFFFFEBEE.toInt()
            val contentColor = if (isPositive) 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
            
            paint.color = boxColor
            canvas.drawRoundRect(40f, yPos, 555f, yPos + 70f, 12f, 12f, paint)
            
            paint.color = contentColor
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val title = if (isPositive) context.getString(R.string.title_prepayment_positive) else context.getString(R.string.title_prepayment_negative)
            canvas.drawText(title, 60f, yPos + 25f, paint)
            
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val benefitLabel = if (isPositive) context.getString(R.string.label_prepayment_benefit) else context.getString(R.string.label_prepayment_loss)
            canvas.drawText("$benefitLabel ${benefit.toCurrency(currencySymbol)}", 60f, yPos + 45f, paint)
            
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${context.getString(R.string.label_total_prepayment_gain)} ${result.netGainPrepaidInstallment.toCurrency(currencySymbol)}", 60f, yPos + 60f, paint)
            
            yPos += 90f
        } else {
            yPos += 10f
        }
        
        // Chart
        val chartWidth = 515f
        val chartHeight = (chartBitmap.height.toFloat() / chartBitmap.width.toFloat()) * chartWidth
        val softwareBitmap = if (chartBitmap.config == Bitmap.Config.HARDWARE) {
            chartBitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            chartBitmap
        }
        canvas.drawBitmap(softwareBitmap, null, RectF(40f, yPos, 40f + chartWidth, yPos + chartHeight), null)
        
        yPos += chartHeight + 40f
        
        // If there's space, start table on page 1, else new page
        if (yPos < 750f) {
            drawTableHeader(context, canvas, yPos, paint, result.monthlyDetails.any { it.prepaymentInstallmentBalance != null })
            yPos += 25f
        }
        
        pdfDocument.finishPage(page)
        
        // Paginated Table
        var detailIndex = 0
        val hasPrepayment = result.monthlyDetails.any { it.prepaymentInstallmentBalance != null }
        val rowsPerPage = 35

        while (detailIndex < result.monthlyDetails.size) {
            val pageNumber = pdfDocument.pages.size + 1
            val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            yPos = 40f
            
            drawTableHeader(context, canvas, yPos, paint, hasPrepayment)
            yPos += 25f
            
            var rowsOnThisPage = 0
            while (rowsOnThisPage < rowsPerPage && detailIndex < result.monthlyDetails.size) {
                val detail = result.monthlyDetails[detailIndex]
                drawTableRow(canvas, yPos, paint, detail, currencySymbol, hasPrepayment)
                yPos += 20f
                detailIndex++
                rowsOnThisPage++
            }
            
            pdfDocument.finishPage(page)
        }

        return try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, "simulation_${System.currentTimeMillis()}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawTableHeader(context: Context, canvas: Canvas, y: Float, paint: Paint, hasPrepayment: Boolean) {
        paint.color = 0xFFF5F5F5.toInt()
        canvas.drawRect(40f, y - 15f, 555f, y + 10f, paint)
        
        paint.color = 0xFF212121.toInt()
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        canvas.drawText(context.getString(R.string.table_month), 50f, y, paint)
        
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(context.getString(R.string.summary_card_cash), 200f, y, paint)
        canvas.drawText(context.getString(R.string.label_standard_short), 350f, y, paint)
        if (hasPrepayment) {
            canvas.drawText(context.getString(R.string.label_prepayment_short), 500f, y, paint)
        }
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawTableRow(canvas: Canvas, y: Float, paint: Paint, detail: com.montanhajr.calculejuros.core.domain.model.MonthlyDetail, currencySymbol: String, hasPrepayment: Boolean) {
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = 0xFF212121.toInt()
        
        canvas.drawText(detail.month.toString(), 50f, y, paint)
        
        paint.textAlign = Paint.Align.RIGHT
        paint.color = 0xFF2E7D32.toInt()
        canvas.drawText(detail.cashBalance.toCurrency(currencySymbol), 200f, y, paint)
        
        paint.color = 0xFF1565C0.toInt()
        canvas.drawText(detail.installmentBalance.toCurrency(currencySymbol), 350f, y, paint)
        
        if (hasPrepayment) {
            val prepaymentBalance = detail.prepaymentInstallmentBalance
            if (prepaymentBalance != null) {
                paint.color = 0xFFFF9800.toInt()
                canvas.drawText(prepaymentBalance.toCurrency(currencySymbol), 500f, y, paint)
            }
        }
        paint.textAlign = Paint.Align.LEFT
        
        // Divider
        paint.color = 0xFFEEEEEE.toInt()
        canvas.drawLine(40f, y + 5f, 555f, y + 5f, paint)
    }

    private fun shareFile(context: Context, uri: Uri, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TITLE, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}
