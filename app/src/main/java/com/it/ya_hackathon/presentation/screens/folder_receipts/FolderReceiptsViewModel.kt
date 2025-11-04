package com.it.ya_hackathon.presentation.screens.folder_receipts

import androidx.lifecycle.viewModelScope
import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicIntent
import com.it.ya_hackathon.basic.BasicUiMessageIntent
import com.it.ya_hackathon.basic.BasicUiState
import com.it.ya_hackathon.basic.BasicViewModel
import com.it.ya_hackathon.domain.service.ReceiptDataServiceInterface
import com.it.ya_hackathon.domain.usecase.AllFoldersUseCaseInterface
import com.it.ya_hackathon.domain.usecase.AllReceiptsUseCaseInterface
import com.it.ya_hackathon.presentation.receipt.FolderData
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FolderReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val receiptDataService: ReceiptDataServiceInterface,
    private val allFoldersUseCase: AllFoldersUseCaseInterface,
) : BasicViewModel<
        FolderReceiptsUiState,
        FolderReceiptsIntent,
        FolderReceiptsEvent,
        FolderReceiptsUiMessageIntent
        >(
    FolderReceiptsUiState.Show
) {
    private val allReceiptsListFlow = MutableStateFlow<List<ReceiptData>?>(null)
    private val allReceiptsListState = allReceiptsListFlow.asStateFlow()

    private val isReportCreatingPending = MutableStateFlow(false)
    private val isReportCreatingPendingState = isReportCreatingPending.asStateFlow()

    private val folderData = MutableStateFlow<FolderData?>(null)
    private val folderDataState = folderData.asStateFlow()

    private fun setAllReceiptsList(newList: List<ReceiptData>) {
        allReceiptsListFlow.value = newList
    }

    private fun setIsReportCreatingPending(newBoolean: Boolean) {
        isReportCreatingPending.value = newBoolean
    }

    private fun setFolderData(newFolderData: FolderData?) {
        folderData.value = newFolderData
    }

    fun getAllReceiptsList() = allReceiptsListState
    fun getIsReportCreatingPendingState() = isReportCreatingPendingState
    fun getFolderDataState() = folderDataState

    override fun setEvent(newEvent: FolderReceiptsEvent) {
        when (newEvent) {
            is FolderReceiptsEvent.RetrieveAllReceiptsForSpecificFolder -> {
                retrieveAllReceiptsForSpecificFolder(folderId = newEvent.folderId)
                monitorAllReceiptsChange()
            }

            is FolderReceiptsEvent.MoveReceiptOutOfFolder -> {
                moveReceiptOutOfFolder(receiptData = newEvent.receiptData)
            }

            is FolderReceiptsEvent.ChangeShareStateForReceipt -> {
                changeSharedStateForReceipt(receiptData = newEvent.receiptData)
            }

            is FolderReceiptsEvent.ChangeCheckStateForSpecificReceipt -> {
                allReceiptsListFlow.value?.let { receiptsList ->
                    changeCheckStateForSpecificReceipt(
                        receiptDataLists = receiptsList,
                        receiptId = newEvent.receiptId,
                    )
                }
            }

            is FolderReceiptsEvent.TurnOffCheckStateForAllReceipts -> {
                allReceiptsListFlow.value?.let { receiptsList ->
                    turnOffCheckStateForAllReceipts(
                        receiptDataLists = receiptsList,
                    )
                }
            }

            is FolderReceiptsEvent.AddConsumerNameToFolder -> {
                folderData.value?.let { folderData ->
                    addConsumerNameToFolder(
                        folderData = folderData,
                        consumerName = newEvent.consumerName,
                    )
                }
            }

            is FolderReceiptsEvent.RetrieveFolderData -> {
                retrieveFolderData(folderId = newEvent.folderId)
            }

            is FolderReceiptsEvent.DeleteConsumerNameFromFolder -> {
                folderData.value?.let { folderData ->
                    deleteConsumerNameFromFolder(
                        folderData = folderData,
                        consumerName = newEvent.consumerName,
                    )
                }
            }

            is FolderReceiptsEvent.DeleteSpecificReceipt -> {
                deleteReceiptById(receiptId = newEvent.receiptId)
            }

            is FolderReceiptsEvent.SetIsSharedStateForCheckedReceipts -> {
                allReceiptsListFlow.value?.let { receiptsList ->
                    setIsSharedStateForCheckedReceipts(receiptsList)
                }
            }
        }
    }

    private fun monitorAllReceiptsChange() {
        viewModelScope.launch {
            allReceiptsListState.collect { list ->
                if (list != null)
                    checkIfReportGenerationIsPending(receiptDataLists = list)
            }
        }
    }

    private fun retrieveAllReceiptsForSpecificFolder(
        folderId: Long,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.gelReceiptsByFolderIdFlow(folderId = folderId).collect { list ->
                setAllReceiptsList(list.reversed())
            }
        }
    }

    private fun retrieveFolderData(
        folderId: Long,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.getFolderByIdFlow(folderId = folderId).collect { folderData ->
                setFolderData(folderData)
            }
        }
    }

    private fun changeCheckStateForSpecificReceipt(
        receiptDataLists: List<ReceiptData>,
        receiptId: Long,
    ) {
        viewModelScope.launch {
            val newReceiptDataLists = receiptDataService.changeCheckStateForSpecificReceipt(
                receiptDataList = receiptDataLists,
                receiptId = receiptId,
            )
            setAllReceiptsList(newReceiptDataLists)
        }
    }

    private fun changeSharedStateForReceipt(
        receiptData: ReceiptData,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.changeIsSharedStateForReceipt(receiptData = receiptData)
        }
    }

    private fun moveReceiptOutOfFolder(
        receiptData: ReceiptData,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptOutFolder(receiptData = receiptData)
        }
    }

    private fun turnOffCheckStateForAllReceipts(
        receiptDataLists: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            val newReceiptDataLists = receiptDataService.turnOffCheckStateForAllReceipts(
                receiptDataList = receiptDataLists,
            )
            setAllReceiptsList(newReceiptDataLists)
        }
    }

    private fun checkIfReportGenerationIsPending(
        receiptDataLists: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            val booleanResponse =
                receiptDataService.checkIfReportGenerationIsPending(receiptDataList = receiptDataLists)
            setIsReportCreatingPending(newBoolean = booleanResponse)
        }
    }

    private fun addConsumerNameToFolder(
        folderData: FolderData,
        consumerName: String,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.addConsumerNameToFolder(
                folderData = folderData,
                consumerName = consumerName,
            )
        }
    }

    private fun deleteConsumerNameFromFolder(
        folderData: FolderData,
        consumerName: String,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.deleteConsumerNameFromFolder(
                folderData = folderData,
                consumerName = consumerName,
            )
        }
    }

    private fun deleteReceiptById(receiptId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.deleteReceiptData(receiptId = receiptId)
        }
    }

    private fun setIsSharedStateForCheckedReceipts(
        allReceiptsList: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.changeIsSharedStateForAllReceipts(allReceiptsList)
        }
    }
}

interface FolderReceiptsUiState : BasicUiState {
    object Show : FolderReceiptsUiState
}

interface FolderReceiptsIntent : BasicIntent

sealed interface FolderReceiptsEvent : BasicEvent {
    class RetrieveAllReceiptsForSpecificFolder(val folderId: Long) : FolderReceiptsEvent
    class ChangeCheckStateForSpecificReceipt(val receiptId: Long) : FolderReceiptsEvent
    class MoveReceiptOutOfFolder(val receiptData: ReceiptData) : FolderReceiptsEvent
    class ChangeShareStateForReceipt(val receiptData: ReceiptData) : FolderReceiptsEvent
    object TurnOffCheckStateForAllReceipts : FolderReceiptsEvent
    class AddConsumerNameToFolder(val consumerName: String) : FolderReceiptsEvent
    class RetrieveFolderData(val folderId: Long) : FolderReceiptsEvent
    class DeleteConsumerNameFromFolder(val consumerName: String) : FolderReceiptsEvent
    class DeleteSpecificReceipt(val receiptId: Long) : FolderReceiptsEvent
    object SetIsSharedStateForCheckedReceipts : FolderReceiptsEvent
}

interface FolderReceiptsUiMessageIntent : BasicUiMessageIntent {
    object InternalError : FolderReceiptsUiMessageIntent
}