package com.it.ya_hackathon.data.room

import androidx.room.Room
import com.it.ya_hackathon.data.room.folder.FolderAdapter
import com.it.ya_hackathon.data.room.folder.FolderAdapterInterface
import com.it.ya_hackathon.data.room.folder.FolderDbRepository
import com.it.ya_hackathon.data.room.folder.FolderDbRepositoryInterface
import com.it.ya_hackathon.data.room.receipt.ReceiptAdapter
import com.it.ya_hackathon.data.room.receipt.ReceiptAdapterInterface
import com.it.ya_hackathon.data.room.receipt.ReceiptDbRepository
import com.it.ya_hackathon.data.room.receipt.ReceiptDbRepositoryInterface
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val roomDbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MyRoomDatabase::class.java,
            "room_database"
        ).build()
    }

    single { get<MyRoomDatabase>().receiptDao() }
    single { get<MyRoomDatabase>().folderDao() }

    factoryOf(::ReceiptAdapter) { bind<ReceiptAdapterInterface>() }
    factoryOf(::ReceiptDbRepository) { bind<ReceiptDbRepositoryInterface>() }

    factoryOf(::FolderAdapter) { bind<FolderAdapterInterface>() }
    factoryOf(::FolderDbRepository) { bind<FolderDbRepositoryInterface>() }

}