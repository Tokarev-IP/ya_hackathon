package com.it.ya_hackathon.presentation.basic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ya_hackathon.R
import com.it.ya_hackathon.presentation.receipt.ReceiptData

@Composable
internal fun ReceiptInfoCardView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
){
    var expanded by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.animateContentSize(),
        onClick = { expanded = !expanded },
    ) {
        ReceiptInfoView(
            receiptData = receiptData,
            expanded = expanded,
            onExpandClicked = { expanded = !expanded }
        )
    }
}

@Composable
internal fun EditReceiptInfoCardView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    onEditReceiptClicked: () -> Unit,
){
    var expanded by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.animateContentSize(),
        onClick = { expanded = !expanded },
    ) {
        EditReceiptInfoView(
            receiptData = receiptData,
            expanded = expanded,
            onExpandClicked = { expanded = !expanded },
            onEditReceiptClicked = { onEditReceiptClicked() },
        )
    }
}

@Composable
private fun ReceiptInfoView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    expanded: Boolean,
    onExpandClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExpandIconButtonView(
            modifier = modifier
                .weight(1f)
                .align(Alignment.Bottom),
            expanded = expanded,
            onExpandClicked = { onExpandClicked()}
        )
        ReceiptInfoColumnView(
            modifier = modifier
                .weight(5f)
                .align(Alignment.CenterVertically)
                .animateContentSize(),
            receiptData = receiptData,
            expanded = expanded,
        )
    }
}

@Composable
private fun EditReceiptInfoView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    expanded: Boolean,
    onExpandClicked: () -> Unit,
    onEditReceiptClicked:() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        ExpandIconButtonView(
            modifier = modifier
                .weight(1f)
                .align(Alignment.Bottom),
            expanded = expanded,
            onExpandClicked = { onExpandClicked()}
        )
        ReceiptInfoColumnView(
            modifier = modifier
                .weight(4f)
                .align(Alignment.CenterVertically)
                .animateContentSize(),
            receiptData = receiptData,
            expanded = expanded,
        )
        IconButton(
            modifier = modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            onClick = { onEditReceiptClicked() },
        ) {
            Icon(
                Icons.Outlined.Edit,
                stringResource(R.string.edit_receipt_info_button)
            )
        }
    }
}

@Composable
private fun ExpandIconButtonView(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandClicked: () -> Unit,
){
    IconButton(
        modifier = modifier,
        onClick = { onExpandClicked() }
    ) {
        AnimatedContent(targetState = expanded) { expand ->
            if (expand)
                Icon(
                    modifier = modifier.animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ),
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.expand_receipt_info_button),
                )
            else
                Icon(
                    modifier = modifier.animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ),
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.narrow_down_receipt_info_button)
                )
        }
    }
}

@Composable
private fun ReceiptInfoColumnView(
    modifier: Modifier = Modifier,
    receiptData: ReceiptData,
    expanded: Boolean,
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = receiptData.receiptName,
            textAlign = TextAlign.Start,
        )

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                receiptData.translatedReceiptName?.let { translatedRestaurant ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        fontSize = 20.sp,
                        text = translatedRestaurant,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    text = receiptData.date,
                    textAlign = TextAlign.Start,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontSize = 20.sp,
                    text = stringResource(R.string.total_is, receiptData.total),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontSize = 20.sp,
                    text = stringResource(R.string.discount_is, receiptData.discount),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontSize = 20.sp,
                    text = stringResource(R.string.tip_is, receiptData.tip),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontSize = 20.sp,
                    text = stringResource(R.string.tax_is, receiptData.tax),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontSize = 20.sp,
                    text = stringResource(R.string.who_paid, receiptData.whoPaid),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}