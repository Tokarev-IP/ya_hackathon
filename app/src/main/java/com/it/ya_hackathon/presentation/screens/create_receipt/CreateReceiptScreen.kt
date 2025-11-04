package com.it.ya_hackathon.presentation.screens.create_receipt

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.it.ya_hackathon.R
import com.it.ya_hackathon.basic.BasicCircularLoadingUi
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_IMAGES
import com.it.ya_hackathon.presentation.basic.BackNavigationButton
import com.it.ya_hackathon.presentation.receipt.ReceiptEvent
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptScreen(
    modifier: Modifier = Modifier,
    receiptViewModel: ReceiptViewModel,
    createReceiptViewModel: CreateReceiptViewModel,
    currentActivity: Activity? = LocalActivity.current,
    folderId: Long?,
) {
    val listOfImages by createReceiptViewModel.getReceiptImages().collectAsStateWithLifecycle()
    val uiState by createReceiptViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var languageSwitchState by rememberSaveable { mutableStateOf(false) }

    val languageCode = Locale.current.language

    LaunchedEffect(key1 = Unit) {
        createReceiptViewModel.getIntentFlow().collectLatest { createIntent ->
            when (createIntent) {
                is CreateReceiptIntent.NewReceiptIsCreated -> {
                    receiptViewModel.setEvent(ReceiptEvent.NewReceiptIsCreated(createIntent.receiptId))
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        createReceiptViewModel.getUiMessageIntentFlow().collectLatest { messageIntent ->
            handleCreateReceiptUiMessages(messageIntent, currentActivity)
        }
    }

    val choosePhotoLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAXIMUM_AMOUNT_OF_IMAGES),
    ) { newListOfImages: List<Uri> ->
        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = newListOfImages))
    }

    val scanningOptions = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(MAXIMUM_AMOUNT_OF_IMAGES)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(SCANNER_MODE_BASE)
        .build()

    val scanner = GmsDocumentScanning.getClient(scanningOptions)
    val scannerLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val result =
                GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            result?.pages?.let { pages ->
                val newListOfImages = pages.map { it.imageUri }
                createReceiptViewModel.setEvent(
                    CreateReceiptEvent.PutImages(listOfImages = newListOfImages)
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.create_receipt)) },
                navigationIcon = {
                    BackNavigationButton { receiptViewModel.setEvent(ReceiptEvent.GoBack) }
                },
            )
        }
    ) { innerPadding ->

        when (uiState) {
            is CreateReceiptUiState.Loading -> {
                BasicCircularLoadingUi(
                    modifier = modifier.padding(innerPadding)
                )
            }

            is CreateReceiptUiState.Show -> {
                CreateReceiptScreenView(
                    modifier = modifier.padding(innerPadding),
                    listOfUri = listOfImages ?: emptyList(),
                    onChoosePhotoClicked = {
                        choosePhotoLaunch.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                                maxItems = MAXIMUM_AMOUNT_OF_IMAGES,
                            )
                        )
                    },
                    onClearPhotoClicked = {
                        createReceiptViewModel.setEvent(CreateReceiptEvent.PutImages(listOfImages = emptyList()))
                    },
                    onGetReceiptFromImageClicked = {
                        createReceiptViewModel.setEvent(
                            CreateReceiptEvent.CreateReceipt(
                                translateTo = if (languageSwitchState) languageCode else null,
                                folderId = folderId,
                            )
                        )
                    },
                    onMakePhotoClicked = {
                        currentActivity?.let { myActivity ->
                            scanner.getStartScanIntent(myActivity)
                                .addOnSuccessListener { intentSender ->
                                    scannerLaunch.launch(
                                        IntentSenderRequest.Builder(intentSender).build()
                                    )
                                }
                                .addOnFailureListener { }
                        }
                    },
                    onSwitchCheckedChange = { value ->
                        languageSwitchState = value
                    },
                    languageSwitchState = languageSwitchState,
                    onManualCreateClicked = {
                        createReceiptViewModel.setEvent(
                            CreateReceiptEvent.CreateReceiptManually(folderId)
                        )
                    }
                )
            }
        }
    }
}

private fun handleCreateReceiptUiMessages(
    messageIntent: CreateReceiptUiMessageIntent,
    currentActivity: Activity?,
) {
    when (messageIntent) {
        is CreateReceiptUiMessageIntent.SomeImagesAreInappropriate -> {
            Toast.makeText(
                currentActivity,
                R.string.image_is_inappropriate,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.InternalError -> {
            Toast.makeText(
                currentActivity,
                R.string.internal_error,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.InternetConnectionError -> {
            Toast.makeText(
                currentActivity,
                R.string.no_internet_connection,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.ReceiptIsTooLong -> {
            Toast.makeText(
                currentActivity,
                R.string.receipt_is_too_long,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.FailedToLoad -> {
            Toast.makeText(
                currentActivity,
                R.string.failed_to_load,
                Toast.LENGTH_SHORT
            ).show()
        }

        is CreateReceiptUiMessageIntent.ReceiptIsEmpty -> {
            Toast.makeText(
                currentActivity,
                R.string.receipt_is_empty,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}