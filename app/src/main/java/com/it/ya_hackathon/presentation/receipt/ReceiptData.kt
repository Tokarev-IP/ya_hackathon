package com.it.ya_hackathon.presentation.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDataJson(
    @SerialName("receipt_name")
    val receiptName: String = "no name",
    @SerialName("translated_receipt_name")
    val translatedReceiptName: String? = null,
    @SerialName("date_in_millis")
    val date: Long = 0L,
    @SerialName("orders")
    val orders: List<OrderDataJson> = emptyList(),
    @SerialName("total_sum")
    val total: Float = 0.0F,
    @SerialName("tax_in_percent")
    val tax: Float = 0.0F,
    @SerialName("discount_in_percent")
    val discount: Float = 0.0F,
    @SerialName("tip_in_percent")
    val tip: Float = 0.0F,
)

@Serializable
data class OrderDataJson(
    @SerialName("name")
    val name: String = "no name",
    @SerialName("translated_name")
    val translatedName: String? = null,
    @SerialName("quantity")
    val quantity: Int = 1,
    @SerialName("price")
    val price: Float = 0.0F,
)

data class ReceiptData(
    val id: Long = 0,
    val receiptName: String = "no name",
    val translatedReceiptName: String? = null,
    val date: String = "no date",
    val total: Float = 0.0F,
    val tax: Float = 0.0F,
    val discount: Float = 0.0F,
    val tip: Float = 0.0F,
    val folderId: Long? = null,
    val isShared: Boolean = false,
    val isChecked: Boolean = false,
    val consumerNamesList: List<String> = emptyList(),
    val whoPaid: String = "",
)

data class OrderData(
    val id: Long,
    val name: String = "no name",
    val translatedName: String? = null,
    val selectedQuantity: Int = 0,
    val quantity: Int = 1,
    val price: Float = 0f,
    val receiptId: Long,
    val consumersList: List<String> = emptyList(),
)

data class OrderDataSplit(
    val name: String = "no name",
    val translatedName: String? = null,
    val price: Float = 0f,
    val consumerNamesList: List<String> = emptyList(),
    val checked: Boolean = false,
    val orderDataId: Long,
)

data class FolderData(
    val id: Long,
    val folderName: String = "no name",
    val consumerNamesList: List<String> = emptyList(),
    val isArchived: Boolean = false,
)

class ReceiptWithOrdersDataSplit(
    val receipt: ReceiptData,
    val orders: List<OrderDataSplit>,
)

class ReceiptWithFolderData(
    val receipt: ReceiptData,
    val folderName: String? = null,
)

class FolderWithReceiptsData(
    val folder: FolderData,
    val amountOfReceipts: Int = 0,
)

class FolderReportData(
    val consumerName: String,
    val totalSum: Float,
    val receiptList: List<ReceiptReportData>,
)

class ReceiptReportData(
    val receiptName: String,
    val translatedReceiptName: String?,
    val date: String,
    val consumerName: String = "",
    val subtotalSum: Float,
    val orderList: List<OrderReportData>,
    val discount: Float,
    val tax: Float,
    val tip: Float,
    val totalSum: Float?,
)

class OrderReportData(
    val name: String,
    val translatedName: String?,
    val amountText: String?,
    val sum: Float,
)