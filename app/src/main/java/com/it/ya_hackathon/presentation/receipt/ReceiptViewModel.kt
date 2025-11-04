package com.it.ya_hackathon.presentation.receipt

import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicIntent
import com.it.ya_hackathon.basic.BasicUiMessageIntent
import com.it.ya_hackathon.basic.BasicUiState
import com.it.ya_hackathon.basic.BasicViewModel
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoBackNavigation
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToAllReceiptsScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToCreateReceiptScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToCreateReceiptScreenFromFolder
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToEditReceiptsScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToFolderReceiptsScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToShowReportsScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.GoToSplitReceiptForAllScreen
import com.it.ya_hackathon.presentation.receipt.ReceiptIntent.NewReceiptIsCreated
import com.it.ya_hackathon.presentation.receipt.ReceiptUiMessageIntent.UiMessage

class ReceiptViewModel() : BasicViewModel<
        ReceiptUiState,
        ReceiptIntent,
        ReceiptEvent,
        ReceiptUiMessageIntent>(
    initialUiState = ReceiptUiState.Show,
) {
    private var folderReportDataList: List<ReceiptData> = emptyList()
    fun getFolderReportDataList() = folderReportDataList

    override fun setEvent(newEvent: ReceiptEvent) {
        when (newEvent) {
            is ReceiptEvent.OpenSplitReceiptForAllScreen -> {
                setIntent(GoToSplitReceiptForAllScreen(receiptId = newEvent.receiptId))
            }

            is ReceiptEvent.OpenCreateReceiptScreen -> {
                setIntent(GoToCreateReceiptScreen)
            }

            is ReceiptEvent.OpenEditReceiptsScreen -> {
                setIntent(GoToEditReceiptsScreen(newEvent.receiptId))
            }

            is ReceiptEvent.OpenAllReceiptsScreen -> {
                setIntent(GoToAllReceiptsScreen)
            }

            is ReceiptEvent.GoBack -> {
                setIntent(GoBackNavigation)
            }

            is ReceiptEvent.NewReceiptIsCreated -> {
                setIntent(NewReceiptIsCreated(newEvent.receiptId))
            }

            is ReceiptEvent.SetUiMessage -> {
                setUiMessageIntent(UiMessage(newEvent.message))
            }

            is ReceiptEvent.OpenCreateReceiptScreenFromFolder -> {
                setIntent(GoToCreateReceiptScreenFromFolder(newEvent.folderId))
            }

            is ReceiptEvent.OpenFolderReceiptsScreen -> {
                setIntent(
                    GoToFolderReceiptsScreen(
                        newEvent.folderId,
                        newEvent.folderName
                    )
                )
            }

            is ReceiptEvent.OpenShowReportsScreen -> {
                setIntent(GoToShowReportsScreen)
            }

            is ReceiptEvent.SetReceiptListForReports -> {
                folderReportDataList = newEvent.receiptDataList
            }
        }
    }
}

interface ReceiptUiState : BasicUiState {
    object Show : ReceiptUiState
}

sealed interface ReceiptEvent : BasicEvent {
    class OpenSplitReceiptForAllScreen(val receiptId: Long) : ReceiptEvent
    object OpenCreateReceiptScreen : ReceiptEvent
    class OpenEditReceiptsScreen(val receiptId: Long) : ReceiptEvent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptEvent
    object OpenAllReceiptsScreen : ReceiptEvent
    object GoBack : ReceiptEvent
    class SetUiMessage(val message: String) : ReceiptEvent
    class OpenCreateReceiptScreenFromFolder(val folderId: Long?) : ReceiptEvent
    class OpenFolderReceiptsScreen(val folderId: Long, val folderName: String) : ReceiptEvent
    object OpenShowReportsScreen : ReceiptEvent
    class SetReceiptListForReports(val receiptDataList: List<ReceiptData>) : ReceiptEvent
}

interface ReceiptIntent : BasicIntent {
    class GoToSplitReceiptForAllScreen(val receiptId: Long) : ReceiptIntent
    object GoToCreateReceiptScreen : ReceiptIntent
    class GoToEditReceiptsScreen(val receiptId: Long) : ReceiptIntent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptIntent
    object GoToAllReceiptsScreen : ReceiptIntent
    object GoBackNavigation : ReceiptIntent
    class GoToCreateReceiptScreenFromFolder(val folderId: Long?) : ReceiptIntent
    class GoToFolderReceiptsScreen(val folderId: Long, val folderName: String) : ReceiptIntent
    object GoToShowReportsScreen : ReceiptIntent
}

interface ReceiptUiMessageIntent : BasicUiMessageIntent {
    class UiMessage(val message: String) : ReceiptUiMessageIntent
}

enum class ReceiptUiMessage(val msg: String) {
    INTERNAL_ERROR("Internal error"),
}