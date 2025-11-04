package com.it.ya_hackathon.presentation.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.icons.PersonAddIcon
import com.it.ya_hackathon.data.services.DataConstants.CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
import com.it.ya_hackathon.data.services.DataConstants.ORDER_CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.presentation.basic.FlowGridLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddConsumerNameBottomSheet(
    modifier: Modifier = Modifier,
    folderConsumerNamesList: List<String>,
    receiptConsumerNamesList: List<String>,
    allConsumerNamesList: List<String>,
    onAddConsumerNameClick: (name: String) -> Unit,
    onRemoveConsumerNameClick: (name: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
    ) {
        AddConsumerNameBottomSheetView(
            modifier = modifier,
            folderConsumerNamesList = folderConsumerNamesList,
            receiptConsumerNamesList = receiptConsumerNamesList,
            allConsumerNamesList = allConsumerNamesList,
            onAddConsumerNameClick = { name ->
                onAddConsumerNameClick(name)
            },
            onRemoveConsumerNameClick = { name ->
                onRemoveConsumerNameClick(name)
            },
        )
    }
}

@Composable
private fun AddConsumerNameBottomSheetView(
    modifier: Modifier = Modifier,
    folderConsumerNamesList: List<String>,
    receiptConsumerNamesList: List<String>,
    allConsumerNamesList: List<String>,
    onAddConsumerNameClick: (name: String) -> Unit,
    onRemoveConsumerNameClick: (name: String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ConsumerNamesView(
                folderConsumerNamesList = folderConsumerNamesList,
                receiptConsumerNamesList = receiptConsumerNamesList,
                onRemoveConsumerNameClick = { name ->
                    onRemoveConsumerNameClick(name)
                },
            )
            AddConsumerNameView(
                allConsumerNamesList = allConsumerNamesList,
                onAddConsumerNameClick = { name ->
                    onAddConsumerNameClick(name)
                },
            )
        }
    }
}

@Composable
private fun ConsumerNamesView(
    modifier: Modifier = Modifier,
    folderConsumerNamesList: List<String>,
    receiptConsumerNamesList: List<String>,
    onRemoveConsumerNameClick: (name: String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (folderConsumerNamesList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.names_from_folder),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowGridLayout {
                folderConsumerNamesList.forEach { name ->
                    OutlinedCard {
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = stringResource(R.string.names_from_receipt),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        )
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = receiptConsumerNamesList.isEmpty()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Justify,
                    text = stringResource(R.string.names_are_empty_add_one),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        FlowGridLayout {
            receiptConsumerNamesList.forEach { name ->
                OutlinedCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                        )
                        IconButton(
                            onClick = { onRemoveConsumerNameClick(name) }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                stringResource(R.string.clear_text_button)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(modifier = modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddConsumerNameView(
    modifier: Modifier = Modifier,
    allConsumerNamesList: List<String>,
    onAddConsumerNameClick: (name: String) -> Unit,
) {
    var consumerNameText by rememberSaveable { mutableStateOf("") }
    var isConsumerNameErrorState by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = allConsumerNamesList.size >= MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    textAlign = TextAlign.Justify,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    text = stringResource(R.string.maximum_amount_of_names_is_reached)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        OutlinedTextField(
            value = consumerNameText,
            onValueChange = { name ->
                isConsumerNameErrorState = false
                if (name.length <= MAXIMUM_CONSUMER_NAME_TEXT_LENGTH)
                    consumerNameText = name
            },
            isError = isConsumerNameErrorState,
            singleLine = true,
            label = { Text(text = stringResource(R.string.name)) },
            trailingIcon = {
                if (consumerNameText.isNotEmpty())
                    IconButton(
                        onClick = {
                            consumerNameText = EMPTY_STRING
                            isConsumerNameErrorState = false
                        }
                    ) { Icon(
                        Icons.Filled.Clear,
                        stringResource(R.string.clear_text_button))
                    }
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        addConsumerName(
                            consumerNameText = consumerNameText.trim(),
                            allConsumerNamesList = allConsumerNamesList,
                            onConsumerNameErrorState = { state ->
                                isConsumerNameErrorState = state
                            },
                            onAddNewConsumerNameClick = { name ->
                                onAddConsumerNameClick(name)
                                consumerNameText = EMPTY_STRING
                            }
                        )
                        focusManager.clearFocus()
                    },
                    enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
                ) {
                    Icon(Icons.Filled.PersonAddIcon, stringResource(R.string.add_name_button))
                }
            },
            supportingText = {
                if (isConsumerNameErrorState && consumerNameText.isEmpty())
                    Text(text = stringResource(R.string.field_is_empty))
                else if (isConsumerNameErrorState && consumerNameText.trim() in allConsumerNamesList)
                    Text(text = stringResource(R.string.name_is_already_existed))
                else if (isConsumerNameErrorState)
                    Text(text = stringResource(R.string.inappropriate_symbols))
                else if (consumerNameText.isNotEmpty())
                    Text(
                        text = stringResource(
                            R.string.maximum_letters,
                            consumerNameText.length,
                            MAXIMUM_CONSUMER_NAME_TEXT_LENGTH,
                        )
                    )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    addConsumerName(
                        consumerNameText = consumerNameText.trim(),
                        allConsumerNamesList = allConsumerNamesList,
                        onConsumerNameErrorState = { state ->
                            isConsumerNameErrorState = state
                        },
                        onAddNewConsumerNameClick = { name ->
                            onAddConsumerNameClick(name)
                            consumerNameText = EMPTY_STRING
                        }
                    )
                    focusManager.clearFocus()
                }
            ),
            enabled = allConsumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES,
        )
        Spacer(modifier = Modifier.height(36.dp))
    }
}

private fun addConsumerName(
    consumerNameText: String,
    allConsumerNamesList: List<String>,
    onAddNewConsumerNameClick: (String) -> Unit,
    onConsumerNameErrorState: (Boolean) -> Unit,
) {
    if (consumerNameText.trim()
            .isEmpty() || consumerNameText.length > MAXIMUM_CONSUMER_NAME_TEXT_LENGTH
    ) {
        onConsumerNameErrorState(true)
        return
    }
    if (CONSUMER_NAME_DIVIDER in consumerNameText.trim() ||
        ORDER_CONSUMER_NAME_DIVIDER in consumerNameText.trim()
    ) {
        onConsumerNameErrorState(true)
        return
    }

    if (consumerNameText.trim() in allConsumerNamesList) {
        onConsumerNameErrorState(true)
        return
    }

    onAddNewConsumerNameClick(consumerNameText.trim())
}