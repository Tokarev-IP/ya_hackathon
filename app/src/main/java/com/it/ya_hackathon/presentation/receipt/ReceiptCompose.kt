package com.it.ya_hackathon.presentation.receipt

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.it.ya_hackathon.presentation.screens.all_receipts.AllReceiptsEvent
import com.it.ya_hackathon.presentation.screens.all_receipts.AllReceiptsScreen
import com.it.ya_hackathon.presentation.screens.all_receipts.AllReceiptsViewModel
import com.it.ya_hackathon.presentation.screens.create_receipt.CreateReceiptScreen
import com.it.ya_hackathon.presentation.screens.create_receipt.CreateReceiptViewModel
import com.it.ya_hackathon.presentation.screens.edit_receipt.EditReceiptEvent
import com.it.ya_hackathon.presentation.screens.edit_receipt.EditReceiptScreen
import com.it.ya_hackathon.presentation.screens.edit_receipt.EditReceiptViewModel
import com.it.ya_hackathon.presentation.screens.folder_receipts.FolderReceiptScreen
import com.it.ya_hackathon.presentation.screens.folder_receipts.FolderReceiptsEvent
import com.it.ya_hackathon.presentation.screens.folder_receipts.FolderReceiptsViewModel
import com.it.ya_hackathon.presentation.screens.show_reports.ShowReportsEvent
import com.it.ya_hackathon.presentation.screens.show_reports.ShowReportsScreen
import com.it.ya_hackathon.presentation.screens.show_reports.ShowReportsViewModel
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.SplitReceiptForAllEvents
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.SplitReceiptForAllScreen
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.SplitReceiptForAllViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCompose(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: ReceiptNavHostDestinations = ReceiptNavHostDestinations.AllReceiptsScreenNav,
    context: Context = LocalContext.current,
) {
    LaunchedEffect(key1 = Unit) {
        receiptViewModel.getIntentFlow().collect { receiptIntent ->
            handleReceiptIntent(
                intent = receiptIntent,
                navHostController = navHostController,
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        receiptViewModel.getUiMessageIntentFlow().collect { uiMessageIntent ->
            when (uiMessageIntent) {
                is ReceiptUiMessageIntent.UiMessage -> {
                    Toast.makeText(
                        context,
                        uiMessageIntent.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = startDestination,
    ) {
        composable<ReceiptNavHostDestinations.CreateReceiptScreenNav> { backStackEntry ->
            val createReceiptViewModel: CreateReceiptViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.CreateReceiptScreenNav>()
            CreateReceiptScreen(
                receiptViewModel = receiptViewModel,
                createReceiptViewModel = createReceiptViewModel,
                folderId = data.folderId,
            )
        }

        composable<ReceiptNavHostDestinations.AllReceiptsScreenNav> {
            val allReceiptsViewModel: AllReceiptsViewModel = koinViewModel()
            LaunchedEffect(key1 = Unit) {
                allReceiptsViewModel.setEvent(AllReceiptsEvent.RetrieveAllData)
            }
            AllReceiptsScreen(
                receiptViewModel = receiptViewModel,
                allReceiptsViewModel = allReceiptsViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav> { backStackEntry ->
            val splitReceiptForAllViewModel: SplitReceiptForAllViewModel = koinViewModel()
            val data =
                backStackEntry.toRoute<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav>()
            LaunchedEffect(key1 = Unit) {
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.RetrieveReceiptData(
                        data.receiptId
                    )
                )
                splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ActivateOrderReportCreator)
            }
            SplitReceiptForAllScreen(
                receiptViewModel = receiptViewModel,
                splitReceiptForAllViewModel = splitReceiptForAllViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.EditReceiptScreenNav> { backStackEntry ->
            val editReceiptViewModel: EditReceiptViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.EditReceiptScreenNav>()
            LaunchedEffect(key1 = data.receiptId) {
                editReceiptViewModel.setEvent(EditReceiptEvent.RetrieveReceiptData(data.receiptId))
            }
            EditReceiptScreen(
                receiptViewModel = receiptViewModel,
                editReceiptViewModel = editReceiptViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.FolderReceiptsScreenNav> { backStackEntry ->
            val folderReceiptsViewModel: FolderReceiptsViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.FolderReceiptsScreenNav>()
            LaunchedEffect(key1 = data.folderId) {
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.RetrieveAllReceiptsForSpecificFolder(data.folderId)
                )
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.RetrieveFolderData(data.folderId)
                )
            }
            FolderReceiptScreen(
                receiptViewModel = receiptViewModel,
                folderReceiptsViewModel = folderReceiptsViewModel,
                folderId = data.folderId,
                folderName = data.folderName,
            )
        }

        composable<ReceiptNavHostDestinations.ShowReportsScreenNav> { backStackEntry ->
            val showReportsViewModel: ShowReportsViewModel = koinViewModel()
            showReportsViewModel.setEvent(
                ShowReportsEvent.SetInitialData(
                    receiptDataList = receiptViewModel.getFolderReportDataList()
                )
            )
            ShowReportsScreen(
                receiptViewModel = receiptViewModel,
                showReceiptsViewModel = showReportsViewModel,
            )
        }
    }
}

private fun handleReceiptIntent(
    intent: ReceiptIntent,
    navHostController: NavHostController,
) {
    when (intent) {
        is ReceiptIntent.GoToSplitReceiptForAllScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.SplitReceiptForAllScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.EditReceiptScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.GoToCreateReceiptScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.CreateReceiptScreenNav(folderId = null))
        }

        is ReceiptIntent.GoToEditReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.NewReceiptIsCreated -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.CreateReceiptScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.GoToAllReceiptsScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.AllReceiptsScreenNav)
        }

        is ReceiptIntent.GoBackNavigation -> {
            navHostController.popBackStack()
        }

        is ReceiptIntent.GoToCreateReceiptScreenFromFolder -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.CreateReceiptScreenNav(folderId = intent.folderId)
            )
        }

        is ReceiptIntent.GoToFolderReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.FolderReceiptsScreenNav(
                    folderId = intent.folderId,
                    folderName = intent.folderName,
                )
            )
        }

        is ReceiptIntent.GoToShowReportsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.ShowReportsScreenNav
            )
        }
    }
}

sealed interface ReceiptNavHostDestinations {
    @Serializable
    class CreateReceiptScreenNav(val folderId: Long?) : ReceiptNavHostDestinations

    @Serializable
    object AllReceiptsScreenNav : ReceiptNavHostDestinations

    @Serializable
    class SplitReceiptForAllScreenNav(val receiptId: Long) : ReceiptNavHostDestinations

    @Serializable
    class EditReceiptScreenNav(val receiptId: Long) : ReceiptNavHostDestinations

    @Serializable
    class FolderReceiptsScreenNav(val folderId: Long, val folderName: String) :
        ReceiptNavHostDestinations

    @Serializable
    object ShowReportsScreenNav: ReceiptNavHostDestinations
}