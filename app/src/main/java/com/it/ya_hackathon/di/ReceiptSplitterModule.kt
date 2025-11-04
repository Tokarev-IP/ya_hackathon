package com.it.ya_hackathon.di

import com.it.ya_hackathon.data.firebase.UserAuthenticator
import com.it.ya_hackathon.data.firebase.UserAuthenticatorInterface
import com.it.ya_hackathon.data.services.ImageConverter
import com.it.ya_hackathon.data.services.ImageConverterInterface
import com.it.ya_hackathon.data.services.ImageLabelingKit
import com.it.ya_hackathon.data.services.ImageLabelingKitInterface
import com.it.ya_hackathon.data.services.ReceiptJsonService
import com.it.ya_hackathon.data.services.ReceiptJsonServiceInterface
import com.it.ya_hackathon.domain.report.FolderPdfReportCreator
import com.it.ya_hackathon.domain.report.FolderPdfReportCreatorInterface
import com.it.ya_hackathon.domain.report.FolderReportCreator
import com.it.ya_hackathon.domain.report.FolderReportCreatorInterface
import com.it.ya_hackathon.domain.report.FolderTextReportCreator
import com.it.ya_hackathon.domain.report.FolderTextReportCreatorInterface
import com.it.ya_hackathon.domain.report.OrderPdfReportCreator
import com.it.ya_hackathon.domain.report.OrderPdfReportCreatorInterface
import com.it.ya_hackathon.domain.report.OrderReportCreator
import com.it.ya_hackathon.domain.report.OrderReportCreatorInterface
import com.it.ya_hackathon.domain.report.OrderTextReportCreator
import com.it.ya_hackathon.domain.report.OrderTextReportCreatorInterface
import com.it.ya_hackathon.domain.service.OrderDataService
import com.it.ya_hackathon.domain.service.OrderDataServiceInterface
import com.it.ya_hackathon.domain.service.OrderDataSplitService
import com.it.ya_hackathon.domain.service.OrderDataSplitServiceInterface
import com.it.ya_hackathon.domain.service.ReceiptDataService
import com.it.ya_hackathon.domain.service.ReceiptDataServiceInterface
import com.it.ya_hackathon.domain.usecase.AllFoldersUseCase
import com.it.ya_hackathon.domain.usecase.AllFoldersUseCaseInterface
import com.it.ya_hackathon.domain.usecase.AllReceiptsUseCase
import com.it.ya_hackathon.domain.usecase.AllReceiptsUseCaseInterface
import com.it.ya_hackathon.domain.usecase.CreateReceiptUseCase
import com.it.ya_hackathon.domain.usecase.CreateReceiptUseCaseInterface
import com.it.ya_hackathon.domain.usecase.EditReceiptUseCase
import com.it.ya_hackathon.domain.usecase.EditReceiptUseCaseInterface
import com.it.ya_hackathon.domain.usecase.FolderReceiptsUseCase
import com.it.ya_hackathon.domain.usecase.FolderReceiptsUseCaseInterface
import com.it.ya_hackathon.domain.usecase.SplitReceiptUseCase
import com.it.ya_hackathon.domain.usecase.SplitReceiptUseCaseInterface
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import com.it.ya_hackathon.presentation.screens.all_receipts.AllReceiptsViewModel
import com.it.ya_hackathon.presentation.screens.create_receipt.CreateReceiptViewModel
import com.it.ya_hackathon.presentation.screens.edit_receipt.EditReceiptViewModel
import com.it.ya_hackathon.presentation.screens.folder_receipts.FolderReceiptsViewModel
import com.it.ya_hackathon.presentation.screens.show_reports.ShowReportsViewModel
import com.it.ya_hackathon.presentation.screens.split_receipt_for_all.SplitReceiptForAllViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val receiptSplitterModule = module {
    // Services & Utilities
    factoryOf(::ImageConverter) { bind<ImageConverterInterface>() }
    factoryOf(::ImageLabelingKit) { bind<ImageLabelingKitInterface>() }
    factoryOf(::ReceiptJsonService) { bind<ReceiptJsonServiceInterface>() }
    factoryOf(::UserAuthenticator) { bind<UserAuthenticatorInterface>() }

    // Use Cases
    factoryOf(::SplitReceiptUseCase) { bind<SplitReceiptUseCaseInterface>() }
    factoryOf(::AllReceiptsUseCase) { bind<AllReceiptsUseCaseInterface>() }
    factoryOf(::EditReceiptUseCase) { bind<EditReceiptUseCaseInterface>() }
    factoryOf(::CreateReceiptUseCase) { bind<CreateReceiptUseCaseInterface>() }
    factoryOf(::AllFoldersUseCase) { bind<AllFoldersUseCaseInterface>() }
    factoryOf(::FolderReceiptsUseCase) { bind<FolderReceiptsUseCaseInterface>() }

    // Business Logic
    factoryOf(::OrderDataService) { bind<OrderDataServiceInterface>() }
    factoryOf(::OrderDataSplitService) { bind<OrderDataSplitServiceInterface>() }
    factoryOf(::ReceiptDataService) { bind<ReceiptDataServiceInterface>() }

    // Reports
    factoryOf(::FolderPdfReportCreator) { bind<FolderPdfReportCreatorInterface>() }
    factoryOf(::OrderPdfReportCreator) { bind<OrderPdfReportCreatorInterface>() }
    factoryOf(::FolderTextReportCreator) { bind<FolderTextReportCreatorInterface>() }
    factoryOf(::OrderTextReportCreator) { bind<OrderTextReportCreatorInterface>() }
    factoryOf(::FolderReportCreator) { bind<FolderReportCreatorInterface>() }
    factoryOf(::OrderReportCreator) { bind<OrderReportCreatorInterface>() }

    // View Model
    viewModelOf(::ReceiptViewModel)
    viewModelOf(::AllReceiptsViewModel)
    viewModelOf(::EditReceiptViewModel)
    viewModelOf(::SplitReceiptForAllViewModel)
    viewModelOf(::CreateReceiptViewModel)
    viewModelOf(::FolderReceiptsViewModel)
    viewModelOf(::ShowReportsViewModel)
}