package com.it.ya_hackathon.data.services

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.it.ya_hackathon.basic.getLanguageString
import com.it.ya_hackathon.data.services.DataConstants.RESPONSE_MIME_TYPE
import com.it.ya_hackathon.data.services.DataConstants.receiptSchemaObjectNotTranslated
import com.it.ya_hackathon.data.services.DataConstants.receiptSchemaObjectTranslated
import com.it.ya_hackathon.presentation.receipt.ReceiptUiMessage
import kotlinx.coroutines.withTimeout

class ReceiptJsonService() : ReceiptJsonServiceInterface {

    private companion object {
        const val ONE_IMAGE = 1
        const val TWO_IMAGES = 2
        const val THREE_IMAGES = 3

        const val MAXIMUM_TIMEOUT = 15000L
    }

    private fun getPrompt(listOfBitmaps: List<Bitmap>, requestText: String): Content {
        return when (listOfBitmaps.size) {
            ONE_IMAGE -> {
                content {
                    image(listOfBitmaps[0])
                    text(requestText)
                }
            }

            TWO_IMAGES -> {
                content {
                    image(listOfBitmaps[0])
                    image(listOfBitmaps[1])
                    text(requestText)
                }
            }

            THREE_IMAGES -> {
                content {
                    image(listOfBitmaps[0])
                    image(listOfBitmaps[1])
                    image(listOfBitmaps[2])
                    text(requestText)
                }
            }

            else -> {
                content {
                    text(requestText)
                }
            }
        }
    }

    override suspend fun getReceiptJsonFromImages(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        aiModel: String,
        translateTo: String?,
    ): String {
        return withTimeout(MAXIMUM_TIMEOUT) {
            getReceiptJsonFromImagesImpl(
                listOfBitmaps = listOfBitmaps,
                requestText = requestText,
                aiModel = aiModel,
                translateTo = translateTo,
            )
        }
    }

    private suspend fun getReceiptJsonFromImagesImpl(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        aiModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
        translateTo: String?,
    ): String {
        val requestTranslateText = requestText.getLanguageString(translateTo = translateTo)
        val generativeModel = getGenerativeModel(
            vertexJson = if (translateTo == null) receiptSchemaObjectNotTranslated else receiptSchemaObjectTranslated,
            aiModel = aiModel,
            mimeType = mimeType,
        )
        val prompt = getPrompt(listOfBitmaps, requestTranslateText)
        val result = generativeModel.generateContent(prompt)
        return result.text ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }

    private fun getGenerativeModel(
        vertexJson: Schema,
        aiModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
    ) = Firebase.ai()
        .generativeModel(
            modelName = aiModel,
            generationConfig = generationConfig {
                responseMimeType = mimeType
                responseSchema = vertexJson
            }
        )

    override suspend fun getReceiptJsonFromText(
        receiptText: String,
        requestText: String,
        aiModel: String,
        translateTo: String?,
    ): String {
        return getReceiptJsonFromTextImpl(
            requestText = requestText,
            aiModel = aiModel,
            translateTo = translateTo,
            receiptText = receiptText,
        )
    }

    private suspend fun getReceiptJsonFromTextImpl(
        requestText: String,
        aiModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
        translateTo: String?,
        receiptText: String,
    ): String {
        val requestTranslateText = requestText.getLanguageString(translateTo = translateTo)
        val overallRequestText = "$requestTranslateText $receiptText"
        val generativeModel = getGenerativeModel(
            vertexJson = if (translateTo == null) receiptSchemaObjectNotTranslated else receiptSchemaObjectTranslated,
            aiModel = aiModel,
            mimeType = mimeType
        )
        val prompt = content { text(overallRequestText) }
        val result = generativeModel.generateContent(prompt)
        return result.text ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }
}

interface ReceiptJsonServiceInterface {
    suspend fun getReceiptJsonFromImages(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        aiModel: String,
        translateTo: String?,
    ): String

    suspend fun getReceiptJsonFromText(
        receiptText: String,
        requestText: String,
        aiModel: String,
        translateTo: String?,
    ): String
}