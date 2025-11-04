package com.it.ya_hackathon.presentation.screens.create_receipt

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.it.ya_hackathon.basic.BasicEvent
import com.it.ya_hackathon.basic.BasicIntent
import com.it.ya_hackathon.basic.BasicUiMessageIntent
import com.it.ya_hackathon.basic.BasicUiState
import com.it.ya_hackathon.basic.BasicViewModel
import com.it.ya_hackathon.domain.usecase.CreateReceiptUseCaseInterface
import com.it.ya_hackathon.domain.usecase.ReceiptCreationResult
import com.it.ya_hackathon.domain.usecase.ReceiptManuallyCreationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateReceiptViewModel(
    private val createReceiptUseCase: CreateReceiptUseCaseInterface,
) : BasicViewModel<
        CreateReceiptUiState,
        CreateReceiptIntent,
        CreateReceiptEvent,
        CreateReceiptUiMessageIntent>(initialUiState = CreateReceiptUiState.Show) {

    private val receiptImagesFlow = MutableStateFlow<List<Uri>?>(null)
    private val receiptImagesState = receiptImagesFlow.asStateFlow()

    private fun setReceiptImages(newReceiptImages: List<Uri>?) {
        receiptImagesFlow.value = newReceiptImages
    }

    fun getReceiptImages() = receiptImagesState

    override fun setEvent(newEvent: CreateReceiptEvent) {
        when (newEvent) {
            is CreateReceiptEvent.CreateReceipt -> {
                receiptImagesState.value?.let { images ->
                        createReceipt(
                            listOfImages = images,
                            translateTo = newEvent.translateTo,
                            folderId = newEvent.folderId,
                        )
                } ?: setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            }

            is CreateReceiptEvent.PutImages -> {
                putReceiptImages(listOfImages = newEvent.listOfImages)
            }

            CreateReceiptEvent.SetLoadingState -> {
                setUiState(CreateReceiptUiState.Loading)
            }

            CreateReceiptEvent.SetShowState -> {
                setUiState(CreateReceiptUiState.Show)
            }

            is CreateReceiptEvent.CreateReceiptManually -> {
                createReceiptManually(newEvent.folderId)
            }
        }
    }

    private fun createReceipt(
        listOfImages: List<Uri>,
        translateTo: String?,
        folderId: Long?,
    ) {
            viewModelScope.launch {
                setUiState(CreateReceiptUiState.Loading)
                val response =
                    createReceiptUseCase.createReceiptFromUriImage(
                        listOfImages = listOfImages,
                        translateTo = translateTo,
                        folderId = folderId,
                    )
                when (response) {
                    is ReceiptCreationResult.Success -> {
                        setIntent(CreateReceiptIntent.NewReceiptIsCreated(response.receiptId))
                    }

                    is ReceiptCreationResult.ImageIsInappropriate -> {
                        setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
                        setUiState(CreateReceiptUiState.Show)
                    }

                    is ReceiptCreationResult.Error -> {
                        setUiMessageIntent(handleReceiptCreationError(response.msg))
                        setUiState(CreateReceiptUiState.Show)
                    }


                    is ReceiptCreationResult.ReceiptIsTooLong -> {
                        setUiMessageIntent(CreateReceiptUiMessageIntent.ReceiptIsTooLong)
                        setUiState(CreateReceiptUiState.Show)
                    }

                    is ReceiptCreationResult.OrdersAreEmpty -> {
                        setUiMessageIntent(CreateReceiptUiMessageIntent.ReceiptIsEmpty)
                        setUiState(CreateReceiptUiState.Show)
                    }
                }
            }
    }

    private fun putReceiptImages(listOfImages: List<Uri>) {
        viewModelScope.launch {
            val filteredListOfImages =
                createReceiptUseCase.filterBySize(listOfImages = listOfImages)
            val result: Boolean =
                createReceiptUseCase.haveImagesGotNotAppropriateImages(listOfImages = listOfImages)
            if (result) {
                setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            }
            setReceiptImages(filteredListOfImages)
        }
    }

    private fun createReceiptManually(folderId: Long?) {
        viewModelScope.launch {
            setUiState(CreateReceiptUiState.Loading)
            createReceiptUseCase.creteReceiptManually(folderId = folderId).run {
                when (this) {
                    is ReceiptManuallyCreationResult.Error -> {
                        setUiMessageIntent(handleReceiptCreationError(this.msg))
                        setUiState(CreateReceiptUiState.Show)
                    }

                    is ReceiptManuallyCreationResult.Success -> {
                        setIntent(
                            CreateReceiptIntent.NewReceiptIsCreated(receiptId = this.receiptId)
                        )
                    }
                }
            }
        }
    }

}

private fun handleReceiptCreationError(errorMsg: String): CreateReceiptUiMessageIntent {
    return when (errorMsg) {
        CreateReceiptUiMessage.INTERNAL_ERROR.message -> CreateReceiptUiMessageIntent.InternalError
        CreateReceiptUiMessage.NETWORK_ERROR.message -> CreateReceiptUiMessageIntent.InternetConnectionError
        CreateReceiptUiMessage.IMAGE_IS_INAPPROPRIATE.message -> CreateReceiptUiMessageIntent.SomeImagesAreInappropriate
        else -> CreateReceiptUiMessageIntent.InternalError
    }
}

interface CreateReceiptUiState : BasicUiState {
    object Show : CreateReceiptUiState
    object Loading : CreateReceiptUiState
}

sealed interface CreateReceiptEvent : BasicEvent {
    class CreateReceipt(val translateTo: String?, val folderId: Long?) : CreateReceiptEvent
    class PutImages(val listOfImages: List<Uri>) : CreateReceiptEvent
    object SetLoadingState : CreateReceiptEvent
    object SetShowState : CreateReceiptEvent
    class CreateReceiptManually(val folderId: Long?) : CreateReceiptEvent
}

interface CreateReceiptUiMessageIntent : BasicUiMessageIntent {
    object SomeImagesAreInappropriate : CreateReceiptUiMessageIntent
    object InternalError : CreateReceiptUiMessageIntent
    object InternetConnectionError : CreateReceiptUiMessageIntent
    object ReceiptIsTooLong : CreateReceiptUiMessageIntent
    object FailedToLoad : CreateReceiptUiMessageIntent
    object ReceiptIsEmpty : CreateReceiptUiMessageIntent
}

interface CreateReceiptIntent : BasicIntent {
    class NewReceiptIsCreated(val receiptId: Long) : CreateReceiptIntent
}

enum class CreateReceiptUiMessage(val message: String) {
    INTERNAL_ERROR("An internal error has occurred."),
    NETWORK_ERROR("A network error (such as timeout, interrupted connection or unreachable host) has occurred."),
    IMAGE_IS_INAPPROPRIATE("Image is inappropriate. Choose another one."),
}