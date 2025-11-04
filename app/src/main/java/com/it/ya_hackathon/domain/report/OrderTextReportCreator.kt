package com.it.ya_hackathon.domain.report

import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData

class OrderTextReportCreator() : OrderTextReportCreatorInterface {

    private companion object {
        const val START_STRING = "•"
        const val SHORT_DIVIDER_STRING = "------"
    }

    override fun buildTextOrderReportForOne(
        reportData: ReceiptReportData,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): String? {
        return runCatching {
            if (reportData.orderList.isEmpty())
                return null

            buildString {
                // Title
                appendLine(" $START_STRING ${reportData.receiptName}")
                reportData.translatedReceiptName?.takeIf { it.isNotBlank() }?.let { appendLine(" $START_STRING $it") }
                if (reportData.date.isNotBlank()) appendLine(" $START_STRING ${reportData.date}")

                appendLine()

                // Orders
                reportData.orderList.forEachIndexed { index, order ->
                    append("  ${index + 1}. ${order.name}")
                    order.translatedName?.takeIf { it.isNotBlank() }?.let { append(" ($it)") }

                    if (order.amountText != null) {
                        append(" = ${order.amountText} = ${order.sum.roundToTwoDecimalPlaces()}")
                    } else {
                        append(" = ${order.sum.roundToTwoDecimalPlaces()}")
                    }
                    appendLine()
                }

                appendLine(SHORT_DIVIDER_STRING)

                // Block for discount, tip, tax
                if (reportData.discount.isNotZero() || reportData.tip.isNotZero() || reportData.tax.isNotZero()) {
                    appendLine(" ${subtotalText}: ${reportData.subtotalSum.roundToTwoDecimalPlaces()}")

                    if (reportData.discount.isNotZero()) {
                        appendLine("   ${discountText}: -${reportData.discount.roundToTwoDecimalPlaces()} %")
                    }
                    if (reportData.tip.isNotZero()) {
                        appendLine("   ${tipText}: +${reportData.tip.roundToTwoDecimalPlaces()} %")
                    }
                    if (reportData.tax.isNotZero()) {
                        appendLine("   ${taxText}: +${reportData.tax.roundToTwoDecimalPlaces()} %")
                    }

                    appendLine(SHORT_DIVIDER_STRING)
                }
                // Total sum
                appendLine(" ${totalText}: ${reportData.totalSum?.roundToTwoDecimalPlaces() ?: reportData.subtotalSum.roundToTwoDecimalPlaces()}")
            }.trim()
        }.getOrNull()
    }

    override fun buildTextOrderReportForAll(
        reportDataList: List<ReceiptReportData>,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String
    ): String? {
        return runCatching {
            if (reportDataList.isEmpty())
                return null

            buildString {
                // Title
                val header = reportDataList.first()
                if (header.receiptName.isNotBlank()) appendLine("$START_STRING ${header.receiptName}")
                header.translatedReceiptName?.takeIf { it.isNotBlank() }?.let { appendLine("$START_STRING $it") }
                if (header.date.isNotBlank()) appendLine("$START_STRING ${header.date}")

                appendLine()

                // Block for every consumer
                reportDataList.forEach { consumerReport ->
                    appendLine("$START_STRING ${consumerReport.consumerName} $START_STRING $totalText: ${consumerReport.totalSum ?: consumerReport.subtotalSum}")

                    consumerReport.orderList.forEachIndexed { index, order ->
                        append("  ${index + 1}. ${order.name}")
                        order.translatedName?.takeIf { it.isNotBlank() }?.let { append(" ($it)") }

                        if (order.amountText != null) {
                            append(" = ${order.amountText} = ${order.sum.roundToTwoDecimalPlaces()}")
                        } else {
                            append(" = ${order.sum.roundToTwoDecimalPlaces()}")
                        }
                        appendLine()
                    }

                    // Block for discount, tip, tax
                    if (consumerReport.discount.isNotZero() || consumerReport.tip.isNotZero() || consumerReport.tax.isNotZero()) {
                        appendLine(SHORT_DIVIDER_STRING)
                        appendLine(" ${subtotalText}: ${consumerReport.subtotalSum.roundToTwoDecimalPlaces()}")

                        if (consumerReport.discount.isNotZero()) {
                            appendLine("   ${discountText}: - ${consumerReport.discount.roundToTwoDecimalPlaces()} %")
                        }
                        if (consumerReport.tip.isNotZero()) {
                            appendLine("   ${tipText}: + ${consumerReport.tip.roundToTwoDecimalPlaces()} %")
                        }
                        if (consumerReport.tax.isNotZero()) {
                            appendLine("   ${taxText}: + ${consumerReport.tax.roundToTwoDecimalPlaces()} %")
                        }
                    }

                    // Total sum
                    appendLine(" ${totalText}: ${(consumerReport.totalSum ?: consumerReport.subtotalSum).roundToTwoDecimalPlaces()}")
                    appendLine()
                }
            }.trim()
        }.getOrNull()
    }
}


interface OrderTextReportCreatorInterface {
    fun buildTextOrderReportForOne(
        reportData: ReceiptReportData,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String,
    ): String?

    fun buildTextOrderReportForAll(
        reportDataList: List<ReceiptReportData>,
        subtotalText: String,
        discountText: String,
        tipText: String,
        taxText: String,
        totalText: String,
    ): String?
}