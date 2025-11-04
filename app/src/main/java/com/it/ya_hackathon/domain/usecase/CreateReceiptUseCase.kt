package com.it.ya_hackathon.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.label.ImageLabel
import com.it.ya_hackathon.data.firebase.UserAuthenticatorInterface
import com.it.ya_hackathon.data.room.receipt.ReceiptDbRepositoryInterface
import com.it.ya_hackathon.data.services.DataConstants.APPROPRIATE_LABELS
import com.it.ya_hackathon.data.services.DataConstants.GEMINI_MODEL_NAME
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_DISHES
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_DISH_QUANTITY
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_AMOUNT_OF_IMAGES
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_PERCENT
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_SUM
import com.it.ya_hackathon.data.services.DataConstants.MAXIMUM_TEXT_LENGTH
import com.it.ya_hackathon.data.services.DataConstants.PROMPT_TEXT
import com.it.ya_hackathon.data.services.ImageConverterInterface
import com.it.ya_hackathon.data.services.ImageLabelingKitInterface
import com.it.ya_hackathon.data.services.ReceiptJsonServiceInterface
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptDataJson
import com.it.ya_hackathon.presentation.receipt.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CreateReceiptUseCase(
    private val imageLabelingKit: ImageLabelingKitInterface,
    private val imageConverter: ImageConverterInterface,
    private val receiptService: ReceiptJsonServiceInterface,
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
    private val userAuthenticator: UserAuthenticatorInterface,
) : CreateReceiptUseCaseInterface {

    private companion object {
        private const val DELAY_TIME = 3000L
    }

    override suspend fun createReceiptFromUriImage(
        listOfImages: List<Uri>,
        translateTo: String?,
        folderId: Long?,
    ): ReceiptCreationResult {
        return convertReceiptFromImageImpl(
            listOfImages = listOfImages,
            translateTo = translateTo,
            folderId = folderId,
        )
    }

    override suspend fun creteReceiptManually(folderId: Long?): ReceiptManuallyCreationResult {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newReceiptData = ReceiptData(folderId = folderId)
                receiptDbRepository.insertReceiptData(newReceiptData)
            }
        }.fold(
            onSuccess = {
                ReceiptManuallyCreationResult.Success(receiptId = it)
            },
            onFailure = {
                ReceiptManuallyCreationResult.Error(
                    it.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                )
            }
        )
    }

    override suspend fun haveImagesGotNotAppropriateImages(listOfImages: List<Uri>): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                val bitmapImages = listOfImages.map {
                    imageConverter.convertImageFromUriToBitmap(it)
                }
                hasNotAppropriateLabel(bitmapImages)
            }.getOrElse {
                true
            }
        }

    private suspend fun convertReceiptFromImageImpl(
        listOfImages: List<Uri>,
        translateTo: String?,
        folderId: Long?,
    ): ReceiptCreationResult = withContext(Dispatchers.IO) {
        runCatching {
            if (userAuthenticator.getCurrentUser()?.uid == null)
                userAuthenticator.authenticateAnonymousUser()
            val listOfBitmaps = listOfImages.map { image ->
                imageConverter.convertImageFromUriToBitmap(image)
            }
            if (hasNotAppropriateLabel(listOfBitmaps)) {
                delay(DELAY_TIME)
                return@withContext ReceiptCreationResult.ImageIsInappropriate
            }
            val receiptJson = receiptService.getReceiptJsonFromImages(
                listOfBitmaps = listOfBitmaps,
                requestText = PROMPT_TEXT,
                aiModel = GEMINI_MODEL_NAME,
                translateTo = translateTo,
            )
            val receiptDataJson: ReceiptDataJson =
                Json.decodeFromString<ReceiptDataJson>(receiptJson)
            val correctedReceiptDataJson = correctReceiptDataJson(receiptDataJson)
            if (correctedReceiptDataJson.orders.size > MAXIMUM_AMOUNT_OF_DISHES)
                return@withContext ReceiptCreationResult.ReceiptIsTooLong
            if (correctedReceiptDataJson.orders.isEmpty())
                return@withContext ReceiptCreationResult.OrdersAreEmpty
            val receiptId: Long = receiptDbRepository
                .insertReceiptDataJson(
                    receiptDataJson = correctedReceiptDataJson,
                    folderId = folderId,
                )
            return@withContext ReceiptCreationResult.Success(receiptId = receiptId)
        }.getOrElse { e: Throwable ->
            delay(DELAY_TIME)
            return@withContext ReceiptCreationResult.Error(
                e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
            )
        }
    }

    private suspend fun hasNotAppropriateLabel(
        imagesBitmap: List<Bitmap>,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                for (imageBitmap in imagesBitmap) {
                    val listOfLabels = imageLabelingKit.getLabelsOfImage(imageBitmap)
                    val isAppropriate = isInLabels(listOfLabels, appropriateLabels)
                    if (isAppropriate == false)
                        true
                }
                false
            }.getOrElse {
                true
            }
        }

    private fun isInLabels(
        listOfLabels: List<ImageLabel>,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean {
        for (label in listOfLabels) {
            if (label.index in appropriateLabels)
                return true
        }
        return false
    }

    override fun filterBySize(listOfImages: List<Uri>): List<Uri> {
        return listOfImages.take(MAXIMUM_AMOUNT_OF_IMAGES)
    }

    private fun correctReceiptDataJson(
        receiptDataJson: ReceiptDataJson,
    ): ReceiptDataJson {
        return receiptDataJson.copy(
            receiptName = receiptDataJson.receiptName.take(MAXIMUM_TEXT_LENGTH),
            translatedReceiptName = receiptDataJson.translatedReceiptName?.take(MAXIMUM_TEXT_LENGTH),
            date = receiptDataJson.date,
            orders = receiptDataJson.orders.take(MAXIMUM_AMOUNT_OF_DISHES).map { order ->
                order.copy(
                    name = order.name.take(MAXIMUM_TEXT_LENGTH),
                    translatedName = order.translatedName?.take(MAXIMUM_TEXT_LENGTH),
                    quantity = order.quantity.takeIf { order.quantity <= MAXIMUM_AMOUNT_OF_DISH_QUANTITY }
                        ?: MAXIMUM_AMOUNT_OF_DISH_QUANTITY,
                    price = order.price.takeIf { order.price <= MAXIMUM_SUM }
                        ?: MAXIMUM_SUM.toFloat()
                )
            },
            total = receiptDataJson.total.takeIf { receiptDataJson.total <= MAXIMUM_SUM }
                ?: MAXIMUM_SUM.toFloat(),
            tax = receiptDataJson.tax.takeIf { receiptDataJson.tax <= MAXIMUM_PERCENT }
                ?: MAXIMUM_PERCENT.toFloat(),
            discount = receiptDataJson.discount.takeIf { receiptDataJson.discount <= MAXIMUM_PERCENT }
                ?: MAXIMUM_PERCENT.toFloat(),
            tip = receiptDataJson.tip.takeIf { receiptDataJson.tip <= MAXIMUM_PERCENT }
                ?: MAXIMUM_PERCENT.toFloat(),
        )
    }
}

interface CreateReceiptUseCaseInterface {
    suspend fun createReceiptFromUriImage(
        listOfImages: List<Uri>,
        translateTo: String?,
        folderId: Long?,
    ): ReceiptCreationResult

    suspend fun creteReceiptManually(folderId: Long?): ReceiptManuallyCreationResult
    suspend fun haveImagesGotNotAppropriateImages(listOfImages: List<Uri>): Boolean
    fun filterBySize(listOfImages: List<Uri>): List<Uri>
}

sealed interface ReceiptCreationResult {
    class Success(val receiptId: Long) : ReceiptCreationResult
    object ImageIsInappropriate : ReceiptCreationResult
    object OrdersAreEmpty : ReceiptCreationResult
    class Error(val msg: String) : ReceiptCreationResult
    object ReceiptIsTooLong : ReceiptCreationResult
}

sealed interface ReceiptManuallyCreationResult {
    class Success(val receiptId: Long) : ReceiptManuallyCreationResult
    class Error(val msg: String) : ReceiptManuallyCreationResult
}