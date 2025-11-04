package com.it.ya_hackathon.data.services

import com.google.firebase.ai.type.Schema

object DataConstants {

    val receiptSchemaObjectTranslated = Schema.obj(
        mapOf(
            "receipt_name" to Schema.string(),
            "translated_receipt_name" to Schema.string(),
            "date_in_millis" to Schema.long(),
            "total_sum" to Schema.float(),
            "tax_in_percent" to Schema.float(),
            "discount_in_percent" to Schema.float(),
            "tip_in_percent" to Schema.float(),
            "orders" to Schema.array(
                Schema.obj(
                    mapOf(
                        "name" to Schema.string(),
                        "translated_name" to Schema.string(),
                        "quantity" to Schema.integer(),
                        "price" to Schema.float(),
                    )
                )
            )
        ),
        optionalProperties = listOf(
            "translated_receipt_name",
            "total_sum",
            "tax_in_percent",
            "discount_in_percent",
            "tip_in_percent",
        )
    )

    val receiptSchemaObjectNotTranslated = Schema.obj(
        mapOf(
            "receipt_name" to Schema.string(),
            "translated_receipt_name" to Schema.string(),
            "date_in_millis" to Schema.long(),
            "total_sum" to Schema.float(),
            "tax_in_percent" to Schema.float(),
            "discount_in_percent" to Schema.float(),
            "tip_in_percent" to Schema.float(),
            "orders" to Schema.array(
                Schema.obj(
                    mapOf(
                        "name" to Schema.string(),
                        "translated_name" to Schema.string(),
                        "quantity" to Schema.integer(),
                        "price" to Schema.float(),
                    ),
                    optionalProperties = listOf(
                        "translated_name",
                    )
                ),
            )
        ),
        optionalProperties = listOf(
            "translated_receipt_name",
            "total_sum",
            "tax_in_percent",
            "discount_in_percent",
            "tip_in_percent",
        )
    )

    const val RESPONSE_MIME_TYPE = "application/json"

    const val MAXIMUM_AMOUNT_OF_DISHES = 300
    const val MAXIMUM_AMOUNT_OF_DISH_QUANTITY = 99
    const val MAXIMUM_AMOUNT_OF_RECEIPTS = 1_000_000
    const val MAXIMUM_AMOUNT_OF_FOLDERS = 1000
    const val MAXIMUM_AMOUNT_OF_RECEIPTS_IN_FOLDER = 1000
    const val MAXIMUM_TEXT_LENGTH = 60
    const val MINIMUM_ORDER_PRICE = -999_999
    const val MAXIMUM_SUM = 999_999
    const val MAXIMUM_PERCENT = 100

    const val LANGUAGE_TEXT = "Translate to:"
    const val PROMPT_TEXT = "Extract data from receipt images. Note: items may have quantities >1, so totals = price × quantity. Calculate totals accordingly and ensure item sums match the final total."
    const val GEMINI_MODEL_NAME = "gemini-flash-lite-latest"

    const val CONSUMER_NAME_DIVIDER = "%"
    const val ORDER_CONSUMER_NAME_DIVIDER = "#"
    const val MAXIMUM_AMOUNT_OF_CONSUMER_NAMES = 20
    const val MAXIMUM_CONSUMER_NAME_TEXT_LENGTH = 40
    const val MAXIMUM_FOLDER_NAME_TEXT_LENGTH = 50

    // Labels for images are the following:
    // https://developers.google.com/ml-kit/vision/image-labeling/label-map
    // 135 Menu 240 Receipt 273 Paper 93 Poster
    val APPROPRIATE_LABELS: List<Int> = listOf(273, 135, 240, 93)

    const val MAXIMUM_AMOUNT_OF_IMAGES = 2
}