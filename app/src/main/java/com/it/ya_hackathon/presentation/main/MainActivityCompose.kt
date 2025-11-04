package com.it.ya_hackathon.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.it.ya_hackathon.presentation.receipt.ReceiptViewModel
import com.it.ya_hackathon.presentation.receipt.ReceiptCompose
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainActivityCompose(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: MainActivityNavHostDestinations = MainActivityNavHostDestinations.ReceiptNav,
){
    NavHost (
        modifier = modifier.fillMaxSize(),
        startDestination = startDestination,
        navController = navHostController,
    ){
        composable<MainActivityNavHostDestinations.ReceiptNav> {
            val receiptViewModel: ReceiptViewModel = koinViewModel()
            ReceiptCompose(
                receiptViewModel = receiptViewModel,
            )
        }
    }
}

sealed interface MainActivityNavHostDestinations{
    @Serializable
    object ReceiptNav: MainActivityNavHostDestinations
}