package com.it.ya_hackathon.domain.usecase

import com.it.ya_hackathon.data.room.receipt.ReceiptDbRepositoryInterface
import com.it.ya_hackathon.domain.service.OrderDataSplitServiceInterface
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptWithOrdersDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
    private val orderDataSplitService: OrderDataSplitServiceInterface,
) : FolderReceiptsUseCaseInterface {

    override suspend fun createDataForReport(
        allReceiptsList: List<ReceiptData>,
    ): DataForReport? = withContext(Dispatchers.IO) {
        runCatching {
            val receiptsList = allReceiptsList.filter { it.isChecked == true }

            val consumerNamesSet = mutableSetOf<String>()
            val receiptWithOrdersDataSplit = mutableListOf<ReceiptWithOrdersDataSplit>()

            for (receipt in receiptsList) {
                val orderDataList = receiptDbRepository.getOrdersByReceiptId(receiptId = receipt.id)
                val orderDataSplitList =
                    orderDataSplitService.convertOrderDataListToOrderDataSplitList(
                        orderDataList = orderDataList,
                    )
                receiptWithOrdersDataSplit.add(
                    ReceiptWithOrdersDataSplit(
                        receipt = receipt,
                        orders = orderDataSplitList,
                    )
                )
                for (orderDataSplit in orderDataSplitList) {
                    consumerNamesSet.addAll(orderDataSplit.consumerNamesList)
                }
            }
            return@withContext DataForReport(
                receiptWithOrdersList = receiptWithOrdersDataSplit,
                consumerNamesList = consumerNamesSet.toList(),
            )
        }.getOrElse { e: Throwable ->
            return@withContext null
        }
    }
}

class DataForReport(
    val receiptWithOrdersList: List<ReceiptWithOrdersDataSplit>,
    val consumerNamesList: List<String>,
)

interface FolderReceiptsUseCaseInterface {
    suspend fun createDataForReport(allReceiptsList: List<ReceiptData>): DataForReport?
}