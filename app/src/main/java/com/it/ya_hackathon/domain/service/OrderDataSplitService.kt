package com.it.ya_hackathon.domain.service

import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.it.ya_hackathon.data.services.DataConstants.ORDER_CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDataSplitService() : OrderDataSplitServiceInterface {

    override suspend fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position && orderDataSplit.consumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
                    orderDataSplit.copy(checked = state)
                } else {
                    orderDataSplit
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun setInitialConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataSplit ->
                if (orderDataSplit.checked) {
                    orderDataSplit.copy(
                        consumerNamesList = consumerNamesList,
                        checked = false
                    )
                } else
                    orderDataSplit
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position) {
                    val newConsumerNamesList = orderDataSplit.consumerNamesList.filter {
                        it != consumerName
                    }
                    orderDataSplit.copy(consumerNamesList = newConsumerNamesList, checked = false)
                } else {
                    orderDataSplit
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun getAllConsumerNames(
        receiptConsumerNamesList: List<String>,
        folderConsumerNamesList: List<String>,
    ): List<String> = withContext(Dispatchers.Default) {
        runCatching {
            val consumerNamesSet = mutableSetOf<String>()
            consumerNamesSet.addAll(folderConsumerNamesList)
            consumerNamesSet.addAll(receiptConsumerNamesList)
            val consumerNamesList = consumerNamesSet.toList()
            return@withContext consumerNamesList
        }.getOrElse { e: Throwable ->
            return@withContext emptyList()
        }
    }

    override suspend fun clearOrderDataSplits(
        orderDataSplitList: List<OrderDataSplit>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataSplit ->
                orderDataSplit.copy(
                    consumerNamesList = emptyList(),
                    checked = false,
                )
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun hasExistingCheckState(
        orderDataSplitList: List<OrderDataSplit>
    ): Boolean = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.any { orderDataSplit ->
                orderDataSplit.checked
            }
        }.getOrElse { e: Throwable ->
            return@withContext false
        }
    }

    override suspend fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position)
                    orderDataSplit.copy(consumerNamesList = emptyList())
                else
                    orderDataSplit
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun addNewConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position) {
                    val newConsumerNameList = orderDataSplit.consumerNamesList
                        .toMutableList()
                        .apply {
                            if (size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES)
                                add(name)
                        }
                    orderDataSplit.copy(consumerNamesList = newConsumerNameList)
                } else
                    orderDataSplit
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun convertOrderDataListToOrderDataSplitList(
        orderDataList: List<OrderData>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            val orderDataSplitList = mutableListOf<OrderDataSplit>()
            for (orderData in orderDataList) {
                val consumerNames = orderData.consumersList
                repeat(orderData.quantity) { index ->
                    var consumerName: String? = null
                    if (consumerNames.size > index) {
                        consumerName = consumerNames[index]
                    }
                    orderDataSplitList.add(
                        OrderDataSplit(
                            name = orderData.name,
                            translatedName = orderData.translatedName,
                            price = orderData.price,
                            orderDataId = orderData.id,
                            consumerNamesList = consumerName?.split(ORDER_CONSUMER_NAME_DIVIDER)
                                ?: emptyList(),
                            checked = false,
                        )
                    )
                }
            }
            return@withContext orderDataSplitList
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<OrderDataSplit>()
        }
    }
}

interface OrderDataSplitServiceInterface {
    suspend fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean,
    ): List<OrderDataSplit>

    suspend fun setInitialConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ): List<OrderDataSplit>

    suspend fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ): List<OrderDataSplit>

    suspend fun getAllConsumerNames(
        receiptConsumerNamesList: List<String>,
        folderConsumerNamesList: List<String>,
    ): List<String>

    suspend fun clearOrderDataSplits(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<OrderDataSplit>

    suspend fun hasExistingCheckState(
        orderDataSplitList: List<OrderDataSplit>,
    ): Boolean

    suspend fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
    ): List<OrderDataSplit>

    suspend fun addNewConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String,
    ): List<OrderDataSplit>

    suspend fun convertOrderDataListToOrderDataSplitList(
        orderDataList: List<OrderData>
    ): List<OrderDataSplit>
}