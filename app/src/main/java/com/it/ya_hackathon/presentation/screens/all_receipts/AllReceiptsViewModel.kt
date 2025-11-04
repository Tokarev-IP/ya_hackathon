package com.it.ya_hackathon.presentation.screens.all_receipts

import androidx.lifecycle.viewModelScope
import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicSimpleViewModel
import com.it.ya_hackathon.domain.usecase.AllFoldersUseCaseInterface
import com.it.ya_hackathon.domain.usecase.AllReceiptsUseCaseInterface
import com.it.ya_hackathon.presentation.receipt.FolderData
import com.it.ya_hackathon.presentation.receipt.FolderWithReceiptsData
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptWithFolderData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val allFoldersUseCase: AllFoldersUseCaseInterface,
) : BasicSimpleViewModel<AllReceiptsEvent>() {

    private val allReceiptsWithFolderDataList = MutableStateFlow<List<ReceiptWithFolderData>?>(null)
    private val allReceiptsWithFolderListState = allReceiptsWithFolderDataList.asStateFlow()

    private val foldersWithReceiptsUnarchived =
        MutableStateFlow<List<FolderWithReceiptsData>?>(null)
    private val foldersWithReceiptsUnarchivedState = foldersWithReceiptsUnarchived.asStateFlow()

    private val foldersWithReceiptsArchived = MutableStateFlow<List<FolderWithReceiptsData>?>(null)
    private val foldersWithReceiptsArchivedState = foldersWithReceiptsArchived.asStateFlow()

    private val allFoldersNamesList = MutableStateFlow<List<String>?>(null)
    private val allFoldersNamesListState = allFoldersNamesList.asStateFlow()

    private fun setAllReceiptsWithFolder(newList: List<ReceiptWithFolderData>) {
        allReceiptsWithFolderDataList.value = newList
    }

    private fun setFoldersWithReceiptsUnarchived(newList: List<FolderWithReceiptsData>) {
        foldersWithReceiptsUnarchived.value = newList
    }

    private fun setFoldersWithReceiptsArchived(newList: List<FolderWithReceiptsData>) {
        foldersWithReceiptsArchived.value = newList
    }

    private fun setAllFoldersNamesList(newList: List<String>) {
        allFoldersNamesList.value = newList
    }

    fun getAllReceiptsWithFolderList() = allReceiptsWithFolderListState
    fun getFoldersWithReceiptsUnarchived() = foldersWithReceiptsUnarchivedState
    fun getFoldersWithReceiptsArchived() = foldersWithReceiptsArchivedState
    fun getAllFoldersNamesList() = allFoldersNamesListState

    override fun setEvent(newEvent: AllReceiptsEvent) {
        when (newEvent) {
            is AllReceiptsEvent.RetrieveAllData -> {
                retrieveAllReceipts()
                retrieveAllFoldersWithReceipts()
            }

            is AllReceiptsEvent.DeleteSpecificReceipt -> {
                deleteReceiptData(receiptId = newEvent.receiptId)
            }

            is AllReceiptsEvent.MoveReceiptInFolder -> {
                moveReceiptInFolder(
                    receiptData = newEvent.receiptData,
                    folderId = newEvent.folderId,
                )
            }

            is AllReceiptsEvent.MoveReceiptOutFolder -> {
                moveReceiptOutFolder(receiptData = newEvent.receiptData)
            }

            is AllReceiptsEvent.SaveFolder -> {
                saveFolder(folderData = newEvent.folderData)
            }

            is AllReceiptsEvent.ArchiveFolder -> {
                archiveFolder(folderData = newEvent.folderData)
            }

            is AllReceiptsEvent.UnArchiveFolder -> {
                unArchiveFolder(folderData = newEvent.folderData)
            }

            is AllReceiptsEvent.DeleteSpecificFolder -> {
                deleteFolderById(folderId = newEvent.folderId)
            }
        }
    }

    private fun retrieveAllReceipts() {
        viewModelScope.launch(Dispatchers.IO) {
            allReceiptsUseCase.getAllReceiptsWithFolderFlow().collect { data ->
                setAllReceiptsWithFolder(data.reversed())
            }
        }
    }

    private fun retrieveAllFoldersWithReceipts() {
        viewModelScope.launch(Dispatchers.IO) {
            allFoldersUseCase.getFoldersWithReceiptsFlow()
                .collect { folders: List<FolderWithReceiptsData> ->
                    setFoldersWithReceiptsArchived(
                        folders
                            .filter { it.folder.isArchived == true }
                            .reversed()
                    )
                    setFoldersWithReceiptsUnarchived(
                        folders
                            .filter { it.folder.isArchived == false }
                            .reversed()
                    )
                    getAllFoldersNamesList(foldersWithReceipts = folders)
                }
        }
    }

    private fun deleteReceiptData(receiptId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.deleteReceiptData(receiptId = receiptId)
        }
    }

    private fun saveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.saveFolder(folderData)
        }
    }

    private fun moveReceiptInFolder(receiptData: ReceiptData, folderId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptInFolder(
                receiptData = receiptData,
                folderId = folderId,
            )
        }
    }

    private fun moveReceiptOutFolder(receiptData: ReceiptData) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptOutFolder(receiptData = receiptData)
        }
    }

    private fun archiveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.archiveFolder(folderData = folderData)
        }
    }

    private fun unArchiveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.unArchiveFolder(folderData = folderData)
        }
    }

    private fun getAllFoldersNamesList(
        foldersWithReceipts: List<FolderWithReceiptsData>
    ) {
        viewModelScope.launch {
            val foldersNamesList = foldersWithReceipts.map { it.folder.folderName }
            setAllFoldersNamesList(foldersNamesList)
        }
    }

    private fun deleteFolderById(folderId: Long) {
        viewModelScope.launch {
            allFoldersUseCase.deleteFolderById(folderId = folderId)
        }
    }
}

sealed interface AllReceiptsEvent : BasicEvent {
    object RetrieveAllData : AllReceiptsEvent
    class DeleteSpecificReceipt(val receiptId: Long) : AllReceiptsEvent
    class SaveFolder(val folderData: FolderData) : AllReceiptsEvent
    class ArchiveFolder(val folderData: FolderData) : AllReceiptsEvent
    class UnArchiveFolder(val folderData: FolderData) : AllReceiptsEvent
    class MoveReceiptInFolder(val receiptData: ReceiptData, val folderId: Long) : AllReceiptsEvent
    class MoveReceiptOutFolder(val receiptData: ReceiptData) : AllReceiptsEvent
    class DeleteSpecificFolder(val folderId: Long) : AllReceiptsEvent
}