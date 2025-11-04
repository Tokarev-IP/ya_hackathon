package com.it.ya_hackathon.domain.report

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData
import java.io.OutputStream

class OrderPdfReportCreator() : OrderPdfReportCreatorInterface {

    override fun generatePdfOrderReportForOne(
        receiptReportData: ReceiptReportData,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): Unit? {
        return runCatching {
            val maxCharAmount = 30
            val pdf = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            var y = margin

            // Padding and Spacing
            val cardPaddingTop = 20f
            val cardPaddingBottom = 16f
            val innerPadding = 14f
            val rowSpacing = 20f
            val newRowSpacing = 24f
            val titlePadding = 20f

            // Types
            val titlePaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val grayPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val textPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.MONOSPACE
                color = Color.BLACK
                isAntiAlias = true
            }
            val translatedTextPaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.MONOSPACE
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val rightAlignPaint = Paint(textPaint).apply { textAlign = Paint.Align.RIGHT }

            val accentPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val subtotalPaint = Paint(accentPaint).apply {
                textSize = 13f
                textAlign = Paint.Align.RIGHT
            }

            val highlightTotalPaint = Paint().apply {
                textSize = 15f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            // Colors
            val bgPaint = Paint().apply { color = Color.WHITE }
            val cardBgPaint = Paint().apply { color = Color.rgb(245, 245, 245) }
            val linePaint = Paint().apply {
                strokeWidth = 1.2f
                color = Color.LTGRAY
            }

            // Order Card
            val hasModifiers = receiptReportData.discount.isNotZero() ||
                    receiptReportData.tip.isNotZero() ||
                    receiptReportData.tax.isNotZero()

            var estimatedHeight = cardPaddingTop + cardPaddingBottom
            estimatedHeight += rowSpacing

            for (order in receiptReportData.orderList) {
                val translatedLength = order.translatedName?.length ?: 0
                estimatedHeight += when {
                    order.name.length < maxCharAmount && translatedLength == 0 -> newRowSpacing
                    order.name.length < maxCharAmount || translatedLength < maxCharAmount -> rowSpacing + newRowSpacing
                    else -> rowSpacing * 2 + newRowSpacing
                }
            }

            if (hasModifiers) {
                estimatedHeight += rowSpacing * 4
            }

            var newPageHeight =
                (estimatedHeight + 2 * margin + cardPaddingTop + cardPaddingBottom + 3 * titlePadding).toInt()
            if (newPageHeight > pageHeight)
                newPageHeight = pageHeight

            fun newPage(): PdfDocument.Page {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, newPageHeight, 1).create()
                return pdf.startPage(pageInfo)
            }

            val page = newPage()
            val canvas = page.canvas
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            // Title
            if (receiptReportData.receiptName.isNotEmpty()) {
                canvas.drawText(receiptReportData.receiptName, margin, y, titlePaint)
                y += titlePadding
            }
            if (!receiptReportData.translatedReceiptName.isNullOrEmpty()) {
                canvas.drawText(receiptReportData.translatedReceiptName, margin, y, grayPaint)
                y += titlePadding
            }
            if (receiptReportData.date.isNotEmpty()) {
                canvas.drawText(receiptReportData.date, margin, y, textPaint)
                y += titlePadding
            }

            val cardLeft = margin
            val cardRight = pageWidth - margin
            val cardTop = y
            val cardBottom = cardTop + estimatedHeight

            canvas.drawRoundRect(cardLeft, cardTop, cardRight, cardBottom, 10f, 10f, cardBgPaint)

            var curY = cardTop + cardPaddingTop
            val finalTotal = receiptReportData.totalSum ?: receiptReportData.subtotalSum

            // Card Title
            canvas.drawText(receiptReportData.consumerName, margin + innerPadding, curY, grayPaint)

            val totalLabel = "${totalText}:"
            val totalValue = finalTotal.roundToTwoDecimalPlaces().toString()
            val totalValueMarginSize = totalValue.length * 3f
            val labelWidth = accentPaint.measureText(totalLabel)
            val valueWidth = subtotalPaint.measureText(totalValue)
            val totalRightX = pageWidth - margin - innerPadding

            canvas.drawText(
                totalLabel,
                totalRightX - valueWidth - totalValueMarginSize - labelWidth,
                curY,
                accentPaint
            )
            canvas.drawText(
                totalValue,
                totalRightX,
                curY,
                highlightTotalPaint
            )

            curY += rowSpacing
            canvas.drawLine(
                cardLeft + innerPadding,
                curY,
                cardRight - innerPadding,
                curY,
                linePaint
            )

            // List of Orders
            var index = 1
            for (order in receiptReportData.orderList) {
                curY += newRowSpacing
                if (order.name.length < maxCharAmount && order.translatedName.isNullOrEmpty()) {
                    val nameLine = "$index. ${order.name}"
                    canvas.drawText(
                        nameLine,
                        margin + innerPadding * 2,
                        curY,
                        textPaint
                    )

                    val rightText = buildString {
                        if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                        append(order.sum.roundToTwoDecimalPlaces().toString())
                    }
                    canvas.drawText(
                        rightText,
                        pageWidth - margin - innerPadding,
                        curY,
                        rightAlignPaint
                    )
                    index++

                } else if (order.name.length < maxCharAmount || (order.translatedName?.length
                        ?: 0) < maxCharAmount
                ) {
                    val nameLine = "$index. ${order.name}"
                    canvas.drawText(
                        nameLine,
                        margin + innerPadding * 2,
                        curY,
                        textPaint
                    )
                    if (order.name.length >= maxCharAmount)
                        curY += rowSpacing

                    val rightText = buildString {
                        if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                        append(order.sum.roundToTwoDecimalPlaces().toString())
                    }
                    canvas.drawText(
                        rightText,
                        pageWidth - margin - innerPadding,
                        curY,
                        rightAlignPaint
                    )

                    if (order.name.length < maxCharAmount)
                        curY += rowSpacing

                    val translatedNameLine = "${order.translatedName}"
                    canvas.drawText(
                        translatedNameLine,
                        margin + innerPadding * 2 + margin / 2,
                        curY,
                        translatedTextPaint
                    )
                    index++
                } else {
                    val nameLine = "$index. ${order.name}"
                    canvas.drawText(
                        nameLine,
                        margin + innerPadding * 2,
                        curY,
                        textPaint
                    )

                    curY += rowSpacing
                    val translatedNameLine = "${order.translatedName}"
                    canvas.drawText(
                        translatedNameLine,
                        margin + innerPadding * 2 + margin / 2,
                        curY,
                        translatedTextPaint
                    )

                    curY += rowSpacing

                    val rightText = buildString {
                        if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                        append(order.sum.roundToTwoDecimalPlaces().toString())
                    }
                    canvas.drawText(
                        rightText,
                        pageWidth - margin - innerPadding,
                        curY,
                        rightAlignPaint
                    )
                    index++
                }
            }

            // Discount/Tip/Tax
            if (hasModifiers) {
                curY += rowSpacing
                canvas.drawLine(
                    cardLeft + innerPadding,
                    curY,
                    cardRight - innerPadding,
                    curY,
                    linePaint
                )
                curY += rowSpacing

                canvas.drawText(
                    "${subtotalText}:  ${receiptReportData.subtotalSum.roundToTwoDecimalPlaces()}",
                    pageWidth - margin - innerPadding,
                    curY,
                    subtotalPaint
                )
                curY += rowSpacing

                val mods = mutableListOf<String>()
                if (receiptReportData.discount.isNotZero()) mods.add("${discountText}: - ${receiptReportData.discount.roundToTwoDecimalPlaces()} %")
                if (receiptReportData.tip.isNotZero()) mods.add("${tipText}: + ${receiptReportData.tip.roundToTwoDecimalPlaces()} %")
                if (receiptReportData.tax.isNotZero()) mods.add("${taxText}: + ${receiptReportData.tax.roundToTwoDecimalPlaces()} %")

                val modsLine = mods.joinToString("    ")
                canvas.drawText(
                    modsLine,
                    pageWidth - margin - innerPadding,
                    curY,
                    subtotalPaint
                )
                curY += rowSpacing

                canvas.drawText(
                    "${totalText}:  ${finalTotal.roundToTwoDecimalPlaces()}",
                    pageWidth - margin - innerPadding,
                    curY,
                    subtotalPaint
                )
            }

            pdf.finishPage(page)
            pdf.writeTo(outputStream)
            pdf.close()
        }.getOrNull()
    }

    override fun generatePdfOrderReportForAll(
        receiptReportDataList: List<ReceiptReportData>,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): Unit? {
        return runCatching {
            if (receiptReportDataList.isEmpty()) return null

            val maxCharAmount = 30

            val pdf = PdfDocument()
            val pageWidth = 595  // A4 Width
            val pageHeight = 842  // A4 Height
            val titlePageHeight = 120  // Title Page Height
            val margin = 40f
            var y = margin

            // Padding and Spacing
            val cardPaddingTop = 20f
            val cardPaddingBottom = 16f
            val innerPadding = 14f
            val rowSpacing = 18f
            val newRowSpacing = 24f
            val afterCardSpacing = 16f
            val titlePadding = 20f

            // Types
            val titlePaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val grayPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val textPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.MONOSPACE
                color = Color.BLACK
                isAntiAlias = true
            }
            val translatedTextPaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.MONOSPACE
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val consumerPaint = Paint().apply {
                textSize = 15f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.rgb(50, 50, 120)
                isAntiAlias = true
            }
            val rightAlignPaint = Paint(textPaint).apply { textAlign = Paint.Align.RIGHT }

            val accentPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val subtotalPaint = Paint(accentPaint).apply {
                textSize = 13f
                textAlign = Paint.Align.RIGHT
            }

            val highlightTotalPaint = Paint().apply {
                textSize = 15f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            // Colors
            val bgPaint = Paint().apply { color = Color.WHITE }
            val cardBgPaint = Paint().apply { color = Color.rgb(245, 245, 245) }
            val linePaint = Paint().apply {
                strokeWidth = 1.2f
                color = Color.LTGRAY
            }

            fun newPage(
                pageNum: Int,
                newPageHeight: Int = pageHeight,
            ): PdfDocument.Page {
                val pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, newPageHeight, pageNum).create()
                return pdf.startPage(pageInfo)
            }

            var pageNum = 1
            var page = newPage(pageNum, titlePageHeight)
            var canvas = page.canvas
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), titlePageHeight.toFloat(), bgPaint)

            fun nextPage(
                newPageHeight: Int = pageHeight,
            ) {
                pdf.finishPage(page)
                pageNum++
                page = newPage(pageNum, newPageHeight)
                canvas = page.canvas
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), newPageHeight.toFloat(), bgPaint)
                y = margin
            }

            // Title
            val first = receiptReportDataList.first()
            if (first.receiptName.isNotEmpty()) {
                canvas.drawText(first.receiptName, margin, y, titlePaint)
                y += titlePadding
            }
            if (!first.translatedReceiptName.isNullOrEmpty()) {
                canvas.drawText(first.translatedReceiptName, margin, y, grayPaint)
                y += titlePadding
            }
            if (first.date.isNotEmpty()) {
                canvas.drawText(first.date, margin, y, textPaint)
                y += titlePadding
            }

            // for every name
            for (reportData in receiptReportDataList) {
                val hasModifiers = reportData.discount.isNotZero() ||
                        reportData.tip.isNotZero() ||
                        reportData.tax.isNotZero()

                var estimatedHeight = cardPaddingTop + cardPaddingBottom

                estimatedHeight += rowSpacing

                for (order in reportData.orderList) {
                    val translatedLength = order.translatedName?.length ?: 0
                    estimatedHeight += when {
                        order.name.length < maxCharAmount && translatedLength == 0 -> newRowSpacing
                        order.name.length < maxCharAmount || translatedLength < maxCharAmount -> rowSpacing + newRowSpacing
                        else -> rowSpacing * 2 + newRowSpacing
                    }
                }

                if (hasModifiers) {
                    estimatedHeight += rowSpacing * 4
                }

                nextPage(estimatedHeight.toInt() + 2 * margin.toInt())

                val cardLeft = margin
                val cardRight = pageWidth - margin
                val cardTop = y
                val cardBottom = cardTop + estimatedHeight

                canvas.drawRoundRect(
                    cardLeft,
                    cardTop,
                    cardRight,
                    cardBottom,
                    10f,
                    10f,
                    cardBgPaint
                )

                var curY = cardTop + cardPaddingTop
                val finalTotal = reportData.totalSum ?: reportData.subtotalSum

                // Order Title
                canvas.drawText(
                    reportData.consumerName,
                    margin + innerPadding,
                    curY,
                    consumerPaint
                )

                val totalLabel = "${totalText}:"
                val totalValue = finalTotal.roundToTwoDecimalPlaces().toString()
                val totalValueMarginSize = totalValue.length * 3f
                val labelWidth = accentPaint.measureText(totalLabel)
                val valueWidth = subtotalPaint.measureText(totalValue)
                val totalRightX = pageWidth - margin - innerPadding

                canvas.drawText(
                    totalLabel,
                    totalRightX - valueWidth - totalValueMarginSize - labelWidth,
                    curY,
                    accentPaint
                )
                canvas.drawText(
                    totalValue,
                    totalRightX,
                    curY,
                    highlightTotalPaint
                )

                curY += rowSpacing

                canvas.drawLine(
                    cardLeft + innerPadding,
                    curY,
                    cardRight - innerPadding,
                    curY,
                    linePaint
                )

                // List of Orders
                var index = 1
                for (order in reportData.orderList) {
                    curY += newRowSpacing
                    if (order.name.length < maxCharAmount && order.translatedName.isNullOrEmpty()) {
                        val nameLine = "$index. ${order.name}"
                        canvas.drawText(
                            nameLine,
                            margin + innerPadding * 2,
                            curY,
                            textPaint
                        )

                        val rightText = buildString {
                            if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                            append(order.sum.roundToTwoDecimalPlaces().toString())
                        }
                        canvas.drawText(
                            rightText,
                            pageWidth - margin - innerPadding,
                            curY,
                            rightAlignPaint
                        )
                        index++

                    } else if (order.name.length < maxCharAmount || (order.translatedName?.length
                            ?: 0) < maxCharAmount
                    ) {
                        val nameLine = "$index. ${order.name}"
                        canvas.drawText(
                            nameLine,
                            margin + innerPadding * 2,
                            curY,
                            textPaint
                        )
                        if (order.name.length >= maxCharAmount)
                            curY += rowSpacing

                        val rightText = buildString {
                            if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                            append(order.sum.roundToTwoDecimalPlaces().toString())
                        }
                        canvas.drawText(
                            rightText,
                            pageWidth - margin - innerPadding,
                            curY,
                            rightAlignPaint
                        )

                        if (order.name.length < maxCharAmount)
                            curY += rowSpacing

                        val translatedNameLine = "${order.translatedName}"
                        canvas.drawText(
                            translatedNameLine,
                            margin + innerPadding * 2 + margin / 2,
                            curY,
                            translatedTextPaint
                        )
                        index++
                    } else {
                        val nameLine = "$index. ${order.name}"
                        canvas.drawText(
                            nameLine,
                            margin + innerPadding * 2,
                            curY,
                            textPaint
                        )

                        curY += rowSpacing
                        val translatedNameLine = "${order.translatedName}"
                        canvas.drawText(
                            translatedNameLine,
                            margin + innerPadding * 2 + margin / 2,
                            curY,
                            translatedTextPaint
                        )

                        curY += rowSpacing

                        val rightText = buildString {
                            if (!order.amountText.isNullOrEmpty()) append("${order.amountText} = ")
                            append(order.sum.roundToTwoDecimalPlaces().toString())
                        }
                        canvas.drawText(
                            rightText,
                            pageWidth - margin - innerPadding,
                            curY,
                            rightAlignPaint
                        )
                        index++
                    }
                }

                if (hasModifiers) {
                    curY += rowSpacing
                    canvas.drawLine(
                        cardLeft + innerPadding,
                        curY,
                        cardRight - innerPadding,
                        curY,
                        linePaint
                    )
                    curY += rowSpacing

                    canvas.drawText(
                        "${subtotalText}:  ${reportData.subtotalSum.roundToTwoDecimalPlaces()}",
                        pageWidth - margin - innerPadding,
                        curY,
                        subtotalPaint
                    )
                    curY += rowSpacing

                    // Discount/Tip/Tax
                    val mods = mutableListOf<String>()
                    if (reportData.discount.isNotZero()) mods.add("${discountText}: - ${reportData.discount.roundToTwoDecimalPlaces()} %")
                    if (reportData.tip.isNotZero()) mods.add("${tipText}: + ${reportData.tip.roundToTwoDecimalPlaces()} %")
                    if (reportData.tax.isNotZero()) mods.add("${taxText}: + ${reportData.tax.roundToTwoDecimalPlaces()} %")

                    val modsLine = mods.joinToString("    ")
                    canvas.drawText(
                        modsLine,
                        pageWidth - margin - innerPadding,
                        curY,
                        subtotalPaint
                    )
                    curY += rowSpacing

                    canvas.drawText(
                        "${totalText}:  ${finalTotal.roundToTwoDecimalPlaces()}",
                        pageWidth - margin - innerPadding,
                        curY,
                        subtotalPaint
                    )
                }

                y = cardBottom + afterCardSpacing
            }

            pdf.finishPage(page)
            pdf.writeTo(outputStream)
            pdf.close()
        }.getOrNull()
    }
}

interface OrderPdfReportCreatorInterface {
    fun generatePdfOrderReportForOne(
        receiptReportData: ReceiptReportData,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): Unit?

    fun generatePdfOrderReportForAll(
        receiptReportDataList: List<ReceiptReportData>,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): Unit?
}