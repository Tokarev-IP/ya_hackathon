package com.it.ya_hackathon.presentation.screens.show_reports

import androidx.lifecycle.viewModelScope
import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicIntent
import com.it.ya_hackathon.basic.BasicUiMessageIntent
import com.it.ya_hackathon.basic.BasicUiState
import com.it.ya_hackathon.basic.BasicViewModel
import com.it.ya_hackathon.domain.report.FolderReportCreatorInterface
import com.it.ya_hackathon.domain.usecase.FolderReceiptsUseCaseInterface
import com.it.ya_hackathon.presentation.receipt.FolderReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowReportsViewModel(
    private val folderReceiptsUseCase: FolderReceiptsUseCaseInterface,
    private val folderReportCreator: FolderReportCreatorInterface,
) : BasicViewModel<
        ShowReportsUiState,
        ShowReportsIntent,
        ShowReportsEvent,
        ShowReportsUiMessageIntent
        >(ShowReportsUiState.Show) {

    private val folderReportDataList = MutableStateFlow<List<FolderReportData>>(emptyList())
    private val folderReportDataListState = folderReportDataList.asStateFlow()

    fun getFolderReportDataListState() = folderReportDataListState

    private fun setFolderReportDataList(newFolderReportDataList: List<FolderReportData>) {
        folderReportDataList.value = newFolderReportDataList
    }

    override fun setEvent(newEvent: ShowReportsEvent) {
        when (newEvent) {
            is ShowReportsEvent.SetInitialData -> {
                createFolderReportDataList(allReceiptList = newEvent.receiptDataList)
            }
        }
    }

    private fun createFolderReportDataList(
        allReceiptList: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            folderReceiptsUseCase.createDataForReport(allReceiptList)?.let { response ->
                folderReportCreator.getFolderDataList(
                    consumerNamesList = response.consumerNamesList,
                    receiptWithOrdersDataSplitList = response.receiptWithOrdersList
                ).run {
                    setFolderReportDataList(this)
                }
            } ?: setUiMessageIntent(ShowReportsUiMessageIntent.InternalError)
        }
    }
}

interface ShowReportsUiState : BasicUiState {
    object Show : ShowReportsUiState
    object Loading : ShowReportsUiState
}

interface ShowReportsIntent : BasicIntent
sealed interface ShowReportsEvent : BasicEvent {
    class SetInitialData(val receiptDataList: List<ReceiptData>) : ShowReportsEvent
}

interface ShowReportsUiMessageIntent : BasicUiMessageIntent {
    object InternalError : ShowReportsUiMessageIntent
}