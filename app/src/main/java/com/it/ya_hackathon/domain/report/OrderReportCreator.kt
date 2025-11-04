package com.it.ya_hackathon.domain.report

import com.it.ya_hackathon.basic.isMoreThanOne
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.isPositive
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import com.it.ya_hackathon.presentation.receipt.OrderReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReportCreator() : OrderReportCreatorInterface {

    override suspend fun buildOrderReportForOne(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): ReceiptReportData? {
        return withContext(Dispatchers.Default) {
            runCatching {
                var finalPrice = 0f
                var index = 1

                if (orderDataList.isEmpty())
                    return@withContext null

                val orderReportData = mutableListOf<OrderReportData>()

                for (orderData in orderDataList) {
                    if (orderData.selectedQuantity.isPositive()) {
                        val sumPrice = orderData.selectedQuantity * orderData.price
                        finalPrice += sumPrice
                        if (orderData.selectedQuantity.isMoreThanOne()) {
                            orderReportData.add(
                                OrderReportData(
                                    name = orderData.name,
                                    translatedName = orderData.translatedName,
                                    amountText = "${orderData.selectedQuantity} x ${orderData.price.roundToTwoDecimalPlaces()}",
                                    sum = sumPrice.roundToTwoDecimalPlaces(),
                                )
                            )
                        } else {
                            orderReportData.add(
                                OrderReportData(
                                    name = orderData.name,
                                    translatedName = orderData.translatedName,
                                    amountText = null,
                                    sum = sumPrice.roundToTwoDecimalPlaces(),
                                )
                            )
                        }
                        index++
                    }
                }
                val totalPriceBeforeAll = finalPrice.roundToTwoDecimalPlaces()

                if ((receiptData.discount.isNotZero() || receiptData.tip.isNotZero() || receiptData.tax.isNotZero()) && finalPrice.isNotZero()) {

                    if (receiptData.discount.isNotZero()) {
                        finalPrice -= (finalPrice * receiptData.discount) / 100
                    }
                    if (receiptData.tip.isNotZero()) {
                        finalPrice += (finalPrice * receiptData.tip) / 100
                    }
                    if (receiptData.tax.isNotZero()) {
                        finalPrice += (finalPrice * receiptData.tax) / 100
                    }
                }

                val receiptReportData = ReceiptReportData(
                    receiptName = receiptData.receiptName,
                    translatedReceiptName = receiptData.translatedReceiptName,
                    date = receiptData.date,
                    subtotalSum = totalPriceBeforeAll.roundToTwoDecimalPlaces(),
                    orderList = orderReportData,
                    discount = receiptData.discount,
                    tip = receiptData.tip,
                    tax = receiptData.tax,
                    totalSum = finalPrice.roundToTwoDecimalPlaces(),
                )

                return@withContext receiptReportData
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }

    override suspend fun buildOrderReportForAll(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
    ): List<ReceiptReportData> {
        return withContext(Dispatchers.Default) {
            runCatching {
                val consumerNameList = extractConsumerNames(orderDataSplitList)
                if (consumerNameList.isEmpty())
                    return@withContext emptyList()

                val receiptReportDataList = mutableListOf<ReceiptReportData>()

                for (consumerName in consumerNameList) {
                    var index = 1
                    var consumerFinalPrice = 0F
                    val newOrderDataSplitList =
                        orderDataSplitList.filter { consumerName in it.consumerNamesList }

                    val reportOrderList = mutableListOf<OrderReportData>()
                    for (orderDataSplit in newOrderDataSplitList) {

                        if (orderDataSplit.consumerNamesList.size.isMoreThanOne()) {
                            val newPrice =
                                (orderDataSplit.price / orderDataSplit.consumerNamesList.size).roundToTwoDecimalPlaces()

                            reportOrderList.add(
                                OrderReportData(
                                    name = orderDataSplit.name,
                                    translatedName = orderDataSplit.translatedName,
                                    amountText = "1/${orderDataSplit.consumerNamesList.size} x ${orderDataSplit.price.roundToTwoDecimalPlaces()}",
                                    sum = newPrice.roundToTwoDecimalPlaces(),
                                )
                            )
                            consumerFinalPrice += newPrice
                        } else {
                            reportOrderList.add(
                                OrderReportData(
                                    name = orderDataSplit.name,
                                    translatedName = orderDataSplit.translatedName,
                                    amountText = null,
                                    sum = orderDataSplit.price.roundToTwoDecimalPlaces(),
                                )
                            )
                            consumerFinalPrice += orderDataSplit.price.roundToTwoDecimalPlaces()
                        }
                        index++
                    }
                    val totalPriceBeforeAll = consumerFinalPrice.roundToTwoDecimalPlaces()

                    if ((receiptData.discount.isNotZero() || receiptData.tip.isNotZero() || receiptData.tax.isNotZero()) && consumerFinalPrice.isNotZero()) {
                        if (receiptData.discount.isNotZero()) {
                            consumerFinalPrice -= (consumerFinalPrice * receiptData.discount) / 100
                        }
                        if (receiptData.tip.isNotZero()) {
                            consumerFinalPrice += (consumerFinalPrice * receiptData.tip) / 100
                        }
                        if (receiptData.tax.isNotZero()) {
                            consumerFinalPrice += (consumerFinalPrice * receiptData.tax) / 100
                        }
                    }
                    receiptReportDataList.add(
                        ReceiptReportData(
                            receiptName = receiptData.receiptName,
                            translatedReceiptName = receiptData.translatedReceiptName,
                            date = receiptData.date,
                            consumerName = consumerName,
                            subtotalSum = totalPriceBeforeAll.roundToTwoDecimalPlaces(),
                            orderList = reportOrderList,
                            discount = receiptData.discount,
                            tip = receiptData.tip,
                            tax = receiptData.tax,
                            totalSum = consumerFinalPrice.roundToTwoDecimalPlaces(),
                        )
                    )
                }
                return@withContext receiptReportDataList
            }.getOrElse { e ->
                return@withContext emptyList()
            }
        }
    }

    private fun extractConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<String> {
        val consumerNamesSet = mutableSetOf<String>()
        for (orderDataSplit in orderDataSplitList) {
            consumerNamesSet.addAll(orderDataSplit.consumerNamesList)
        }
        return consumerNamesSet.toList()
    }
}

interface OrderReportCreatorInterface {
    suspend fun buildOrderReportForOne(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): ReceiptReportData?

    suspend fun buildOrderReportForAll(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
    ): List<ReceiptReportData>
}