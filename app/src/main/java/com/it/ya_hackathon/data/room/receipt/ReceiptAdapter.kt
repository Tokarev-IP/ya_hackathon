package com.it.ya_hackathon.data.room.receipt

import com.it.ya_hackathon.basic.convertDateToMillis
import com.it.ya_hackathon.basic.convertMillisToDate
import com.it.ya_hackathon.basic.roundToTwoDecimalPlaces
import com.it.ya_hackathon.data.room.OrderEntity
import com.it.ya_hackathon.data.room.ReceiptEntity
import com.it.ya_hackathon.data.services.DataConstants.CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.presentation.receipt.OrderData
import com.it.ya_hackathon.presentation.receipt.OrderDataJson
import com.it.ya_hackathon.presentation.receipt.ReceiptData
import com.it.ya_hackathon.presentation.receipt.ReceiptDataJson

class ReceiptAdapter : ReceiptAdapterInterface {

    override suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData> {
        return receiptEntityList.map { receiptEntity ->
            ReceiptData(
                id = receiptEntity.id,
                receiptName = receiptEntity.receiptName,
                translatedReceiptName = receiptEntity.translatedReceiptName,
                date = receiptEntity.date.convertMillisToDate(),
                total = receiptEntity.total.roundToTwoDecimalPlaces(),
                tax = receiptEntity.tax.roundToTwoDecimalPlaces(),
                discount = receiptEntity.discount.roundToTwoDecimalPlaces(),
                tip = receiptEntity.tip.roundToTwoDecimalPlaces(),
                folderId = receiptEntity.folderId,
                isShared = receiptEntity.isShared,
                consumerNamesList = receiptEntity.consumerNames
                    .split(CONSUMER_NAME_DIVIDER)
                    .filter { it.isNotEmpty() },
                whoPaid = receiptEntity.whoPaid,
            )
        }
    }

    override suspend fun transformReceiptEntityToReceiptData(
        receiptEntity: ReceiptEntity
    ): ReceiptData {
        return receiptEntity.run {
            ReceiptData(
                id = id,
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
                date = date.convertMillisToDate(),
                total = total.roundToTwoDecimalPlaces(),
                tax = tax.roundToTwoDecimalPlaces(),
                discount = discount.roundToTwoDecimalPlaces(),
                tip = tip.roundToTwoDecimalPlaces(),
                folderId = folderId,
                isShared = isShared,
                consumerNamesList = consumerNames
                    .split(CONSUMER_NAME_DIVIDER)
                    .filter { it.isNotEmpty() },
                whoPaid = receiptEntity.whoPaid,
            )
        }
    }

    override suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson,
        folderId: Long?,
    ): ReceiptEntity {
        return receiptDataJson.run {
            ReceiptEntity(
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
                date = date,
                total = total.roundToTwoDecimalPlaces(),
                tax = tax.roundToTwoDecimalPlaces(),
                discount = discount.roundToTwoDecimalPlaces(),
                tip = tip.roundToTwoDecimalPlaces(),
                folderId = folderId,
            )
        }
    }

    override suspend fun transformOrderDataJsonListToOrderEntityList(
        orderDataJsonList: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity> {
        return orderDataJsonList.map { orderDataJson ->
            OrderEntity(
                orderName = orderDataJson.name,
                translatedName = orderDataJson.translatedName,
                quantity = orderDataJson.quantity,
                price = orderDataJson.price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
            )
        }
    }

    override suspend fun transformOrderEntityListToOrderDataList(
        orderEntityList: List<OrderEntity>
    ): List<OrderData> {
        return orderEntityList.map { orderEntity ->
            OrderData(
                id = orderEntity.id,
                name = orderEntity.orderName,
                translatedName = orderEntity.translatedName,
                quantity = orderEntity.quantity,
                price = orderEntity.price.roundToTwoDecimalPlaces(),
                receiptId = orderEntity.receiptId,
                consumersList = orderEntity.consumerNames
                    .split(CONSUMER_NAME_DIVIDER)
                    .filter { it.isNotEmpty() },
            )
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity {
        return receiptData.run {
            ReceiptEntity(
                id = id,
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
                date = date.convertDateToMillis(),
                total = total.roundToTwoDecimalPlaces(),
                tax = tax.roundToTwoDecimalPlaces(),
                discount = discount.roundToTwoDecimalPlaces(),
                tip = tip.roundToTwoDecimalPlaces(),
                folderId = folderId,
                isShared = isShared,
                consumerNames = consumerNamesList.joinToString(CONSUMER_NAME_DIVIDER),
                whoPaid = whoPaid,
            )
        }
    }

    override suspend fun transformOrderDataToOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                id = id,
                orderName = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
                consumerNames = consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformOrderDataToNewOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                orderName = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
                consumerNames = consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformOrderDataListToOrderEntityList(
        orderDataList: List<OrderData>
    ): List<OrderEntity> {
        return orderDataList.map { orderData ->
            OrderEntity(
                id = orderData.id,
                orderName = orderData.name,
                translatedName = orderData.translatedName,
                quantity = orderData.quantity,
                price = orderData.price.roundToTwoDecimalPlaces(),
                receiptId = orderData.receiptId,
                consumerNames = orderData.consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }
}

interface ReceiptAdapterInterface {
    suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData>

    suspend fun transformReceiptEntityToReceiptData(
        receiptEntity: ReceiptEntity,
    ): ReceiptData

    suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson,
        folderId: Long?,
    ): ReceiptEntity

    suspend fun transformOrderDataJsonListToOrderEntityList(
        orderDataJsonList: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity>

    suspend fun transformOrderEntityListToOrderDataList(
        orderEntityList: List<OrderEntity>,
    ): List<OrderData>

    suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData,
    ): ReceiptEntity

    suspend fun transformOrderDataToOrderEntity(
        orderData: OrderData,
    ): OrderEntity

    suspend fun transformOrderDataToNewOrderEntity(
        orderData: OrderData
    ): OrderEntity

    suspend fun transformOrderDataListToOrderEntityList(
        orderDataList: List<OrderData>
    ): List<OrderEntity>
}