package com.it.ya_hackathon.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.it.ya_hackathon.data.room.folder.FolderDao
import com.it.ya_hackathon.data.room.receipt.ReceiptDao

@Database(
    entities = [
        ReceiptEntity::class,
        OrderEntity::class,
        FolderEntity::class,
    ],
    version = 1
)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
    abstract fun folderDao(): FolderDao
}
