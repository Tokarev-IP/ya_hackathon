package com.it.ya_hackathon.data.room.folder

import com.it.ya_hackathon.data.room.FolderEntity
import com.it.ya_hackathon.data.services.DataConstants.CONSUMER_NAME_DIVIDER
import com.it.ya_hackathon.presentation.receipt.FolderData

class FolderAdapter() : FolderAdapterInterface {

    override suspend fun transformFolderEntityToFolderData(
        folderEntity: FolderEntity,
    ): FolderData {
        return FolderData(
            id = folderEntity.id,
            folderName = folderEntity.folderName,
            isArchived = folderEntity.isArchived,
            consumerNamesList = folderEntity.consumerNames
                .split(CONSUMER_NAME_DIVIDER)
                .filter { it.isNotEmpty() }
        )
    }

    override suspend fun transformFolderEntityListToFolderDataList(
        folderEntityList: List<FolderEntity>,
    ): List<FolderData> {
        return folderEntityList.map {
            FolderData(
                id = it.id,
                folderName = it.folderName,
                isArchived = it.isArchived,
                consumerNamesList = it.consumerNames
                    .split(CONSUMER_NAME_DIVIDER)
                    .filter { data -> data.isNotEmpty() }
            )
        }
    }

    override suspend fun transformFolderDataListToFolderEntityList(
        folderDataList: List<FolderData>,
    ): List<FolderEntity> {
        return folderDataList.map {
            FolderEntity(
                id = it.id,
                folderName = it.folderName,
                isArchived = it.isArchived,
                consumerNames = it.consumerNamesList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformFolderDataToFolderEntity(
        folderData: FolderData,
    ): FolderEntity {
        return FolderEntity(
            id = folderData.id,
            folderName = folderData.folderName,
            isArchived = folderData.isArchived,
            consumerNames = folderData.consumerNamesList.joinToString(CONSUMER_NAME_DIVIDER)
        )
    }

    override suspend fun transformNewFolderDataToFolderEntity(
        folderData: FolderData,
    ): FolderEntity {
        return FolderEntity(
            folderName = folderData.folderName,
            isArchived = folderData.isArchived,
            consumerNames = folderData.consumerNamesList.joinToString(CONSUMER_NAME_DIVIDER)
        )
    }

}

interface FolderAdapterInterface {
    suspend fun transformFolderEntityToFolderData(
        folderEntity: FolderEntity
    ): FolderData

    suspend fun transformFolderEntityListToFolderDataList(
        folderEntityList: List<FolderEntity>
    ): List<FolderData>

    suspend fun transformFolderDataListToFolderEntityList(
        folderDataList: List<FolderData>
    ): List<FolderEntity>

    suspend fun transformFolderDataToFolderEntity(
        folderData: FolderData
    ): FolderEntity

    suspend fun transformNewFolderDataToFolderEntity(
        folderData: FolderData
    ): FolderEntity
}