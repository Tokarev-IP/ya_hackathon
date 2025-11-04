package com.it.ya_hackathon.domain.report

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.FolderReportData
import java.io.OutputStream

class FolderPdfReportCreator() : FolderPdfReportCreatorInterface {

    override fun generateShortReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        totalText: String,
    ) {
        runCatching {
            if (folderReportDataList.isEmpty()) return

            val pdf = PdfDocument()
            val pageWidth = 595 // A4 Width
            val pageHeight = 842 // A4 Height
            val margin = 40f
            var y = margin

            val rowSpacing = 18f
            val titlePadding = 20f
            val translatedTitlePadding = 14f

            val titlePaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val translatedTitlePaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val titleTotalPaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val consumerPaint = Paint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.rgb(50, 50, 120)
                isAntiAlias = true
            }

            val accentPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val subtotalPaint = Paint(accentPaint).apply {
                textSize = 10f
                textAlign = Paint.Align.RIGHT
            }
            val highlightFolderTotalPaint = Paint().apply {
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            val bgPaint = Paint().apply { color = Color.WHITE }
            val linePaint = Paint().apply {
                strokeWidth = 1.2f
                color = Color.LTGRAY
            }

            fun newPage(pageNum: Int, newPageHeight: Int): PdfDocument.Page {
                val pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, newPageHeight, pageNum).create()
                return pdf.startPage(pageInfo)
            }

            val receiptNameList = folderReportDataList
                .flatMap { it.receiptList }
                .distinctBy { it.receiptName }

            var pageNum = 1
            var page = newPage(pageNum, pageHeight)
            var canvas = page.canvas
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            fun nextPage(newPageHeight: Int = pageHeight) {
                pdf.finishPage(page)
                pageNum++
                page = newPage(pageNum, newPageHeight)
                canvas = page.canvas
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), newPageHeight.toFloat(), bgPaint)
                y = margin
            }

            // Receipt Names First Page
            receiptNameList.forEach { receipt ->
                canvas.drawText(receipt.receiptName, margin, y, titlePaint)
                y += translatedTitlePadding
                canvas.drawText(
                    "${receipt.translatedReceiptName}   ${receipt.date}",
                    margin,
                    y,
                    translatedTitlePaint
                )
                y += titlePadding
            }

            canvas.drawLine(
                margin,
                y,
                pageWidth - margin,
                y,
                linePaint
            )
            y += rowSpacing

            for (folder in folderReportDataList) {
                // Counting a Page's Height
                var estimatedHeight = 0f
                estimatedHeight += titlePadding // Consumer Name Title
                estimatedHeight += 2 * rowSpacing // Line

                if (y + estimatedHeight > pageHeight - margin)
                    nextPage(pageHeight)

                // Current Y - position of the cursor
                y += titlePadding

                // Consumer Name Title
                canvas.drawText(
                    folder.consumerName,
                    margin,
                    y,
                    consumerPaint
                )
                val folderTotalLabel = "${totalText}:"
                val folderTotalValue = folder.totalSum.roundToTwoDecimalPlaces().toString()
                val folderTotalValueMarginSize = folderTotalValue.length * 6f + 14f
                val folderLabelWidth = accentPaint.measureText(folderTotalLabel)
                val folderValueWidth = subtotalPaint.measureText(folderTotalValue)
                val folderTotalRightX = pageWidth - margin

                canvas.drawText(
                    folderTotalLabel,
                    folderTotalRightX - folderValueWidth - folderTotalValueMarginSize - folderLabelWidth,
                    y,
                    titleTotalPaint
                )
                canvas.drawText(
                    folderTotalValue,
                    folderTotalRightX,
                    y,
                    highlightFolderTotalPaint
                )

                // Line
                y += rowSpacing
                canvas.drawLine(
                    margin,
                    y,
                    pageWidth - margin,
                    y,
                    linePaint
                )
                y += rowSpacing
            }
            pdf.finishPage(page)
            pdf.writeTo(outputStream)
            pdf.close()
        }.getOrNull()
    }

    override fun generateBasicReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        totalText: String,
    ) {
        runCatching {
            if (folderReportDataList.isEmpty()) return

            val pdf = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            var y = margin

            val cardPaddingTop = 24f
            val cardPaddingBottom = 16f
            val innerPadding = 14f
            val rowSpacing = 18f
            val afterCardSpacing = 10f
            val titlePadding = 20f
            val translatedTitlePadding = 14f

            val titlePaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val translatedTitlePaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val titleTotalPaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val consumerPaint = Paint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.rgb(50, 50, 120)
                isAntiAlias = true
            }

            val accentPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val subtotalPaint = Paint(accentPaint).apply {
                textSize = 10f
                textAlign = Paint.Align.RIGHT
            }

            val highlightTotalPaint = Paint().apply {
                textSize = 16f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            val highlightFolderTotalPaint = Paint().apply {
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            val bgPaint = Paint().apply { color = Color.WHITE }
            val cardBgPaint = Paint().apply { color = Color.rgb(245, 245, 245) }
            val linePaint = Paint().apply {
                strokeWidth = 1.2f
                color = Color.LTGRAY
            }

            fun newPage(pageNum: Int, newPageHeight: Int): PdfDocument.Page {
                val pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, newPageHeight, pageNum).create()
                return pdf.startPage(pageInfo)
            }

            val receiptNameList = folderReportDataList
                .flatMap { it.receiptList }
                .distinctBy { it.receiptName }

            var titlePageHeight = 2 * margin
            for (receipt in receiptNameList) {
                titlePageHeight += titlePadding
                if (receipt.translatedReceiptName != null)
                    titlePageHeight += translatedTitlePadding
            }


            var pageNum = 1
            var page = newPage(pageNum, titlePageHeight.toInt())
            var canvas = page.canvas
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), titlePageHeight, bgPaint)

            fun nextPage(newPageHeight: Int = pageHeight) {
                pdf.finishPage(page)
                pageNum++
                page = newPage(pageNum, newPageHeight)
                canvas = page.canvas
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), newPageHeight.toFloat(), bgPaint)
                y = margin
            }

            // Receipt Names First Page
            receiptNameList.forEach { receipt ->
                canvas.drawText(receipt.receiptName, margin, y, titlePaint)
                y += translatedTitlePadding
                canvas.drawText(
                    "${receipt.translatedReceiptName}   ${receipt.date}",
                    margin,
                    y,
                    translatedTitlePaint
                )
                y += titlePadding
            }

            for (folder in folderReportDataList) {
                // Counting a Page's Height
                var pageEstimatedHeight = 0f
                pageEstimatedHeight += margin // Margin of a page
                pageEstimatedHeight += titlePadding // Consumer Name Title
                pageEstimatedHeight += 2 * rowSpacing // Line
                for (reportData in folder.receiptList) { // for every Receipt
                    pageEstimatedHeight += cardPaddingTop + cardPaddingBottom // Card Paddings
                    pageEstimatedHeight += rowSpacing // Receipt Name
                    if (reportData.translatedReceiptName != null)
                        pageEstimatedHeight += rowSpacing
                    pageEstimatedHeight += afterCardSpacing
                }
                pageEstimatedHeight += margin

                nextPage(pageEstimatedHeight.toInt())

                // Current Y - position of the cursor
                y += titlePadding

                // Consumer Name Title
                canvas.drawText(
                    folder.consumerName,
                    margin,
                    y,
                    consumerPaint
                )
                val folderTotalLabel = "${totalText}:"
                val folderTotalValue = folder.totalSum.roundToTwoDecimalPlaces().toString()
                val folderTotalValueMarginSize = folderTotalValue.length * 6f + 14f
                val folderLabelWidth = accentPaint.measureText(folderTotalLabel)
                val folderValueWidth = subtotalPaint.measureText(folderTotalValue)
                val folderTotalRightX = pageWidth - margin

                canvas.drawText(
                    folderTotalLabel,
                    folderTotalRightX - folderValueWidth - folderTotalValueMarginSize - folderLabelWidth,
                    y,
                    titleTotalPaint
                )
                canvas.drawText(
                    folderTotalValue,
                    folderTotalRightX,
                    y,
                    highlightFolderTotalPaint
                )

                // Line
                y += rowSpacing
                canvas.drawLine(
                    margin,
                    y,
                    pageWidth - margin,
                    y,
                    linePaint
                )
                y += rowSpacing

                for (reportData in folder.receiptList) {
                    // Count the Height of a Card
                    // EstimatedHeight - is the height of the current Card
                    var estimatedHeight = cardPaddingTop + cardPaddingBottom
                    if (reportData.translatedReceiptName != null)
                        estimatedHeight += rowSpacing

                    // Card's Sizes
                    val cardLeft = margin
                    val cardRight = pageWidth - margin
                    val cardTop = y // Top of the Card after margin
                    val cardBottom = cardTop + estimatedHeight

                    // Current Y - position of the cursor for the Card
                    var curY = cardTop + cardPaddingTop

                    canvas.drawRoundRect(
                        cardLeft,
                        cardTop,
                        cardRight,
                        cardBottom,
                        10f,
                        10f,
                        cardBgPaint
                    )

                    val finalTotal = reportData.totalSum ?: reportData.subtotalSum

                    canvas.drawText(
                        reportData.receiptName,
                        margin + innerPadding,
                        curY,
                        titlePaint
                    )

                    val totalLabel = "${totalText}:"
                    val totalValue = finalTotal.roundToTwoDecimalPlaces().toString()
                    val totalValueMarginSize = totalValue.length * 4f
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

                    if (reportData.translatedReceiptName != null) {
                        curY += rowSpacing
                        canvas.drawText(
                            reportData.translatedReceiptName,
                            margin + innerPadding,
                            curY,
                            translatedTitlePaint
                        )
                    }
                    y = curY + cardPaddingBottom + afterCardSpacing
                }
            }
            pdf.finishPage(page)
            pdf.writeTo(outputStream)
            pdf.close()
        }.getOrNull()
    }

    override fun generateLongReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String,
    ) {
        runCatching {
            if (folderReportDataList.isEmpty()) return

            val pdf = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            var y = margin

            val cardPaddingTop = 24f
            val cardPaddingBottom = 16f
            val innerPadding = 14f
            val rowSpacing = 18f
            val newRowSpacing = 24f
            val afterCardSpacing = 16f
            val titlePadding = 20f
            val translatedTitlePadding = 14f
            val maxCharAmount = 30

            val titlePaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
                isAntiAlias = true
            }
            val translatedTitlePaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.DKGRAY
                isAntiAlias = true
            }
            val titleTotalPaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                color = Color.BLACK
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
                textSize = 16f
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
                textSize = 10f
                textAlign = Paint.Align.RIGHT
            }

            val highlightTotalPaint = Paint().apply {
                textSize = 16f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            val highlightFolderTotalPaint = Paint().apply {
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                color = Color.rgb(0, 120, 80)
                textAlign = Paint.Align.RIGHT
            }

            val bgPaint = Paint().apply { color = Color.WHITE }
            val cardBgPaint = Paint().apply { color = Color.rgb(245, 245, 245) }
            val linePaint = Paint().apply {
                strokeWidth = 1.2f
                color = Color.LTGRAY
            }

            fun newPage(pageNum: Int, newPageHeight: Int): PdfDocument.Page {
                val pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, newPageHeight, pageNum).create()
                return pdf.startPage(pageInfo)
            }

            val receiptNameList = folderReportDataList
                .flatMap { it.receiptList }
                .distinctBy { it.receiptName }

            var titlePageHeight = 2 * margin
            for (receipt in receiptNameList) {
                titlePageHeight += titlePadding
                if (receipt.translatedReceiptName != null)
                    titlePageHeight += translatedTitlePadding
            }


            var pageNum = 1
            var page = newPage(pageNum, titlePageHeight.toInt())
            var canvas = page.canvas
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), titlePageHeight, bgPaint)

            fun nextPage(newPageHeight: Int = pageHeight) {
                pdf.finishPage(page)
                pageNum++
                page = newPage(pageNum, newPageHeight)
                canvas = page.canvas
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), newPageHeight.toFloat(), bgPaint)
                y = margin
            }

            // Receipt Names First Page
            receiptNameList.forEach { receipt ->
                canvas.drawText(receipt.receiptName, margin, y, titlePaint)
                y += translatedTitlePadding
                canvas.drawText(
                    "${receipt.translatedReceiptName}   ${receipt.date}",
                    margin,
                    y,
                    translatedTitlePaint
                )
                y += titlePadding
            }

            for (folder in folderReportDataList) {
                // Counting a Page's Height
                var pageEstimatedHeight = 0f
                pageEstimatedHeight += margin // Margin of a page
                pageEstimatedHeight += titlePadding // Consumer Name Title
                pageEstimatedHeight += 2 * rowSpacing // Line
                for (reportData in folder.receiptList) { // for every Receipt
                    pageEstimatedHeight += cardPaddingTop + cardPaddingBottom // Card Paddings
                    pageEstimatedHeight += rowSpacing // Receipt Name
                    if (reportData.translatedReceiptName != null)
                        pageEstimatedHeight += rowSpacing

                    for (order in reportData.orderList) {
                        val translatedLength = order.translatedName?.length ?: 0
                        pageEstimatedHeight += when {
                            order.name.length < maxCharAmount && translatedLength == 0 -> newRowSpacing
                            order.name.length < maxCharAmount || translatedLength < maxCharAmount -> rowSpacing + newRowSpacing
                            else -> rowSpacing * 2 + newRowSpacing
                        }
                    }

                    val hasModifiers = reportData.discount.isNotZero() ||
                            reportData.tip.isNotZero() ||
                            reportData.tax.isNotZero()
                    if (hasModifiers) {
                        pageEstimatedHeight += rowSpacing * 4
                    }
                    pageEstimatedHeight += afterCardSpacing
                }
                pageEstimatedHeight += margin

                nextPage(pageEstimatedHeight.toInt())

                // Current Y - position of the cursor
                y += titlePadding

                // Consumer Name Title
                canvas.drawText(
                    folder.consumerName,
                    margin,
                    y,
                    consumerPaint
                )
                val folderTotalLabel = "${totalText}:"
                val folderTotalValue = folder.totalSum.roundToTwoDecimalPlaces().toString()
                val folderTotalValueMarginSize = folderTotalValue.length * 6f + 14f
                val folderLabelWidth = accentPaint.measureText(folderTotalLabel)
                val folderValueWidth = subtotalPaint.measureText(folderTotalValue)
                val folderTotalRightX = pageWidth - margin

                canvas.drawText(
                    folderTotalLabel,
                    folderTotalRightX - folderValueWidth - folderTotalValueMarginSize - folderLabelWidth,
                    y,
                    titleTotalPaint
                )
                canvas.drawText(
                    folderTotalValue,
                    folderTotalRightX,
                    y,
                    highlightFolderTotalPaint
                )

                // Line
                y += rowSpacing
                canvas.drawLine(
                    margin,
                    y,
                    pageWidth - margin,
                    y,
                    linePaint
                )
                y += rowSpacing

                for (reportData in folder.receiptList) {
                    // Count the Height of a Card
                    // EstimatedHeight - is the height of the current Card
                    var estimatedHeight = cardPaddingTop + cardPaddingBottom
                    estimatedHeight += rowSpacing
                    if (reportData.translatedReceiptName != null)
                        estimatedHeight += rowSpacing
                    for (order in reportData.orderList) {
                        val translatedLength = order.translatedName?.length ?: 0
                        estimatedHeight += when {
                            order.name.length < maxCharAmount && translatedLength == 0 -> newRowSpacing
                            order.name.length < maxCharAmount || translatedLength < maxCharAmount -> rowSpacing + newRowSpacing
                            else -> rowSpacing * 2 + newRowSpacing
                        }
                    }
                    val hasModifiers = reportData.discount.isNotZero() ||
                            reportData.tip.isNotZero() ||
                            reportData.tax.isNotZero()
                    if (hasModifiers) {
                        estimatedHeight += rowSpacing * 4
                    }

                    // Card's Sizes
                    val cardLeft = margin
                    val cardRight = pageWidth - margin
                    val cardTop = y // Top of the Card after margin
                    val cardBottom = cardTop + estimatedHeight

                    // Current Y - position of the cursor for the Card
                    var curY = cardTop + cardPaddingTop

                    canvas.drawRoundRect(
                        cardLeft,
                        cardTop,
                        cardRight,
                        cardBottom,
                        10f,
                        10f,
                        cardBgPaint
                    )

                    val finalTotal = reportData.totalSum ?: reportData.subtotalSum

                    canvas.drawText(
                        reportData.receiptName,
                        margin + innerPadding,
                        curY,
                        titlePaint
                    )

                    val totalLabel = "${totalText}:"
                    val totalValue = finalTotal.roundToTwoDecimalPlaces().toString()
                    val totalValueMarginSize = totalValue.length * 4f
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

                    if (reportData.translatedReceiptName != null) {
                        curY += rowSpacing
                        canvas.drawText(
                            reportData.translatedReceiptName,
                            margin + innerPadding,
                            curY,
                            translatedTitlePaint
                        )
                    }

                    curY += rowSpacing
                    canvas.drawLine(
                        cardLeft + innerPadding,
                        curY,
                        cardRight - innerPadding,
                        curY,
                        linePaint
                    )

                    var index = 1
                    for (order in reportData.orderList) {
                        curY += newRowSpacing
                        if (order.name.length < maxCharAmount && order.translatedName.isNullOrEmpty()) {
                            val nameLine = "$index. ${order.name}"
                            canvas.drawText(nameLine, margin + innerPadding * 2, curY, textPaint)

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
                            canvas.drawText(nameLine, margin + innerPadding * 2, curY, textPaint)
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
                            canvas.drawText(nameLine, margin + innerPadding * 2, curY, textPaint)

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

                    y = curY + cardPaddingBottom + afterCardSpacing
                }
            }
            pdf.finishPage(page)
            pdf.writeTo(outputStream)
            pdf.close()
        }.getOrNull()
    }
}

interface FolderPdfReportCreatorInterface {

    fun generateShortReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        totalText: String,
    )

    fun generateBasicReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        totalText: String,
    )

    fun generateLongReportPdf(
        folderReportDataList: List<FolderReportData>,
        outputStream: OutputStream,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String,
    )
}