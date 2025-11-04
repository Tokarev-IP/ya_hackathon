package com.it.ya_hackathon.presentation.screens.split_receipt_for_all

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.isNotZero
import com.it.ya_hackathon.basic.shimmerBrush
import com.it.ya_hackathon.presentation.basic.FlowGridLayout
import com.it.ya_hackathon.presentation.basic.ReceiptInfoCardView
import com.it.ya_hackathon.presentation.receipt.OrderDataSplit
import com.it.ya_hackathon.presentation.receipt.OrderReportData
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptReportData

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SplitReceiptForAllScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataSplitList: List<OrderDataSplit>,
    onShareOrderReportClick: () -> Unit,
    onCheckStateChange: (Boolean, Int) -> Unit,
    onRemoveConsumerNameClick: (Int, String) -> Unit,
    onClearAllConsumerNamesClick: (Int) -> Unit,
    onAddConsumerNameForSpecificOrderClick: (Int, String) -> Unit,
    allConsumerNamesList: List<String>,
    onEditReceiptConsumerNamesClick: () -> Unit,
    uiState: SplitReceiptForAllUiState,
    onSaveAsPdf: () -> Unit,
    receiptReportDataList: List<ReceiptReportData>,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState) {
            is SplitReceiptForAllUiState.Show -> {
                receiptData?.let {
                    SplitReceiptForAllView(
                        receiptData = receiptData,
                        orderDataSplitList = orderDataSplitList,
                        onShareOrderReportClick = { onShareOrderReportClick() },
                        onCheckStateChange = { state, position ->
                            onCheckStateChange(state, position)
                        },
                        onRemoveConsumerNameClick = { position, consumerName ->
                            onRemoveConsumerNameClick(position, consumerName)
                        },
                        onClearAllConsumerNamesClick = { position ->
                            onClearAllConsumerNamesClick(position)
                        },
                        onAddConsumerNameForSpecificOrderClick = { position, name ->
                            onAddConsumerNameForSpecificOrderClick(position, name)
                        },
                        allConsumerNamesList = allConsumerNamesList,
                        onEditReceiptConsumerNamesClick = { onEditReceiptConsumerNamesClick() },
                        onSaveAsPdf = { onSaveAsPdf() },
                        receiptReportDataList = receiptReportDataList,
                    )
                } ?: ShimmedShowReportsScreenView()
            }

            is SplitReceiptForAllUiState.Loading -> {
                ShimmedShowReportsScreenView()
            }
        }
    }
}

@Composable
private fun SplitReceiptForAllView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataSplitList: List<OrderDataSplit>,
    onShareOrderReportClick: () -> Unit = {},
    onCheckStateChange: (Boolean, Int) -> Unit = { _, _ -> },
    onRemoveConsumerNameClick: (Int, String) -> Unit = { _, _ -> },
    onClearAllConsumerNamesClick: (Int) -> Unit = {},
    onAddConsumerNameForSpecificOrderClick: (Int, String) -> Unit = { _, _ -> },
    allConsumerNamesList: List<String> = emptyList(),
    onEditReceiptConsumerNamesClick: () -> Unit = {},
    onSaveAsPdf: () -> Unit = {},
    receiptReportDataList: List<ReceiptReportData> = emptyList(),
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            ReceiptInfoColumn(
                receiptData = receiptData,
                isOrderEmpty = orderDataSplitList.isEmpty(),
                allConsumerNamesList = allConsumerNamesList,
                onEditReceiptConsumerNamesClick = { onEditReceiptConsumerNamesClick() },
            )
        }
        items(orderDataSplitList.size) { index ->
            val orderDataCheck = orderDataSplitList[index]
            OrderDataCheckCardItem(
                orderDataSplit = orderDataCheck,
                onCheckedChange = { state ->
                    onCheckStateChange(state, index)
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(index, consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick(index) },
                onAddConsumerForSpecificOrderNameClick = { name ->
                    onAddConsumerNameForSpecificOrderClick(index, name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ReceiptReportColumn(
                receiptReportDataList = receiptReportDataList,
            )
            ReportActionsView(
                reportIsEmpty = receiptReportDataList.isEmpty(),
                onShareOrderReportClick = { onShareOrderReportClick() },
                onSaveAsPdf = { onSaveAsPdf() },
            )
        }
    }
}

@Composable
private fun ReceiptInfoColumn(
    receiptData: ReceiptData,
    modifier: Modifier = Modifier,
    isOrderEmpty: Boolean,
    allConsumerNamesList: List<String>,
    onEditReceiptConsumerNamesClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ReceiptInfoCardView(receiptData = receiptData)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        ReceiptConsumerNamesView(
            allConsumerNamesList = allConsumerNamesList,
            onEditReceiptConsumerNamesClick = { onEditReceiptConsumerNamesClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = isOrderEmpty,
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.receipt_is_empty),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ReceiptConsumerNamesView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onEditReceiptConsumerNamesClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        AnimatedContent(
            targetState = allConsumerNamesList.isEmpty(),
        ) { isEmpty ->
            if (isEmpty)
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.names_definition),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.names_are_empty_add_one),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { onEditReceiptConsumerNamesClick() }
                    ) {
                        Text(
                            text = stringResource(R.string.add_person_name)
                        )
                    }
                }
            else
                FlowGridLayout {
                    allConsumerNamesList.forEach { name ->
                        OutlinedCard {
                            Text(
                                modifier = modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                text = name,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp,
                            )
                        }
                    }
                    IconButton(
                        onClick = { onEditReceiptConsumerNamesClick() }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            stringResource(R.string.add_person_name)
                        )
                    }
                }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun OrderDataCheckCardItem(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
    onCheckedChange: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
    onAddConsumerForSpecificOrderNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (orderDataSplit.consumerNamesList.isEmpty()) {
                        Modifier.clickable { onCheckedChange(!orderDataSplit.checked) }
                    } else Modifier
                ),
        ) {
            OrderDataSplitItem(
                orderDataSplit = orderDataSplit,
                onCheckedChange = { checked ->
                    onCheckedChange(checked)
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() },
                onAddConsumerNameForSpecificOrderClick = { name ->
                    onAddConsumerForSpecificOrderNameClick(name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
        }
    }
}

@Composable
private fun OrderDataSplitItem(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
    onCheckedChange: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
    onAddConsumerNameForSpecificOrderClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    var expandConsumerNames by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = orderDataSplit.consumerNamesList) {
        if (orderDataSplit.consumerNamesList.isEmpty())
            expandConsumerNames = false
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 12.dp, bottom = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = orderDataSplit.consumerNamesList.isEmpty(),
        ) { isEmpty ->
            if (isEmpty)
                Checkbox(
                    modifier = modifier.weight(2f),
                    checked = orderDataSplit.checked,
                    onCheckedChange = { checked ->
                        onCheckedChange(checked)
                    },
                )
            else
                Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = modifier
                .weight(12f)
                .animateContentSize(),
        ) {
            OrderInfoView(
                orderDataSplit = orderDataSplit,
            )
            OrderConsumerNameView(
                consumerNamesList = orderDataSplit.consumerNamesList,
                expandConsumerNames = expandConsumerNames,
                onExpandConsumerNamesClick = { state: Boolean ->
                    expandConsumerNames = state
                },
                onRemoveConsumerNameClick = { consumerName ->
                    onRemoveConsumerNameClick(consumerName)
                },
                onClearAllConsumerNamesClick = { onClearAllConsumerNamesClick() },
                onAddConsumerNameClick = { name ->
                    onAddConsumerNameForSpecificOrderClick(name)
                },
                allConsumerNamesList = allConsumerNamesList,
            )
        }
    }
}

@Composable
private fun OrderInfoView(
    modifier: Modifier = Modifier,
    orderDataSplit: OrderDataSplit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = modifier.weight(10f),
        ) {
            Text(
                text = orderDataSplit.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_3,
            )
            orderDataSplit.translatedName?.let { translatedName ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = translatedName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_2,
                )
            }
        }
        Text(
            modifier = modifier.weight(4f),
            textAlign = TextAlign.End,
            text = orderDataSplit.price.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun OrderConsumerNameView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    expandConsumerNames: Boolean,
    onExpandConsumerNamesClick: (Boolean) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
    onClearAllConsumerNamesClick: () -> Unit,
    onAddConsumerNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    AnimatedVisibility(
        visible = consumerNamesList.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = !expandConsumerNames,
                    modifier = modifier.weight(12f),
                ) { showNames ->
                    if (showNames)
                        ConsumerNamesRowView(consumerNamesList = consumerNamesList)
                    else
                        Box {
                            TextButton(
                                modifier = modifier.align(Alignment.Center),
                                onClick = { onClearAllConsumerNamesClick() }
                            ) {
                                Text(text = stringResource(R.string.clear_all_names))
                            }
                        }
                }

                AnimatedContent(
                    targetState = expandConsumerNames,
                    modifier = modifier.weight(2f),
                ) { expand ->
                    IconButton(
                        onClick = {
                            if (expandConsumerNames)
                                onExpandConsumerNamesClick(false)
                            else
                                onExpandConsumerNamesClick(true)
                        },
                    ) {
                        if (expand)
                            Icon(
                                Icons.Outlined.KeyboardArrowUp,
                                stringResource(R.string.narrow_down_consumer_names_button)
                            )
                        else
                            Icon(
                                Icons.Outlined.KeyboardArrowDown,
                                stringResource(R.string.expand_consumer_names_button)
                            )
                    }
                }
            }

            AnimatedVisibility(
                visible = expandConsumerNames,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkOut(),
            ) {
                EditConsumerNamesView(
                    consumerNamesList = consumerNamesList,
                    onRemoveConsumerNameClick = { consumerName ->
                        onRemoveConsumerNameClick(consumerName)
                    },
                    onAddConsumerNameClick = { name ->
                        onAddConsumerNameClick(name)
                    },
                    allConsumerNamesList = allConsumerNamesList,
                )
            }
        }
    }
}

@Composable
private fun ConsumerNamesRowView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            if (consumerNamesList.joinToString(SEPARATOR).length > INFO_DISPLAY_CHAR_COUNT) {
                Text(
                    text = consumerNamesList.size.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        items(consumerNamesList.size) { index ->
            OutlinedCard {
                Text(
                    modifier = modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    text = consumerNamesList[index],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun EditConsumerNamesView(
    modifier: Modifier = Modifier,
    consumerNamesList: List<String>,
    onRemoveConsumerNameClick: (String) -> Unit,
    onAddConsumerNameClick: (String) -> Unit,
    allConsumerNamesList: List<String>,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConsumerNamesGrid(
            allConsumerNamesList = allConsumerNamesList,
            consumerNamesList = consumerNamesList,
            onChooseConsumerNameClick = { name ->
                onAddConsumerNameClick(name)
            },
            onRemoveConsumerNameClick = { name ->
                onRemoveConsumerNameClick(name)
            },
        )
    }
}

@Composable
private fun ConsumerNamesGrid(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    consumerNamesList: List<String>,
    onChooseConsumerNameClick: (String) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    FlowGridLayout {
        repeat(allConsumerNamesList.size) { index ->
            val consumerName = allConsumerNamesList[index]
            OutlinedCard(
                onClick = { onRemoveConsumerNameClick(consumerName) },
                enabled = consumerName in consumerNamesList
            ) {
                Box(
                    modifier = modifier
                        .then(
                            if (consumerName !in consumerNamesList)
                                Modifier.clickable { onChooseConsumerNameClick(consumerName) }
                            else
                                Modifier
                        )
                ) {
                    Text(
                        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = consumerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = MAXIMUM_AMOUNT_OF_LINES_IS_1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportActionsView(
    modifier: Modifier = Modifier,
    onShareOrderReportClick: () -> Unit,
    onSaveAsPdf: () -> Unit,
    reportIsEmpty: Boolean,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = !reportIsEmpty
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(
                    onClick = { onShareOrderReportClick() },
                ) {
                    Text(
                        text = stringResource(R.string.share_as_text),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                }

                TextButton(
                    onClick = { onSaveAsPdf() },
                ) {
                    Text(
                        text = stringResource(R.string.save_as_pdf),
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ReceiptReportColumn(
    modifier: Modifier = Modifier,
    receiptReportDataList: List<ReceiptReportData>,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = receiptReportDataList.isEmpty()
        ) { receiptIsEmpty ->
            if (receiptIsEmpty)
                Text(
                    modifier = modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.order_report_is_empty),
                )
            else
                Text(
                    text = stringResource(R.string.report_is),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

        }
        Spacer(modifier = Modifier.height(16.dp))

        repeat(receiptReportDataList.size) { index ->
            ReportItemView(receiptReportData = receiptReportDataList[index])
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ReportItemView(
    modifier: Modifier = Modifier,
    receiptReportData: ReceiptReportData,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = modifier.weight(1F),
                    text = receiptReportData.consumerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier = modifier.weight(1F),
                    text = stringResource(
                        R.string.total_is,
                        receiptReportData.totalSum ?: receiptReportData.subtotalSum
                    ),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            repeat(receiptReportData.orderList.size) { index ->
                val order = receiptReportData.orderList[index]
                ElevatedCard {
                    OrderReportItemView(orderReportData = order)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (receiptReportData.discount.isNotZero() || receiptReportData.tax.isNotZero() || receiptReportData.tip.isNotZero()) {
                HorizontalDivider()
                ReceiptDiscountTaxTipView(receiptReportData = receiptReportData)
            }
        }
    }
}

@Composable
internal fun ReceiptDiscountTaxTipView(
    modifier: Modifier = Modifier,
    receiptReportData: ReceiptReportData,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = stringResource(R.string.subtotal_is, receiptReportData.subtotalSum),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
        )
        DiscountTextTipView(
            receiptReportData = receiptReportData,
        )
        Text(
            text = stringResource(
                R.string.total_is,
                receiptReportData.totalSum ?: receiptReportData.subtotalSum
            ),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
internal fun DiscountTextTipView(
    modifier: Modifier = Modifier,
    receiptReportData: ReceiptReportData,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        if (receiptReportData.discount.isNotZero()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.discount_is,
                    receiptReportData.discount
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        if (receiptReportData.tip.isNotZero()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.tip_is,
                    receiptReportData.tip
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        if (receiptReportData.tax.isNotZero()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    R.string.tax_is,
                    receiptReportData.tax
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
internal fun OrderReportItemView(
    modifier: Modifier = Modifier,
    orderReportData: OrderReportData,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                modifier = modifier.weight(2f),
                text = orderReportData.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
            )
            Text(
                modifier = modifier.weight(1f),
                text = orderReportData.sum.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
            )
        }

        if (orderReportData.translatedName != null || orderReportData.amountText != null) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                orderReportData.translatedName?.let { translatedName ->
                    Text(
                        modifier = modifier.weight(1f),
                        text = translatedName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Start,
                    )
                }
                orderReportData.amountText?.let { amountText ->
                    Text(
                        modifier = modifier.weight(1f),
                        text = amountText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmedShowReportsScreenView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))

        repeat(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
internal fun TopAppBarSplitReceipt(
    modifier: Modifier = Modifier,
    onEditReceiptClick: () -> Unit,
    onClearReportClick: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = stringResource(R.string.receipt_view_submenu_button)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.edit))
                },
                onClick = {
                    expanded = false
                    onEditReceiptClick()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_receipt_button)
                    )
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.clear_report))
                },
                onClick = {
                    expanded = false
                    onClearReportClick()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Clear,
                        contentDescription = stringResource(R.string.clear_report_button)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplitReceiptForAllViewPreview() {
    SplitReceiptForAllView(
        receiptData =
            ReceiptData(
                id = 1,
                receiptName = "restaurant abdi paluma kulupa group",
                date = "18/03/2024",
                total = 60.0f,
                tax = 1.5F,
                discount = 0.0F,
                tip = 2.0F,
            ),
        orderDataSplitList =
            listOf(
                OrderDataSplit(
                    name = "order1",
                    translatedName = "заказ 1",
                    price = 999000.0f,
                    consumerNamesList = emptyList(),
                    checked = true,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order2",
                    price = 20.0f,
                    consumerNamesList = listOf("Alex"),
                    checked = true,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    translatedName = "перевод 1223 паошпов вошвоп вопшавоп ушегуре впргвр",
                    price = 30.0f,
                    consumerNamesList = emptyList(),
                    checked = false,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    price = 30.0f,
                    consumerNamesList = listOf("Dan", "John"),
                    checked = false,
                    orderDataId = 1,
                ),
                OrderDataSplit(
                    name = "order777",
                    price = 30.0f,
                    consumerNamesList = listOf("Dan", "John", "Abby", "Sian", "Alex", "Laura"),
                    checked = true,
                    orderDataId = 1,
                ),
            ),
        allConsumerNamesList = listOf(
            "Dan",
            "John",
            "Abby",
            "Sian",
            "Alex",
            "Laura",
            "Vasya",
            "Oleg"
        ),
    )
}

private const val MAXIMUM_AMOUNT_OF_LINES_IS_1 = 1
private const val MAXIMUM_AMOUNT_OF_LINES_IS_2 = 2
private const val MAXIMUM_AMOUNT_OF_LINES_IS_3 = 3
private const val SEPARATOR = "__"
private const val INFO_DISPLAY_CHAR_COUNT = 20