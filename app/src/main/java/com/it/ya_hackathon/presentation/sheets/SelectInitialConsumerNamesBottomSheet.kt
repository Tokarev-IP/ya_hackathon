package com.it.ya_hackathon.presentation.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.it.ya_hackathon.presentation.basic.FlowGridLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectInitialConsumerNamesBottomSheet(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onDismissClick: () -> Unit,
    onSetSelectedNamesClick: (List<String>) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissClick() },
        sheetState = sheetState,
    ) {
        SelectInitialConsumerNamesBottomView(
            allConsumerNamesList = allConsumerNamesList,
            onSetSelectedNamesClick = { names ->
                onSetSelectedNamesClick(names)
            },
        )
    }
}

@Composable
private fun SelectInitialConsumerNamesBottomView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String> = emptyList(),
    onSetSelectedNamesClick: (List<String>) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            SelectInitialConsumerNamesView(
                allConsumerNamesList = allConsumerNamesList,
                onSetSelectedNamesClick = { names ->
                    onSetSelectedNamesClick(names)
                },
            )
        }
    }
}

@Composable
private fun SelectInitialConsumerNamesView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onSetSelectedNamesClick: (List<String>) -> Unit,
) {
    val chosenConsumerNamesList = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.select_consumer_names),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (allConsumerNamesList.size >= MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.maximum_amount_of_names_is_reached)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (allConsumerNamesList.isNotEmpty())
            OrderConsumerNamesGrid(
                allConsumerNamesList = allConsumerNamesList,
                onAddConsumerNameClick = { name ->
                    chosenConsumerNamesList.add(name)
                    onSetSelectedNamesClick(chosenConsumerNamesList)
                },
                onRemoveConsumerNameClick = { name ->
                    chosenConsumerNamesList.remove(name)
                    onSetSelectedNamesClick(chosenConsumerNamesList)
                },
                orderConsumerNamesList = chosenConsumerNamesList,
            )
        else
            Text(
                text = stringResource(R.string.names_are_empty),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun OrderConsumerNamesGrid(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    orderConsumerNamesList: List<String>,
    onAddConsumerNameClick: (String) -> Unit,
    onRemoveConsumerNameClick: (String) -> Unit,
) {
    FlowGridLayout(
        horizontalSpacing = 8.dp,
        verticalSpacing = 4.dp,
    ) {
        repeat(allConsumerNamesList.size) { index ->
            val consumerName = allConsumerNamesList[index]
            OutlinedCard(
                onClick = { onRemoveConsumerNameClick(consumerName) },
                enabled = consumerName in orderConsumerNamesList
            ) {
                Box(
                    modifier = modifier
                        .then(
                            if (consumerName !in orderConsumerNamesList)
                                Modifier.clickable { onAddConsumerNameClick(consumerName) }
                            else
                                Modifier
                        )
                ) {
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = consumerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = MAXIMUM_LINE_IS_1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

const val EMPTY_STRING = ""
private const val MAXIMUM_LINE_IS_1 = 1

@Preview(showBackground = true)
@Composable
private fun SetInitialConsumerNameBottomSheetViewPreview() {
    SelectInitialConsumerNamesBottomView(
        allConsumerNamesList = listOf(
            "consumer1",
            "consumer2",
            "consumer number 3",
            "consumer4",
            "consumer5",
            "consumer6",
            "consumer7",
            "consumer8",
            "consumer1",
            "consumer2",
            "consumer number 3",
            "consumer4",
            "consumer5",
            "consumer6",
            "consumer7",
            "consumer8",
            "consumer1",
        )
    )
}