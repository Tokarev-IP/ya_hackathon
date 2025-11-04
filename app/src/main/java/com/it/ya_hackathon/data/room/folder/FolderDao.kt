package com.it.ya_hackathon.data.room.folder

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.it.ya_hackathon.data.room.FolderEntity
import com.it.ya_hackathon.data.room.FolderWithReceiptsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Upsert
    suspend fun insertFolder(folder: FolderEntity)

    @Query("SELECT * FROM folder_data")
    fun getAllFoldersFlow(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folder_data WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?

    @Query("SELECT * FROM folder_data WHERE id = :id")
    fun getFolderByIdFlow(id: Long): Flow<FolderEntity?>

    @Transaction
    @Query("SELECT * FROM folder_data")
    fun getFoldersWithReceipts(): Flow<List<FolderWithReceiptsEntity>>

    @Query("DELETE FROM folder_data WHERE id = :id")
    suspend fun deleteFolderById(id: Long)

}