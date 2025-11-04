package com.it.ya_hackathon.presentation.screens.show_reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.icons.BasicTextIcon
import com.it.ya_hackathon.basic.icons.LongTextIcon
import com.it.ya_hackathon.basic.icons.ShortTextIcon
import com.it.ya_hackathon.domain.report.FolderPdfReportCreatorInterface
import com.it.ya_hackathon.domain.report.FolderTextReportCreatorInterface
import com.it.ya_hackathon.presentation.basic.BackNavigationButton
import com.it.ya_hackathon.presentation.basic.showInternalErrorToast
import com.it.ya_hackathon.presentation.receipt.ReceiptEvent
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShowReportsScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    showReceiptsViewModel: ShowReportsViewModel,
    localContext: Context = LocalContext.current,
    pdfReportCreator: FolderPdfReportCreatorInterface = koinInject<FolderPdfReportCreatorInterface>(),
    textReportCreator: FolderTextReportCreatorInterface = koinInject<FolderTextReportCreatorInterface>()
) {
    val subtotalText = stringResource(R.string.subtotal_text)
    val discountText = stringResource(R.string.discount_text)
    val tipText = stringResource(R.string.tip_text)
    val taxText = stringResource(R.string.tax_text)
    val totalText = stringResource(R.string.total_text)

    val folderReportDataList by showReceiptsViewModel.getFolderReportDataListState()
        .collectAsStateWithLifecycle()

    val uiState by showReceiptsViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        showReceiptsViewModel.getUiMessageIntentFlow().collectLatest { intent ->
            when (intent) {
                is ShowReportsUiMessageIntent.InternalError -> {
                    showInternalErrorToast(localContext)
                }
            }
        }
    }

    var selectedItem: ReportItem by remember { mutableStateOf(ReportItem.SHORT_REPORT) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            localContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                when (selectedItem) {
                    ReportItem.SHORT_REPORT -> {
                        pdfReportCreator.generateShortReportPdf(
                            folderReportDataList = folderReportDataList,
                            outputStream = outputStream,
                            totalText = totalText,
                        )
                    }
                    ReportItem.BASIC_REPORT -> {
                        pdfReportCreator.generateBasicReportPdf(
                            folderReportDataList = folderReportDataList,
                            outputStream = outputStream,
                            totalText = totalText,
                        )
                    }
                    ReportItem.LONG_REPORT -> {
                        pdfReportCreator.generateLongReportPdf(
                            folderReportDataList = folderReportDataList,
                            outputStream = outputStream,
                            subtotalText = subtotalText,
                            discountText = discountText,
                            tipText = tipText,
                            taxText = taxText,
                            totalText = totalText,
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = selectedItem.appBarTitle())
                },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                }
            )
        },
        bottomBar = {
            ReportsNavigationBar(
                selectedItem = selectedItem,
                onSelectedItemChange = { item: ReportItem ->
                    selectedItem = item
                }
            )
        }
    ) { innerPadding ->
        ShowReportsScreenView(
            modifier = modifier.padding(innerPadding),
            folderReportDataList = folderReportDataList,
            onShareClicked = {
                var orderReportText: String?

                when (selectedItem) {
                    ReportItem.SHORT_REPORT -> {
                        orderReportText = textReportCreator.createShortTextReport(
                            folderReportDataList = folderReportDataList,
                            totalText = totalText,
                        )
                    }

                    ReportItem.BASIC_REPORT -> {
                        orderReportText = textReportCreator.createBasicTextReport(
                            folderReportDataList = folderReportDataList,
                            totalText = totalText,
                        )
                    }

                    ReportItem.LONG_REPORT -> {
                        orderReportText = textReportCreator.createLongTextReport(
                            folderReportDataList = folderReportDataList,
                            subTotalText = subtotalText,
                            discountText = discountText,
                            tipText = tipText,
                            taxText = taxText,
                            totalText = totalText,
                        )
                    }
                }
                orderReportText?.let { report ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, report)
                    }
                    launcher.launch(Intent.createChooser(shareIntent, "Share order report"))
                }
            },
            onPdfSaveClicked = {
                createPdfLauncher.launch("report.pdf")
            },
            uiState = uiState,
            selectedReportItem = selectedItem,
        )
    }
}

internal enum class ReportItem(
    val appBarTitle: @Composable () -> String,
    val icon: @Composable () -> Unit,
) {
    SHORT_REPORT(
        appBarTitle = { stringResource(R.string.short_report) },
        icon = {
            Icon(
                Icons.Filled.ShortTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
    ),
    BASIC_REPORT(
        appBarTitle = { stringResource(R.string.basic_report) },
        icon = {
            Icon(
                Icons.Filled.BasicTextIcon,
                stringResource(R.string.basic_report_icon)
            )
        },
    ),
    LONG_REPORT(
        appBarTitle = { stringResource(R.string.long_report) },
        icon = {
            Icon(
                Icons.Filled.LongTextIcon,
                stringResource(R.string.short_report_icon)
            )
        },
    )
}

@Composable
private fun ReportsNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: ReportItem,
    onSelectedItemChange: (ReportItem) -> Unit,
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            selected = selectedItem == ReportItem.SHORT_REPORT,
            onClick = { onSelectedItemChange(ReportItem.SHORT_REPORT) },
            label = { Text(ReportItem.SHORT_REPORT.appBarTitle()) },
            icon = { ReportItem.SHORT_REPORT.icon() }
        )
        NavigationBarItem(
            selected = selectedItem == ReportItem.BASIC_REPORT,
            onClick = { onSelectedItemChange(ReportItem.BASIC_REPORT) },
            label = { Text(ReportItem.BASIC_REPORT.appBarTitle()) },
            icon = { ReportItem.BASIC_REPORT.icon() }
        )
        NavigationBarItem(
            selected = selectedItem == ReportItem.LONG_REPORT,
            onClick = { onSelectedItemChange(ReportItem.LONG_REPORT) },
            label = { Text(ReportItem.LONG_REPORT.appBarTitle()) },
            icon = { ReportItem.LONG_REPORT.icon() }
        )

    }
}