package com.it.ya_hackathon.domain.usecase

import com.it.ya_hackathon.basic.BasicFunResponse
import com.it.ya_hackathon.basic.BasicResultFunResponse
import com.it.ya_hackathon.data.room.receipt.ReceiptDbRepositoryInterface
import com.it.ya_hackathon.data.services.DataConstants.ORDER_CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class SplitReceiptUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : SplitReceiptUseCaseInterface {

    override suspend fun retrieveReceiptData(receiptId: Long): ReceiptData? {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getReceiptDataById(id = receiptId)
            }.getOrNull()
        }
    }

    override suspend fun retrieveReceiptDataFlow(receiptId: Long): Flow<ReceiptData?> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getReceiptByIdFlow(receiptId)
                .catch { emit(null) }
        }
    }

    override suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getOrdersByReceiptId(receiptId = receiptId)
            }.getOrElse {
                return@withContext emptyList()
            }
        }
    }

    override suspend fun saveReceiptAndOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>,
        receiptData: ReceiptData,
    ): BasicFunResponse = withContext(Dispatchers.IO) {
        runCatching {
            val newOrderDataList = orderDataList.map { orderData ->
                transformOrderDataSplitListToOrderData(
                    orderDataSplitList = orderDataSplitList.filter { it.orderDataId == orderData.id },
                    orderData = orderData
                )
            }
            receiptDbRepository.insertOrderDataLists(orderDataList = newOrderDataList)
            receiptDbRepository.insertReceiptData(receiptData = receiptData)
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun addConsumerNameToReceiptData(
        receiptData: ReceiptData,
        name: String
    ): BasicResultFunResponse<ReceiptData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newConsumerSet = receiptData.consumerNamesList.toMutableSet().apply {
                    add(name)
                }
                val newConsumerList = newConsumerSet.toList()
                val newReceiptData = receiptData.copy(consumerNamesList = newConsumerList)
                receiptDbRepository.insertReceiptData(newReceiptData)
                BasicResultFunResponse.Success(newReceiptData)
            }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                BasicResultFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun removeConsumerNameFromReceiptData(
        receiptData: ReceiptData,
        name: String
    ): BasicResultFunResponse<ReceiptData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newConsumerSet = receiptData.consumerNamesList.toMutableSet().apply {
                    remove(name)
                }
                val newConsumerList = newConsumerSet.toList()
                val newReceiptData = receiptData.copy(consumerNamesList = newConsumerList)
                receiptDbRepository.insertReceiptData(newReceiptData)
                BasicResultFunResponse.Success(newReceiptData)
            }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                BasicResultFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    private fun transformOrderDataSplitListToOrderData(
        orderDataSplitList: List<OrderDataSplit>,
        orderData: OrderData,
    ): OrderData {
        val newConsumerList = mutableListOf<String>()
        for (orderDataSplit in orderDataSplitList) {
            val consumerNames =
                orderDataSplit.consumerNamesList.joinToString(ORDER_CONSUMER_NAME_DIVIDER)
            if (consumerNames.isNotEmpty())
                newConsumerList.add(consumerNames)
        }
        return orderData.copy(consumersList = newConsumerList)
    }
}

interface SplitReceiptUseCaseInterface {
    suspend fun retrieveReceiptData(receiptId: Long): ReceiptData?
    suspend fun retrieveReceiptDataFlow(receiptId: Long): Flow<ReceiptData?>
    suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData>
    suspend fun saveReceiptAndOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>,
        receiptData: ReceiptData,
    ): BasicFunResponse

    suspend fun addConsumerNameToReceiptData(
        receiptData: ReceiptData,
        name: String
    ): BasicResultFunResponse<ReceiptData>

    suspend fun removeConsumerNameFromReceiptData(
        receiptData: ReceiptData,
        name: String
    ): BasicResultFunResponse<ReceiptData>
}