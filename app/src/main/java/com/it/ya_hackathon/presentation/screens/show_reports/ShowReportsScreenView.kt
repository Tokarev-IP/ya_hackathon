package com.it.ya_hackathon.presentation.screens.show_reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.shimmerBrush
import com.it.ya_hackathon.presentation.receipt.FolderReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.OrderReportItemView
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.ReceiptDiscountTaxTipView

@Composable
internal fun ShowReportsScreenView(
    modifier: Modifier = Modifier,
    folderReportDataList: List<FolderReportData>,
    onShareClicked: () -> Unit,
    onPdfSaveClicked: () -> Unit,
    uiState: ShowReportsUiState,
    selectedReportItem: ReportItem,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState) {
            is ShowReportsUiState.Loading -> ShimmedShowReportsView()
            is ShowReportsUiState.Show -> {
                ShowReportsView(
                    onShareClicked = { onShareClicked() },
                    onPdfSaveClicked = { onPdfSaveClicked() },
                    folderReportDataList = folderReportDataList,
                    selectedReportItem = selectedReportItem,
                )
            }
        }
    }
}

@Composable
private fun ShowReportsView(
    modifier: Modifier = Modifier,
    onShareClicked: () -> Unit,
    onPdfSaveClicked: () -> Unit,
    folderReportDataList: List<FolderReportData>,
    selectedReportItem: ReportItem,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(folderReportDataList.size) { index ->
            when (selectedReportItem) {
                ReportItem.SHORT_REPORT -> {
                    ShortReportItemCardView(folderReportData = folderReportDataList[index])
                }

                ReportItem.BASIC_REPORT -> {
                    BasicReportItemCardView(folderReportData = folderReportDataList[index])
                }

                ReportItem.LONG_REPORT -> {
                    LongReportItemCardView(folderReportData = folderReportDataList[index])
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            TextReportActionView(
                folderReportDataList = folderReportDataList,
                onShareClicked = { onShareClicked() },
                onPdfSaveClicked = { onPdfSaveClicked() },
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ShortReportItemCardView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        ShortReportItemView(folderReportData = folderReportData)
    }
}

@Composable
private fun BasicReportItemCardView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        BasicReportItemView(folderReportData = folderReportData)
    }
}

@Composable
private fun LongReportItemCardView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        LongReportItemView(folderReportData = folderReportData)
    }
}

@Composable
private fun ShortReportItemView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                modifier = modifier.weight(1F),
                text = folderReportData.consumerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1F),
                text = stringResource(
                    R.string.total_is,
                    folderReportData.totalSum
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
            )

        }
    }
}

@Composable
private fun BasicReportItemView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                modifier = modifier.weight(1F),
                text = folderReportData.consumerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1F),
                text = stringResource(
                    R.string.total_is,
                    folderReportData.totalSum
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
            )

        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(4.dp))

        repeat(folderReportData.receiptList.size) { index ->
            val receiptReportData: ReceiptReportData = folderReportData.receiptList[index]

            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
            ) {
                BasicReportReceiptItemView(receiptReportData = receiptReportData)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LongReportItemView(
    modifier: Modifier = Modifier,
    folderReportData: FolderReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                modifier = modifier.weight(1F),
                text = folderReportData.consumerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1F),
                text = stringResource(
                    R.string.total_is,
                    folderReportData.totalSum
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
            )

        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(4.dp))

        repeat(folderReportData.receiptList.size) { index ->
            val receiptReportData: ReceiptReportData = folderReportData.receiptList[index]

            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
            ) {
                LongReportReceiptItemView(receiptReportData = receiptReportData)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BasicReportReceiptItemView(
    modifier: Modifier = Modifier,
    receiptReportData: ReceiptReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = modifier.weight(1F),
                text = receiptReportData.receiptName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1F),
                text = stringResource(
                    R.string.total_is,
                    receiptReportData.totalSum ?: receiptReportData.subtotalSum
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            receiptReportData.translatedReceiptName?.let { translatedName ->
                Text(
                    modifier = modifier.weight(1F),
                    text = translatedName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                )
            }
            Text(
                modifier = modifier.weight(1F),
                text = receiptReportData.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun LongReportReceiptItemView(
    modifier: Modifier = Modifier,
    receiptReportData: ReceiptReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = modifier.weight(1F),
                text = receiptReportData.receiptName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1F),
                text = stringResource(
                    R.string.total_is,
                    receiptReportData.totalSum ?: receiptReportData.subtotalSum
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
            )
        }
        Row(modifier = modifier.fillMaxWidth()) {
            receiptReportData.translatedReceiptName?.let { translatedName ->
                Text(
                    modifier = modifier.weight(1F),
                    text = translatedName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                )
            }
            Text(
                modifier = modifier.weight(1F),
                text = receiptReportData.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(4.dp))

        repeat(receiptReportData.orderList.size) { index ->
            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
            ) {
                OrderReportItemView(orderReportData = receiptReportData.orderList[index])
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        if (receiptReportData.discount.isNotZero() || receiptReportData.tax.isNotZero() || receiptReportData.tip.isNotZero()) {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            ReceiptDiscountTaxTipView(receiptReportData = receiptReportData)
        }
    }
}

@Composable
private fun TextReportActionView(
    modifier: Modifier = Modifier,
    folderReportDataList: List<FolderReportData>,
    onShareClicked: () -> Unit,
    onPdfSaveClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (folderReportDataList.isEmpty())
            Text(
                text = stringResource(R.string.order_report_is_empty),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )

        TextButton(
            onClick = { onShareClicked() },
            enabled = folderReportDataList.isNotEmpty(),
        ) {
            Text(
                text = stringResource(R.string.share_as_text),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
        }

        TextButton(
            onClick = { onPdfSaveClicked() },
            enabled = folderReportDataList.isNotEmpty(),
        ) {
            Text(
                text = stringResource(R.string.save_as_pdf),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
private fun ShimmedShowReportsView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(6) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}