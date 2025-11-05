package com.it.ya_hackathon.presentation.screens.split_receipt_for_all

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.icons.SaveIcon
import com.it.ya_hackathon.domain.report.OrderPdfReportCreatorInterface
import com.it.ya_hackathon.domain.report.OrderTextReportCreatorInterface
import com.it.ya_hackathon.presentation.basic.BackNavigationButton
import com.it.ya_hackathon.presentation.basic.showInternalErrorToast
import com.it.ya_hackathon.presentation.dialogs.AcceptClearingDialog
import com.it.ya_hackathon.presentation.receipt.ReceiptEvent
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import com.it.ya_hackathon.presentation.sheets.AddConsumerNameBottomSheet
import com.it.ya_hackathon.presentation.sheets.SelectInitialConsumerNamesBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SplitReceiptForAllScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    splitReceiptForAllViewModel: SplitReceiptForAllViewModel,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    localContext: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    orderPdfReportCreator: OrderPdfReportCreatorInterface = koinInject<OrderPdfReportCreatorInterface>(),
    orderTextReportCreator: OrderTextReportCreatorInterface = koinInject<OrderTextReportCreatorInterface>(),
) {
    var doesDataNeedToSave by rememberSaveable { mutableStateOf(false) }
    var wasDataSaved by rememberSaveable { mutableStateOf(false) }

    val uiState by splitReceiptForAllViewModel.getUiStateFlow().collectAsStateWithLifecycle()
    val receiptData by splitReceiptForAllViewModel.getReceiptData()
        .collectAsStateWithLifecycle()
    val orderDataSplitList by splitReceiptForAllViewModel.getOrderDataSplitList()
        .collectAsStateWithLifecycle()
    val allConsumerNamesList by splitReceiptForAllViewModel.getAllConsumerNamesList()
        .collectAsStateWithLifecycle()
    val isCheckStateExisted by splitReceiptForAllViewModel.getIsCheckStateExisted()
        .collectAsStateWithLifecycle()
    val receiptReportDataList by splitReceiptForAllViewModel.getReceiptReportDataList()
        .collectAsStateWithLifecycle()

    val folderConsumerNamesList = splitReceiptForAllViewModel.getFolderConsumerNamesList()

    var isShownSelectConsumerNamesBottomSheet by rememberSaveable { mutableStateOf(false) }
    var isShownClearOrderReportDialog by rememberSaveable { mutableStateOf(false) }
    var isShownAddConsumerNameBottomSheet by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    val subtotalText = stringResource(R.string.subtotal_text)
    val discountText = stringResource(R.string.discount_text)
    val tipText = stringResource(R.string.tip_text)
    val taxText = stringResource(R.string.tax_text)
    val totalText = stringResource(R.string.total_text)

    val createPdfFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            localContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                orderPdfReportCreator.generatePdfOrderReportForAll(
                    outputStream = outputStream,
                    receiptReportDataList = receiptReportDataList,
                    subtotalText = subtotalText,
                    discountText = discountText,
                    tipText = tipText,
                    taxText = taxText,
                    totalText = totalText,
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        splitReceiptForAllViewModel.getUiMessageIntentFlow().collect { uiMessageState ->
            when (uiMessageState) {
                is SplitReceiptForAllUiMessageIntent.InternalError -> {
                    showInternalErrorToast(localContext)
                }

                is SplitReceiptForAllUiMessageIntent.DataWasSaved -> {
                    scope.launch {
                        wasDataSaved = true
                        delay(1000)
                        doesDataNeedToSave = false
                        delay(300)
                        wasDataSaved = false
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        maxLines = ONE_LINE,
                        text = stringResource(R.string.split_the_receipt_for_all),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                },
                actions = {
                    TopAppBarSplitReceipt(
                        onEditReceiptClick = {
                            receiptData?.id?.let { id ->
                                receiptViewModel.setEvent(
                                    ReceiptEvent.OpenEditReceiptsScreen(receiptId = id)
                                )
                            }
                        },
                        onClearReportClick = { isShownClearOrderReportDialog = true },
                    )
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isCheckStateExisted == true,
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { isShownSelectConsumerNamesBottomSheet = true },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.set_consumer_name_button),
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier.padding(16.dp),
                visible = isCheckStateExisted == false && doesDataNeedToSave,
            ) {
                FloatingActionButton(
                    onClick = {
                        if (wasDataSaved == false)
                            splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.SaveReceiptAndOrderDataSplit)
                    }
                ) {
                    AnimatedContent(
                        targetState = wasDataSaved
                    ) { wasDataSaved ->
                        if (wasDataSaved)
                            Icon(Icons.Filled.Check, stringResource(R.string.save_button))
                        else
                            Icon(Icons.Filled.SaveIcon, stringResource(R.string.save_button))
                    }
                }
            }

        }
    ) { innerPadding ->
        SplitReceiptForAllScreenView(
            modifier = modifier.padding(innerPadding),
            receiptData = receiptData,
            orderDataSplitList = orderDataSplitList,
            onShareOrderReportClick = {
                receiptData?.let { receipt ->
                    splitReceiptForAllViewModel.setEvent(
                        SplitReceiptForAllEvents.SetIsSharedStateForReceipt(
                            receiptData = receipt
                        )
                    )
                }

                val textReportForAll = orderTextReportCreator.buildTextOrderReportForAll(
                    reportDataList = receiptReportDataList,
                    subtotalText = subtotalText,
                    discountText = discountText,
                    tipText = tipText,
                    taxText = taxText,
                    totalText = totalText,
                )

                textReportForAll?.let { textReport ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, textReport)
                    }
                    launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                } ?: showInternalErrorToast(localContext)
            },
            onCheckStateChange = { state, position ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.SetCheckState(
                        position = position,
                        state = state,
                    )
                )
            },
            onRemoveConsumerNameClick = { position, consumerName ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.ClearConsumerNameForOrder(
                        position = position,
                        name = consumerName,
                    )
                )
                doesDataNeedToSave = true
            },
            onClearAllConsumerNamesClick = { position ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.ClearAllConsumerNames(position = position)
                )
                doesDataNeedToSave = true
            },
            onAddConsumerNameForSpecificOrderClick = { position, name ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.AddConsumerNameForSpecificOrder(
                        position = position,
                        name = name,
                    )
                )
                doesDataNeedToSave = true
            },
            allConsumerNamesList = allConsumerNamesList,
            onEditReceiptConsumerNamesClick = {
                isShownAddConsumerNameBottomSheet = true
            },
            uiState = uiState,
            onSaveAsPdf = {
                createPdfFileLauncher.launch("report.pdf")
            },
            receiptReportDataList = receiptReportDataList,
        )
    }

    if (isShownSelectConsumerNamesBottomSheet) {
        SelectInitialConsumerNamesBottomSheet(
            allConsumerNamesList = allConsumerNamesList,
            onDismissClick = { isShownSelectConsumerNamesBottomSheet = false },
            onSetSelectedNamesClick = { names ->
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.SetInitialConsumerNamesForCheckedOrders(
                        consumerNamesList = names
                    )
                )
                doesDataNeedToSave = true
                isShownSelectConsumerNamesBottomSheet = false
            },
        )
    }

    if (isShownClearOrderReportDialog) {
        AcceptClearingDialog(
            onDismissRequest = { isShownClearOrderReportDialog = false },
            onClearClicked = {
                splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ClearOrderReport)
                isShownClearOrderReportDialog = false
                doesDataNeedToSave = true
            },
            infoText = stringResource(R.string.clear_report_text),
        )
    }

    if (isShownAddConsumerNameBottomSheet) {
        receiptData?.let { receipt ->
            AddConsumerNameBottomSheet(
                folderConsumerNamesList = folderConsumerNamesList,
                receiptConsumerNamesList = receipt.consumerNamesList,
                allConsumerNamesList = allConsumerNamesList,
                onAddConsumerNameClick = { name ->
                    splitReceiptForAllViewModel.setEvent(
                        SplitReceiptForAllEvents.AddConsumerNameToReceipt(name)
                    )
                },
                onRemoveConsumerNameClick = { name ->
                    splitReceiptForAllViewModel.setEvent(
                        SplitReceiptForAllEvents.RemoveConsumerNameFromReceipt(name)
                    )
                },
                onDismissRequest = {
                    isShownAddConsumerNameBottomSheet = false
                }
            )
        }
    }
}

private const val ONE_LINE = 1