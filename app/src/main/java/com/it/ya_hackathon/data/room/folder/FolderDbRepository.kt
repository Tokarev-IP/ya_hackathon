package com.it.ya_hackathon.data.room.folder

import com.it.ya_hackathon.data.room.receipt.ReceiptAdapterInterface
import com.it.ya_hackathon.presentation.receipt.FolderData
import com.it.ya_hackathon.presentation.receipt.FolderWithReceiptsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FolderDbRepository(
    private val folderDao: FolderDao,
    private val folderAdapter: FolderAdapterInterface,
    private val receiptAdapter: ReceiptAdapterInterface,
) : FolderDbRepositoryInterface {

    override suspend fun getAllFoldersFlow(): Flow<List<FolderData>> {
        return folderDao.getAllFoldersFlow().map { folderEntityList ->
            folderAdapter.transformFolderEntityListToFolderDataList(folderEntityList = folderEntityList)
        }
    }

    override suspend fun getFolderById(id: Long): FolderData? {
        return folderDao.getFolderById(id).let { folderEntity ->
            folderEntity?.let {
                folderAdapter.transformFolderEntityToFolderData(folderEntity = folderEntity)
            }
        }
    }

    override suspend fun getFolderByIdFlow(id: Long): Flow<FolderData?> {
        return folderDao.getFolderByIdFlow(id).map { folderEntity ->
            folderEntity?.let {
                folderAdapter.transformFolderEntityToFolderData(folderEntity = folderEntity)
            }
        }
    }

    override suspend fun getFoldersWithReceipts(): Flow<List<FolderWithReceiptsData>> {
        return folderDao.getFoldersWithReceipts().map { folderWithReceiptsEntityList ->
            folderWithReceiptsEntityList.map { folderWithReceiptsEntity ->
                FolderWithReceiptsData(
                    folder = folderAdapter.transformFolderEntityToFolderData(
                        folderWithReceiptsEntity.folder
                    ),
                    amountOfReceipts = receiptAdapter.transformReceiptEntityListToReceiptDateList(
                        folderWithReceiptsEntity.receipts
                    ).size
                )
            }
        }
    }

    override suspend fun insertFolder(folderData: FolderData) {
        return folderDao.insertFolder(
            folder = folderAdapter.transformFolderDataToFolderEntity(folderData = folderData)
        )
    }

    override suspend fun insertNewFolder(folderData: FolderData) {
        return folderDao.insertFolder(
            folder = folderAdapter.transformNewFolderDataToFolderEntity(folderData = folderData)
        )
    }

    override suspend fun deleteFolderById(id: Long) {
        return folderDao.deleteFolderById(id)
    }
}

interface FolderDbRepositoryInterface {
    suspend fun getAllFoldersFlow(): Flow<List<FolderData>>
    suspend fun getFolderById(id: Long): FolderData?
    suspend fun getFolderByIdFlow(id: Long): Flow<FolderData?>
    suspend fun getFoldersWithReceipts(): Flow<List<FolderWithReceiptsData>>
    suspend fun insertFolder(folderData: FolderData)
    suspend fun insertNewFolder(folderData: FolderData)
    suspend fun deleteFolderById(id: Long)
}