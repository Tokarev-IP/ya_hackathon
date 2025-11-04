package com.it.ya_hackathon.domain.service

import com.it.ya_hackathon.presentation.receipt.OrderData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDataService() : OrderDataServiceInterface {

    override suspend fun addQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataList.map { currentData ->
                if (currentData.id == orderId && currentData.selectedQuantity < currentData.quantity) {
                    currentData.let {
                        val updatedQuantity = it.selectedQuantity + 1
                        it.copy(selectedQuantity = updatedQuantity)
                    }
                } else {
                    currentData
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataList
        }
    }

    override suspend fun subtractQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataList.map { currentData ->
                if (currentData.id == orderId && currentData.selectedQuantity > 0) {
                    currentData.let {
                        val updatedQuantity = it.selectedQuantity - 1
                        it.copy(selectedQuantity = updatedQuantity)
                    }
                } else {
                    currentData
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataList
        }
    }

    override suspend fun clearAllQuantity(
        orderDataList: List<OrderData>
    ): List<OrderData> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataList.map { currentData ->
                currentData.copy(selectedQuantity = 0)
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataList
        }
    }
}

interface OrderDataServiceInterface {
    suspend fun addQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData>

    suspend fun subtractQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData>

    suspend fun clearAllQuantity(
        orderDataList: List<OrderData>,
    ): List<OrderData>
}