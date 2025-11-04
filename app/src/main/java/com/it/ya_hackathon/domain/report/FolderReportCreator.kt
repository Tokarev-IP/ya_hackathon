package com.it.ya_hackathon.domain.report

import com.it.ya_hackathon.basic.isMoreThanOne
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.FolderReportData
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import com.it.ya_hackathon.presentation.receipt.OrderReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptWithOrdersDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderReportCreator() : FolderReportCreatorInterface {

    override suspend fun getFolderDataList(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): List<FolderReportData> = withContext(Dispatchers.Default) {
        runCatching {
            val folderReportDataList = mutableListOf<FolderReportData>()

            if (consumerNamesList.isEmpty() || receiptWithOrdersDataSplitList.isEmpty())
                return@withContext emptyList()

            for (consumer in consumerNamesList) {
                var consumerTotalSum = 0F
                val receiptReportDataList = mutableListOf<ReceiptReportData>()

                for (receiptWithOrdersDataSplit in receiptWithOrdersDataSplitList) {
                    val orderReportDataList = mutableListOf<OrderReportData>()

                    val receipt = receiptWithOrdersDataSplit.receipt
                    val isContained = isConsumerNameInOrders(
                        consumerName = consumer,
                        ordersList = receiptWithOrdersDataSplit.orders,
                    )
                    var index = 1
                    if (isContained) {
                        var receiptTotalSum = 0F

                        for (orderDataSplit in receiptWithOrdersDataSplit.orders) {
                            if (consumer in orderDataSplit.consumerNamesList) {
                                val orderPrice =
                                    (orderDataSplit.price / orderDataSplit.consumerNamesList.size).roundToTwoDecimalPlaces()
                                if (orderDataSplit.consumerNamesList.size.isMoreThanOne()) {
                                    orderReportDataList.add(
                                        OrderReportData(
                                            name = orderDataSplit.name,
                                            translatedName = orderDataSplit.translatedName,
                                            amountText = "1/${orderDataSplit.consumerNamesList.size} x ${orderDataSplit.price}",
                                            sum = orderPrice.roundToTwoDecimalPlaces()
                                        )
                                    )
                                    receiptTotalSum += orderPrice
                                } else {

                                    orderReportDataList.add(
                                        OrderReportData(
                                            name = orderDataSplit.name,
                                            translatedName = orderDataSplit.translatedName,
                                            amountText = null,
                                            sum = orderDataSplit.price.roundToTwoDecimalPlaces()
                                        )
                                    )
                                    receiptTotalSum += orderDataSplit.price
                                }
                                index++
                            }
                        }

                        val subTotalSum = receiptTotalSum.roundToTwoDecimalPlaces()

                        if ((receipt.discount.isNotZero() || receipt.tip.isNotZero() || receipt.tax.isNotZero()) && receiptTotalSum.isNotZero()) {

                            if (receipt.discount.isNotZero()) {
                                receiptTotalSum -= (receiptTotalSum * receipt.discount) / 100
                            }
                            if (receipt.tip.isNotZero()) {
                                receiptTotalSum += (receiptTotalSum * receipt.tip) / 100
                            }
                            if (receipt.tax.isNotZero()) {
                                receiptTotalSum += (receiptTotalSum * receipt.tax) / 100
                            }
                        }
                        consumerTotalSum += receiptTotalSum

                        receiptReportDataList.add(
                            ReceiptReportData(
                                receiptName = receipt.receiptName,
                                translatedReceiptName = receipt.translatedReceiptName,
                                date = receipt.date,
                                consumerName = consumer,
                                subtotalSum = subTotalSum,
                                orderList = orderReportDataList,
                                tax = receipt.tax,
                                discount = receipt.discount,
                                tip = receipt.tip,
                                totalSum = receiptTotalSum
                            )
                        )
                    }
                }
                folderReportDataList.add(
                    FolderReportData(
                        consumerName = consumer,
                        totalSum = consumerTotalSum.roundToTwoDecimalPlaces(),
                        receiptList = receiptReportDataList
                    )
                )
            }
            return@withContext folderReportDataList
        }.getOrElse { e: Throwable ->
            return@withContext emptyList()
        }
    }

    private fun isConsumerNameInOrders(
        consumerName: String,
        ordersList: List<OrderDataSplit>,
    ): Boolean {
        return ordersList.any { orderDataSplit ->
            consumerName in orderDataSplit.consumerNamesList
        }
    }
}

interface FolderReportCreatorInterface {
    suspend fun getFolderDataList(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): List<FolderReportData>
}