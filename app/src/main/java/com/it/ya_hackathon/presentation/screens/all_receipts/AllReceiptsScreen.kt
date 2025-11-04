package com.it.ya_hackathon.presentation.screens.all_receipts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.getOrZero
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_RECEIPTS
import com.it.ya_hackathon.presentation.dialogs.AcceptDeletionDialog
import com.it.ya_hackathon.presentation.dialogs.AcceptRemovingDialog
import com.it.ya_hackathon.presentation.dialogs.AddNewFolderDialog
import com.it.ya_hackathon.presentation.dialogs.EditFolderDialog
import com.it.ya_hackathon.presentation.receipt.ReceiptEvent
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import com.it.ya_hackathon.presentation.sheets.ChooseFolderBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReceiptsScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    allReceiptsViewModel: AllReceiptsViewModel,
) {
    var showAcceptDeletionReceiptDialog by rememberSaveable { mutableStateOf(false) }
    var showAddNewFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showEditFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showChooseFolderNameBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showAcceptDeletionFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveReceiptFromFolderDialog by rememberSaveable { mutableStateOf(false) }

    val allReceiptsWithFolderList by allReceiptsViewModel.getAllReceiptsWithFolderList()
        .collectAsStateWithLifecycle()
    val foldersWithReceiptsUnarchived by allReceiptsViewModel.getFoldersWithReceiptsUnarchived()
        .collectAsStateWithLifecycle()
    val foldersWithReceiptsArchived by allReceiptsViewModel.getFoldersWithReceiptsArchived()
        .collectAsStateWithLifecycle()
    val allFoldersNamesList by allReceiptsViewModel.getAllFoldersNamesList()
        .collectAsStateWithLifecycle()

    var chosenReceiptIdToDelete: Long? = null
    var chosenFolderIdToEdit: Long? = null
    var chosenReceiptIdToMove: Long? = null
    var chosenFolderIdToDelete: Long? = null
    var chosenReceiptIdToRemoveFromFolder: Long? = null

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = allReceiptsWithFolderList?.size.getOrZero() < MAXIMUM_AMOUNT_OF_RECEIPTS
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { receiptViewModel.setEvent(ReceiptEvent.OpenCreateReceiptScreen) },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_new_receipt_button)
                    )
                }
            }
        }
    ) { innerPadding ->
        AllReceiptsScreenView(
            modifier = modifier.padding(innerPadding),
            allReceiptsWithFolder = allReceiptsWithFolderList,
            onReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenSplitReceiptForAllScreen(receiptId = receiptId)
                )
            },
            onDeleteReceiptClicked = { receiptId ->
                chosenReceiptIdToDelete = receiptId
                showAcceptDeletionReceiptDialog = true
            },
            onEditReceiptClicked = { receiptId ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenEditReceiptsScreen(
                        receiptId = receiptId
                    )
                )
            },
            foldersWithReceiptsUnarchived = foldersWithReceiptsUnarchived,
            foldersWithReceiptsArchived = foldersWithReceiptsArchived,
            onAddNewFolderClicked = { showAddNewFolderDialog = true },
            onEditFolderClicked = { folderId ->
                chosenFolderIdToEdit = folderId
                showEditFolderDialog = true
            },
            onArchiveFolderClicked = { folderData ->
                allReceiptsViewModel.setEvent(AllReceiptsEvent.ArchiveFolder(folderData = folderData))
            },
            onUnarchiveFolderClicked = { folderData ->
                allReceiptsViewModel.setEvent(AllReceiptsEvent.UnArchiveFolder(folderData = folderData))
            },
            onFolderClick = { folderData ->
                receiptViewModel.setEvent(
                    ReceiptEvent.OpenFolderReceiptsScreen(
                        folderId = folderData.id,
                        folderName = folderData.folderName,
                    )
                )
            },
            onMoveReceiptToClicked = { receiptId ->
                chosenReceiptIdToMove = receiptId
                showChooseFolderNameBottomSheet = true
            },
            onDeleteFolderClicked = { folderId ->
                chosenFolderIdToDelete = folderId
                showAcceptDeletionFolderDialog = true
            },
            onRemoveReceiptFromFolderClicked = { receiptId ->
                chosenReceiptIdToRemoveFromFolder = receiptId
                showRemoveReceiptFromFolderDialog = true
            },
        )

        if (showAcceptDeletionReceiptDialog)
            chosenReceiptIdToDelete?.let { receiptId ->
                AcceptDeletionDialog(
                    infoText = stringResource(R.string.do_you_want_to_delete_this_receipt),
                    onDismissRequest = { showAcceptDeletionReceiptDialog = false },
                    onDeleteClicked = {
                        allReceiptsViewModel.setEvent(
                            AllReceiptsEvent.DeleteSpecificReceipt(
                                receiptId
                            )
                        )
                        chosenReceiptIdToDelete = null
                        showAcceptDeletionReceiptDialog = false
                    }
                )
            } ?: run { showAcceptDeletionReceiptDialog = false }

        if (showEditFolderDialog) {
            chosenFolderIdToEdit?.let { folderId ->
                val folderData = foldersWithReceiptsUnarchived?.find { it.folder.id == folderId }
                    ?: foldersWithReceiptsArchived?.find { it.folder.id == folderId }

                folderData?.let { data ->
                    EditFolderDialog(
                        onDismissRequest = {
                            showEditFolderDialog = false
                            chosenFolderIdToEdit = null
                        },
                        folderData = data.folder,
                        onSaveButtonClicked = { folderData ->
                            allReceiptsViewModel.setEvent(
                                AllReceiptsEvent.SaveFolder(folderData = folderData)
                            )
                            chosenFolderIdToEdit = null
                            showEditFolderDialog = false
                        },
                        allFoldersName = allFoldersNamesList ?: emptyList(),
                    )
                } ?: run { showEditFolderDialog = false }
            }
        }

        if (showAddNewFolderDialog) {
            AddNewFolderDialog(
                onDismissRequest = { showAddNewFolderDialog = false },
                onSaveButtonClicked = { folderData ->
                    allReceiptsViewModel.setEvent(
                        AllReceiptsEvent.SaveFolder(folderData = folderData)
                    )
                    showAddNewFolderDialog = false
                },
                allFoldersName = allFoldersNamesList ?: emptyList()
            )
        }

        if (showChooseFolderNameBottomSheet) {
            chosenReceiptIdToMove?.let { receiptId ->
                val receiptData = allReceiptsWithFolderList?.find { it.receipt.id == receiptId }
                receiptData?.let {
                    ChooseFolderBottomSheet(
                        onDismissRequest = {
                            showChooseFolderNameBottomSheet = false
                            chosenReceiptIdToMove = null
                        },
                        unarchivedFoldersWithReceipts = foldersWithReceiptsUnarchived
                            ?: emptyList(),
                        onFolderIsChosen = { folderId ->
                            chosenReceiptIdToMove = null
                            showChooseFolderNameBottomSheet = false
                            allReceiptsViewModel.setEvent(
                                AllReceiptsEvent.MoveReceiptInFolder(
                                    receiptData = receiptData.receipt,
                                    folderId = folderId
                                )
                            )
                        },
                    )
                } ?: run { showChooseFolderNameBottomSheet = false }
            }
        }

        if (showAcceptDeletionFolderDialog) {
            chosenFolderIdToDelete?.let { folderId ->
                AcceptDeletionDialog(
                    onDismissRequest = {
                        showAcceptDeletionFolderDialog = false
                        chosenFolderIdToDelete = null
                    },
                    onDeleteClicked = {
                        allReceiptsViewModel.setEvent(
                            AllReceiptsEvent.DeleteSpecificFolder(folderId = folderId)
                        )
                        showAcceptDeletionFolderDialog = false
                        chosenFolderIdToDelete = null
                    },
                    infoText = stringResource(R.string.do_you_want_to_delete_this_folder)
                )
            } ?: run { showAcceptDeletionFolderDialog = false }
        }

        if (showRemoveReceiptFromFolderDialog) {
            val receiptToRemoveFromFolder = allReceiptsWithFolderList?.find {
                it.receipt.id == chosenReceiptIdToRemoveFromFolder
            }
            receiptToRemoveFromFolder?.let { receiptData ->
                AcceptRemovingDialog(
                    onDismissRequest = {
                        showRemoveReceiptFromFolderDialog = false
                        chosenReceiptIdToRemoveFromFolder = null
                    },
                    onRemoveClicked = {
                        allReceiptsViewModel.setEvent(
                            AllReceiptsEvent.MoveReceiptOutFolder(receiptData = receiptToRemoveFromFolder.receipt)
                        )
                        showRemoveReceiptFromFolderDialog = false
                        chosenReceiptIdToRemoveFromFolder = null
                    },
                    infoText = stringResource(R.string.do_you_want_to_remove_this_receipt_from)
                )
            }
        }
    }
}