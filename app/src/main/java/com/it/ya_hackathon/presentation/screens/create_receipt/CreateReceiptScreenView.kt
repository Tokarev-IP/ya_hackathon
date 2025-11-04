package com.it.ya_hackathon.presentation.screens.create_receipt

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.it.ya_hackathon.R
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_IMAGES

@Composable
internal fun CreateReceiptScreenView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onChoosePhotoClicked: () -> Unit,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
    onMakePhotoClicked: () -> Unit,
    onSwitchCheckedChange: (Boolean) -> Unit,
    languageSwitchState: Boolean,
    onManualCreateClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CreateReceiptView(
            listOfUri = listOfUri,
            onChoosePhotoClicked = { onChoosePhotoClicked() },
            onClearPhotoClicked = { onClearPhotoClicked() },
            onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
            onMakePhotoClicked = { onMakePhotoClicked() },
            onSwitchCheckedChange = { value ->
                onSwitchCheckedChange(value)
            },
            languageSwitchState = languageSwitchState,
            onManualCreateClicked = { onManualCreateClicked() },
        )
    }
}

@Composable
private fun CreateReceiptView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onChoosePhotoClicked: () -> Unit = {},
    onClearPhotoClicked: () -> Unit = {},
    onGetReceiptFromImageClicked: () -> Unit = {},
    onMakePhotoClicked: () -> Unit = {},
    onSwitchCheckedChange: (Boolean) -> Unit = {},
    languageSwitchState: Boolean = false,
    onManualCreateClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        item {
            if (listOfUri.isEmpty()) {
                ChoosePhotoBoxView(
                    onChoosePhotoClicked = { onChoosePhotoClicked() },
                    onMakePhotoClicked = { onMakePhotoClicked() },
                    onManualCreateClicked = { onManualCreateClicked() },
                )
            } else {
                ImagesAreSelectedView(
                    listOfUri = listOfUri,
                    onClearPhotoClicked = { onClearPhotoClicked() },
                    onGetReceiptFromImageClicked = { onGetReceiptFromImageClicked() },
                    onSwitchCheckedChange = { value ->
                        onSwitchCheckedChange(value)
                    },
                    languageSwitchState = languageSwitchState,
                )
            }
        }
    }
}

@Composable
private fun ImagesAreSelectedView(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onClearPhotoClicked: () -> Unit,
    onGetReceiptFromImageClicked: () -> Unit,
    onSwitchCheckedChange: (Boolean) -> Unit,
    languageSwitchState: Boolean,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        if (listOfUri.size == ONE_ELEMENT) {
            PhotoBoxView(
                uri = listOfUri.firstOrNull(),
                onClearPhotoClicked = { onClearPhotoClicked() }
            )
        } else
            ImageCarouselBox(
                listOfUri = listOfUri,
                onClearPhotoClicked = { onClearPhotoClicked() },
            )
        Spacer(modifier = Modifier.height(40.dp))

        ChooseTranslatedLanguageView(
            switchState = languageSwitchState,
            onSwitchCheckedChange = { value ->
                onSwitchCheckedChange(value)
            },
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.translate_text),
            fontSize = 12.sp,
            fontWeight = FontWeight.Thin,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(
            onClick = { onGetReceiptFromImageClicked() },
        ) {
            Text(text = stringResource(id = R.string.split_the_receipt))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCarouselBox(
    modifier: Modifier = Modifier,
    listOfUri: List<Uri>,
    onClearPhotoClicked: () -> Unit = {},
    height: Dp = 320.dp,
    preferredItemWidth: Dp = 240.dp,
) {
    Box {
        HorizontalMultiBrowseCarousel(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp)),
            state = rememberCarouselState(itemCount = { listOfUri.size }),
            preferredItemWidth = preferredItemWidth,
            itemSpacing = 8.dp,
        ) { index ->
            AsyncImage(
                model = listOfUri[index],
                contentDescription = stringResource(R.string.receipt_photo),
                contentScale = ContentScale.Crop,
            )
        }
        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = { onClearPhotoClicked() }
        ) {
            Icon(
                Icons.Filled.Clear,
                contentDescription = stringResource(id = R.string.clear_receipt_photo)
            )
        }
    }
}

@Composable
private fun PhotoBoxView(
    modifier: Modifier = Modifier,
    uri: Uri?,
    onClearPhotoClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(320.dp)
            .width(320.dp)
    ) {
        AsyncImage(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 60.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp)),
            model = uri,
            contentDescription = stringResource(R.string.receipt_photo),
            contentScale = ContentScale.Crop,
        )

        IconButton(
            modifier = modifier.align(Alignment.TopEnd),
            onClick = { onClearPhotoClicked() }
        ) {
            Icon(
                Icons.Filled.Clear,
                contentDescription = stringResource(id = R.string.clear_receipt_photo)
            )
        }
    }
}

@Composable
private fun ChoosePhotoBoxView(
    modifier: Modifier = Modifier,
    onChoosePhotoClicked: () -> Unit,
    onMakePhotoClicked: () -> Unit,
    onManualCreateClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.receipt_photo_is_empty, MAXIMUM_AMOUNT_OF_IMAGES),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = { onChoosePhotoClicked() },
        ) {
            Text(
                text = stringResource(id = R.string.select_receipt_photo),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onMakePhotoClicked() },
        ) {
            Text(
                text = stringResource(id = R.string.take_photo),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onManualCreateClicked() },
        ) {
            Text(
                text = stringResource(id = R.string.create_manually),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseTranslatedLanguageView(
    modifier: Modifier = Modifier,
    switchState: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.padding(horizontal = 36.dp),
        onClick = { onSwitchCheckedChange(!switchState) },
        enabled = switchState,
    ) {
        Box(
            modifier = modifier.then(
                if (!switchState)
                    Modifier.clickable { onSwitchCheckedChange(!switchState) }
                else
                    Modifier
            )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(id = R.string.translate))
                Switch(
                    checked = switchState,
                    onCheckedChange = { value ->
                        onSwitchCheckedChange(value)
                    }
                )
            }
        }
    }
}

private const val ONE_ELEMENT = 1

@Composable
@Preview(showBackground = true)
private fun ChoosePhotoViewPreview() {
    CreateReceiptView(
        listOfUri = listOf("1234".toUri(), "1232345".toUri()),
    )
}