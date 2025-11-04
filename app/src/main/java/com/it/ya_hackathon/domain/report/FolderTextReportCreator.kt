package com.it.ya_hackathon.domain.report

import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.FolderReportData

class FolderTextReportCreator() : FolderTextReportCreatorInterface {

    private companion object {
        const val EMPTY_STRING = ""
        const val START_STRING = "•"
        const val LONG_DIVIDER_STRING = "---------------"
    }

    override fun createBasicTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
    ): String? {
        return runCatching {
            if (folderReportDataList.isEmpty()) return null

            buildString {
                folderReportDataList
                    .flatMap { it.receiptList }
                    .distinctBy { it.receiptName }
                    .forEach { receipt ->
                        append(" • ${receipt.receiptName}")
                        if (receipt.translatedReceiptName?.isNotBlank() == true)
                            append(" (${receipt.translatedReceiptName})")
                        appendLine(" ${receipt.date}")
                    }
                appendLine()
                folderReportDataList.forEachIndexed { index, folder ->
                    val consumerName = folder.consumerName

                    appendLine("${index + 1}. $consumerName $START_STRING $totalText: ${folder.totalSum.roundToTwoDecimalPlaces()}")

                    folder.receiptList.forEach { receipt ->
                        append("  $START_STRING ${receipt.receiptName}")
                        if (receipt.translatedReceiptName?.isNotBlank() == true)
                            append(" (${receipt.translatedReceiptName})")
                        appendLine(" ${receipt.date}")
                        appendLine("     ${totalText}: ${(receipt.totalSum ?: receipt.subtotalSum).roundToTwoDecimalPlaces()}")
                    }
                    appendLine()
                }
            }.trim()
        }.getOrNull()
    }

    override fun createShortTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
    ): String? {
        return runCatching {
            if (folderReportDataList.isEmpty()) return null

            buildString {
                folderReportDataList
                    .flatMap { it.receiptList }
                    .distinctBy { it.receiptName }
                    .forEach { receipt ->
                        append(" • ${receipt.receiptName}")
                        if (receipt.translatedReceiptName?.isNotBlank() == true)
                            append(" (${receipt.translatedReceiptName})")
                        appendLine(" ${receipt.date}")
                    }
                appendLine()
                folderReportDataList.forEachIndexed { index, folder ->
                    appendLine(" ${index + 1}. ${folder.consumerName} $START_STRING $totalText: ${folder.totalSum.roundToTwoDecimalPlaces()}")
                }
            }.trim()
        }.getOrNull()
    }

    override fun createLongTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
        subTotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
    ): String? {
        return runCatching {
            if (folderReportDataList.isEmpty()) return EMPTY_STRING

            buildString {
                folderReportDataList
                    .flatMap { it.receiptList }
                    .distinctBy { it.receiptName }
                    .forEach { receipt ->
                        append(" • ${receipt.receiptName}")
                        if (receipt.translatedReceiptName?.isNotBlank() == true)
                            append(" (${receipt.translatedReceiptName})")
                        appendLine(" ${receipt.date}")
                    }

                appendLine()
                appendLine(LONG_DIVIDER_STRING)
                appendLine()

                folderReportDataList.forEachIndexed { index, folder ->
                    val consumerName = folder.consumerName

                    appendLine("${index + 1}. $consumerName $START_STRING $totalText: ${folder.totalSum.roundToTwoDecimalPlaces()}")
                    appendLine()

                    folder.receiptList.forEach { receipt ->
                        append("  $START_STRING ${receipt.receiptName}")
                        if (receipt.translatedReceiptName?.isNotBlank() == true)
                            append(" (${receipt.translatedReceiptName})")
                        appendLine(" ${receipt.date}")
                        appendLine("   ${totalText}: ${(receipt.totalSum ?: receipt.subtotalSum).roundToTwoDecimalPlaces()}")
                        receipt.orderList.forEachIndexed { orderIndex, order ->
                            append("     ${orderIndex + 1}. ${order.name}")
                            if (order.translatedName?.isNotBlank() == true)
                                append(" (${order.translatedName})")
                            appendLine(" ${order.amountText} = ${order.sum.roundToTwoDecimalPlaces()}")
                        }
                        if (receipt.discount.isNotZero() || receipt.tip.isNotZero() || receipt.tax.isNotZero()) {
                            appendLine("    ${subTotalText}: ${receipt.subtotalSum.roundToTwoDecimalPlaces()}")
                            var discountTipTax = ""
                            if (receipt.discount.isNotZero()) discountTipTax +=(" $discountText: ${receipt.discount} % ")
                            if (receipt.tip.isNotZero()) discountTipTax +=(" $tipText: ${receipt.tip} %")
                            if (receipt.tax.isNotZero()) discountTipTax +=(" $taxText: ${receipt.tax} %")
                            if (discountTipTax.isNotBlank())
                                appendLine("    $discountTipTax")
                            appendLine("    ${totalText}: ${receipt.subtotalSum.roundToTwoDecimalPlaces()}")
                        }
                        appendLine()
                    }
                    appendLine(LONG_DIVIDER_STRING)
                    appendLine()
                }
            }.trim()
        }.getOrNull()
    }
}

interface FolderTextReportCreatorInterface {
    fun createLongTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
        subTotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
    ): String?

    fun createBasicTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
    ): String?

    fun createShortTextReport(
        folderReportDataList: List<FolderReportData>,
        totalText: String,
    ): String?
}