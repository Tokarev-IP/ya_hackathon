package com.it.ya_hackathon.presentation.screens.edit_receipt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.shimmerBrush
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_DISHES
import com.it.ya_hackathon.presentation.basic.EditReceiptInfoCardView
import com.it.ya_hackathon.presentation.basic.OrderItemView
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.ReceiptData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditReceiptScreenView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData?,
    orderDataList: List<OrderData>,
    onEditOrderClicked: (id: Long) -> Unit,
    onDeleteOrderClicked: (id: Long) -> Unit,
    onEditReceiptClicked: () -> Unit,
    onAddNewOrderClicked: () -> Unit,
    goToSplitReceiptScreenClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        receiptData?.let { receipt ->
            EditReceiptView(
                receiptData = receipt,
                orderDataList = orderDataList,
                onEditOrderClicked = { id -> onEditOrderClicked(id) },
                onDeleteOrderClicked = { id ->
                    onDeleteOrderClicked(id)
                },
                onEditReceiptClicked = { onEditReceiptClicked() },
                onAddNewOrderClicked = { onAddNewOrderClicked() },
                goToSplitReceiptScreenClick = { goToSplitReceiptScreenClick() },
            )
        } ?: ShimmedEditReceiptsScreenView()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditReceiptView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData = ReceiptData(id = 0),
    orderDataList: List<OrderData> = emptyList(),
    onEditOrderClicked: (id: Long) -> Unit = {},
    onDeleteOrderClicked: (id: Long) -> Unit = {},
    onEditReceiptClicked: () -> Unit = {},
    onAddNewOrderClicked: () -> Unit = {},
    goToSplitReceiptScreenClick: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        item {
            TopInfoView(
                receiptData = receiptData,
                orderDataList = orderDataList,
                onEditReceiptClicked = { onEditReceiptClicked() },
            )
        }
        items(orderDataList.size) { index ->
            val orderData = orderDataList[index]
            OrderCardView(
                orderData = orderData,
                onDeleteOrderClicked = { onDeleteOrderClicked(orderData.id) },
                onEditOrderClicked = { onEditOrderClicked(orderData.id) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            BottomActionsView(
                onAddNewOrderClicked = { onAddNewOrderClicked() },
                enabled = orderDataList.size < MAXIMUM_AMOUNT_OF_DISHES,
                goToSplitReceiptScreenClick = { goToSplitReceiptScreenClick() },
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TopInfoView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    orderDataList: List<OrderData>,
    onEditReceiptClicked: () -> Unit,
){
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        EditReceiptInfoCardView(
            receiptData = receiptData,
            onEditReceiptClicked = { onEditReceiptClicked() },
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(
            visible = orderDataList.isEmpty(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.no_orders_add_one),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun OrderCardView(
    modifier: Modifier = Modifier,
    orderData: OrderData,
    onDeleteOrderClicked: () -> Unit,
    onEditOrderClicked: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onDeleteOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_order_button)
                )
            }
            IconButton(
                onClick = { onEditOrderClicked() },
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_order_button)
                )
            }
        }
        Column(
            modifier = modifier.padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OrderItemView(orderData = orderData)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BottomActionsView(
    modifier: Modifier = Modifier,
    onAddNewOrderClicked: () -> Unit,
    enabled: Boolean,
    goToSplitReceiptScreenClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = !enabled,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.maximum_amount_of_dishes_reached),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        AddOrderButtonView(
            onAddNewOrderClicked = { onAddNewOrderClicked() },
            enabled = enabled,
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { goToSplitReceiptScreenClick() },
        ) {
            Text(
                text = stringResource(R.string.go_to_split_receipt_screen),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AddOrderButtonView(
    modifier: Modifier = Modifier,
    onAddNewOrderClicked: () -> Unit,
    enabled: Boolean,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onAddNewOrderClicked() },
        enabled = enabled,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Outlined.Add,
                stringResource(R.string.add_new_order_button)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.add_new_order),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }

    }
}

@Composable
private fun ShimmedEditReceiptsScreenView(
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
                .height(520.dp)
                .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))

        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun EditReceiptViewPreview() {
    EditReceiptView(
        receiptData =
            ReceiptData(
                id = 1,
                receiptName = "restaurant fhgf hgfh gfh gfh gfh gfh gf hgfhgf",
                translatedReceiptName = "ресторан пар апр апр апр парап р  папр апр ап",
                date = "18/03/2024",
                total = 60.0f,
                tax = 10.0f,
            ),
        orderDataList =
            listOf(
                OrderData(
                    id = 1,
                    name = "order1",
                    translatedName = "заказ 1",
                    quantity = 79,
                    price = 100000.0f,
                    receiptId = 1,
                ),
                OrderData(
                    id = 2,
                    name = "order2",
                    quantity = 2,
                    price = 20.0f,
                    receiptId = 1,
                ),
                OrderData(
                    id = 3,
                    name = "order3 fdgdf dfgfdg dfgdfg erter xcxv sdfdsf sdfsdf asd jyhn vcvf erret fgdfg",
                    translatedName = "апаап апврара аврварапр паоокуасм иарар счмацйяссся сравнпоавл вавапвап ",
                    quantity = 3,
                    price = 30.0f,
                    receiptId = 1,
                ),
            ),
    )
}