package com.it.ya_hackathon.presentation.screens.split_receipt_for_all

import androidx.lifecycle.viewModelScope
import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicFunResponse
import com.it.ya_hackathon.basic.BasicIntent
import com.it.ya_hackathon.basic.BasicResultFunResponse
import com.it.ya_hackathon.basic.BasicUiMessageIntent
import com.it.ya_hackathon.basic.BasicUiState
import com.it.ya_hackathon.basic.BasicViewModel
import com.it.ya_hackathon.domain.report.OrderReportCreatorInterface
import com.it.ya_hackathon.domain.service.OrderDataSplitServiceInterface
import com.it.ya_hackathon.domain.usecase.AllFoldersUseCaseInterface
import com.it.ya_hackathon.domain.usecase.AllReceiptsUseCaseInterface
import com.it.ya_hackathon.domain.usecase.SplitReceiptUseCaseInterface
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SplitReceiptForAllViewModel(
    private val splitReceiptUseCase: SplitReceiptUseCaseInterface,
    private val orderDataSplitService: OrderDataSplitServiceInterface,
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val allFolderUseCase: AllFoldersUseCaseInterface,
    private val orderReportCreator: OrderReportCreatorInterface,
) : BasicViewModel<
        SplitReceiptForAllUiState,
        SplitReceiptForAllIntent,
        SplitReceiptForAllEvents,
        SplitReceiptForAllUiMessageIntent>(initialUiState = SplitReceiptForAllUiState.Loading) {

    private var orderDataList: List<OrderData> = emptyList()
    private var folderConsumerNamesList: List<String> = emptyList()

    private val receiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val receiptDataState = receiptDataFlow.asStateFlow()

    private val splitOrderDataSplitListFlow = MutableStateFlow<List<OrderDataSplit>>(emptyList())
    private val splitOrderDataCheckListState = splitOrderDataSplitListFlow.asStateFlow()

    private val allConsumerNamesListFlow = MutableStateFlow<List<String>>(emptyList())
    private val allConsumerNamesListState = allConsumerNamesListFlow.asStateFlow()

    private val checkStateExistenceFlow = MutableStateFlow(false)
    private val checkStateExistenceState = checkStateExistenceFlow.asStateFlow()

    private val receiptReportDataListFlow = MutableStateFlow<List<ReceiptReportData>>(emptyList())
    private val receiptReportDataListState = receiptReportDataListFlow.asStateFlow()

    private fun setReceiptData(data: ReceiptData?) {
        receiptDataFlow.value = data
    }


    private fun setOrderDataSplitList(list: List<OrderDataSplit>) {
        splitOrderDataSplitListFlow.value = list
    }

    private fun setAllConsumerNamesList(list: List<String>) {
        allConsumerNamesListFlow.value = list
    }

    private fun setCheckStateExistence(state: Boolean) {
        checkStateExistenceFlow.value = state
    }

    private fun setFolderConsumerNamesList(list: List<String>) {
        folderConsumerNamesList = list
    }

    private fun setReceiptReportDataList(list: List<ReceiptReportData>) {
        receiptReportDataListFlow.value = list
    }

    fun getReceiptData() = receiptDataState
    fun getOrderDataSplitList() = splitOrderDataCheckListState
    fun getAllConsumerNamesList() = allConsumerNamesListState
    fun getIsCheckStateExisted() = checkStateExistenceState
    fun getFolderConsumerNamesList() = folderConsumerNamesList
    fun getReceiptReportDataList() = receiptReportDataListState

    override fun setEvent(newEvent: SplitReceiptForAllEvents) {
        when (newEvent) {
            is SplitReceiptForAllEvents.ActivateOrderReportCreator -> {
                monitorOrderDataCheck()
            }

            is SplitReceiptForAllEvents.ClearConsumerNameForOrder -> {
                clearSpecificConsumerNameForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    consumerName = newEvent.name,
                )
            }

            is SplitReceiptForAllEvents.RetrieveReceiptData -> {
                monitorReceiptData()
                retrieveAllData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptForAllEvents.SetCheckState -> {
                setCheckState(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    state = newEvent.state,
                )
            }

            is SplitReceiptForAllEvents.SetInitialConsumerNamesForCheckedOrders -> {
                setInitialConsumerNamesForCheckedOrders(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    consumerNamesList = newEvent.consumerNamesList,
                )
            }

            is SplitReceiptForAllEvents.ClearOrderReport -> {
                clearOrderReport(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                )
            }

            is SplitReceiptForAllEvents.SaveReceiptAndOrderDataSplit -> {
                receiptDataFlow.value?.let { receiptData ->
                    saveReceiptOrderDataSplitList(
                        orderDataSplitList = splitOrderDataCheckListState.value,
                        orderDataList = orderDataList,
                        receiptData = receiptData,
                    )
                } ?: setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
            }

            is SplitReceiptForAllEvents.ClearAllConsumerNames -> {
                clearAllConsumerNamesForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                )
            }

            is SplitReceiptForAllEvents.AddConsumerNameForSpecificOrder -> {
                addConsumerNameForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    name = newEvent.name,
                )
            }

            is SplitReceiptForAllEvents.SetIsSharedStateForReceipt -> {
                setIsSharedStateForReceipt(newEvent.receiptData)
            }

            is SplitReceiptForAllEvents.AddConsumerNameToReceipt -> {
                receiptDataFlow.value?.let { receiptData ->
                    addConsumerNameToReceipt(
                        receiptData = receiptData,
                        name = newEvent.name,
                    )
                } ?: setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
            }

            is SplitReceiptForAllEvents.RemoveConsumerNameFromReceipt -> {
                receiptDataFlow.value?.let { receiptData ->
                    removeConsumerNameFromReceipt(
                        receiptData = receiptData,
                        name = newEvent.name,
                    )
                } ?: setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
            }
        }
    }

    private fun retrieveAllData(receiptId: Long) {
        viewModelScope.launch {
            setUiState(SplitReceiptForAllUiState.Loading)
            async { retrieveReceiptData(receiptId = receiptId) }.await()
            async { retrieveOrderDataSplitList(receiptId = receiptId) }.await()
            async {
                receiptDataFlow.value?.let { receiptData ->
                    if (receiptData.folderId != null)
                        retrieveFolderData(
                            folderId = receiptData.folderId,
                            receiptData = receiptData,
                        )
                }
            }.await()
            setUiState(SplitReceiptForAllUiState.Show)
        }
    }

    private fun monitorReceiptData() {
        viewModelScope.launch {
            receiptDataFlow.collect { receiptData ->
                receiptData?.let { receipt ->
                    setAllConsumerNames(
                        receiptConsumerNames = receipt.consumerNamesList,
                        folderConsumerNamesList = folderConsumerNamesList,
                    )
                } ?: setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
            }
        }
    }

    private suspend fun retrieveReceiptData(receiptId: Long) {
        splitReceiptUseCase.retrieveReceiptData(receiptId = receiptId).run {
            setReceiptData(this)
        }
    }

    private suspend fun retrieveFolderData(folderId: Long, receiptData: ReceiptData) {
        allFolderUseCase.getFolderById(folderId = folderId).run {
            this?.consumerNamesList?.let { consumerNames ->
                setFolderConsumerNamesList(consumerNames)
                setAllConsumerNames(
                    receiptConsumerNames = receiptData.consumerNamesList,
                    folderConsumerNamesList = consumerNames,
                )
            }
        }
    }

    private suspend fun retrieveOrderDataSplitList(receiptId: Long) {
        splitReceiptUseCase.retrieveOrderDataList(receiptId = receiptId).run {
            orderDataList = this
            val orderDataSplitList =
                orderDataSplitService.convertOrderDataListToOrderDataSplitList(
                    orderDataList = this
                )
            setOrderDataSplitList(orderDataSplitList)
        }
    }

    @OptIn(FlowPreview::class)
    private fun monitorOrderDataCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            splitOrderDataSplitListFlow
                .debounce(250L)
                .collect { orderDataSplitList ->
                    receiptDataFlow.value?.let { receipt ->
                        createReceiptReportDataList(
                            receiptData = receipt,
                            orderDataSplitList = orderDataSplitList,
                        )
                    }
                    setCheckStateExistence(orderDataSplitList = orderDataSplitList)
                }
        }
    }

    private fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList =
                orderDataSplitService.clearSpecificConsumerNameForSpecificOrder(
                    orderDataSplitList = orderDataSplitList,
                    position = position,
                    consumerName = consumerName,
                )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setInitialConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList =
                orderDataSplitService.setInitialConsumerNamesForCheckedOrders(
                    orderDataSplitList = orderDataSplitList,
                    consumerNamesList = consumerNamesList,
                )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataSplitService.setCheckState(
                orderDataSplitList = orderDataSplitList,
                position = position,
                state = state,
            )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun clearOrderReport(
        orderDataSplitList: List<OrderDataSplit>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataSplitService.clearOrderDataSplits(
                orderDataSplitList = orderDataSplitList,
            )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setAllConsumerNames(
        receiptConsumerNames: List<String>,
        folderConsumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newConsumerNameList = orderDataSplitService.getAllConsumerNames(
                receiptConsumerNamesList = receiptConsumerNames,
                folderConsumerNamesList = folderConsumerNamesList,
            )
            setAllConsumerNamesList(newConsumerNameList)
        }
    }

    private fun setCheckStateExistence(
        orderDataSplitList: List<OrderDataSplit>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newIsCheckStateExisted = orderDataSplitService.hasExistingCheckState(
                orderDataSplitList = orderDataSplitList,
            )
            setCheckStateExistence(newIsCheckStateExisted)
        }
    }

    private fun saveReceiptOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>,
        receiptData: ReceiptData,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val response = splitReceiptUseCase.saveReceiptAndOrderDataSplitList(
                orderDataSplitList = orderDataSplitList,
                orderDataList = orderDataList,
                receiptData = receiptData,
            )
            when (response) {
                is BasicFunResponse.Success -> {
                    setUiMessageIntent(SplitReceiptForAllUiMessageIntent.DataWasSaved)
                }

                is BasicFunResponse.Error -> {
                    setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
                }
            }
        }
    }

    private fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderSplitDataList = orderDataSplitService.clearAllConsumerNamesForSpecificOrder(
                orderDataSplitList = orderDataSplitList,
                position = position,
            )
            setOrderDataSplitList(newOrderSplitDataList)
        }
    }

    private fun addConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderSplitDataList = orderDataSplitService.addNewConsumerNameForSpecificOrder(
                orderDataSplitList = orderDataSplitList,
                position = position,
                name = name,
            )
            setOrderDataSplitList(newOrderSplitDataList)
        }
    }

    private fun setIsSharedStateForReceipt(
        receiptData: ReceiptData
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            allReceiptsUseCase.insertReceiptData(
                receiptData = receiptData.copy(isShared = true)
            )
        }
    }


    private fun addConsumerNameToReceipt(
        receiptData: ReceiptData,
        name: String,
    ) {
        viewModelScope.launch {
            splitReceiptUseCase.addConsumerNameToReceiptData(
                receiptData = receiptData,
                name = name,
            ).run {
                when (this) {
                    is BasicResultFunResponse.Success -> {
                        setReceiptData(this.data)
                    }

                    is BasicResultFunResponse.Error -> {
                        setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
                    }
                }
            }
        }
    }

    private fun removeConsumerNameFromReceipt(
        receiptData: ReceiptData,
        name: String,
    ) {
        viewModelScope.launch {
            splitReceiptUseCase.removeConsumerNameFromReceiptData(
                receiptData = receiptData,
                name = name,
            ).run {
                when (this) {
                    is BasicResultFunResponse.Success -> {
                        setReceiptData(this.data)
                    }

                    is BasicResultFunResponse.Error -> {
                        setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
                    }
                }
            }
        }
    }

    private fun createReceiptReportDataList(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
    ) {
        viewModelScope.launch {
            orderReportCreator.buildOrderReportForAll(
                receiptData = receiptData,
                orderDataSplitList = orderDataSplitList,
            ).run {
                setReceiptReportDataList(this)
            }
        }
    }
}

sealed interface SplitReceiptForAllEvents : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptForAllEvents
    object ActivateOrderReportCreator : SplitReceiptForAllEvents

    class SetCheckState(val position: Int, val state: Boolean) : SplitReceiptForAllEvents
    class SetInitialConsumerNamesForCheckedOrders(val consumerNamesList: List<String>) :
        SplitReceiptForAllEvents

    class ClearConsumerNameForOrder(val position: Int, val name: String) :
        SplitReceiptForAllEvents

    object ClearOrderReport : SplitReceiptForAllEvents
    object SaveReceiptAndOrderDataSplit : SplitReceiptForAllEvents
    class ClearAllConsumerNames(val position: Int) : SplitReceiptForAllEvents
    class AddConsumerNameForSpecificOrder(val position: Int, val name: String) :
        SplitReceiptForAllEvents

    class SetIsSharedStateForReceipt(val receiptData: ReceiptData) : SplitReceiptForAllEvents
    class AddConsumerNameToReceipt(val name: String) : SplitReceiptForAllEvents
    class RemoveConsumerNameFromReceipt(val name: String) : SplitReceiptForAllEvents
}

interface SplitReceiptForAllUiState : BasicUiState {
    object Show : SplitReceiptForAllUiState
    object Loading : SplitReceiptForAllUiState
}

interface SplitReceiptForAllIntent : BasicIntent

interface SplitReceiptForAllUiMessageIntent : BasicUiMessageIntent {
    object DataWasSaved : SplitReceiptForAllUiMessageIntent
    object InternalError : SplitReceiptForAllUiMessageIntent
}