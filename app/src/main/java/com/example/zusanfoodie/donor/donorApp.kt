package com.example.zusanfoodie.donor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun DonorApp(
    donorId: Int,
    donorName: String,
    onLogoutClick: () -> Unit
) {

    val navController =
        rememberNavController()

    val listingViewModel: ListingViewModel =
        viewModel()


    // ==========================================
    // LOAD DONOR LISTINGS FROM MYSQL
    // ==========================================

    LaunchedEffect(donorId) {

        listingViewModel.loadDonorListings(
            donorId
        )
    }


    // ==========================================
    // DONOR NAVIGATION
    // ==========================================

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {


        // ==========================================
        // DONOR HOME
        // ==========================================

        composable("home") {

            HomeScreen(
                vm = listingViewModel,
                nav = navController,
                donorName = donorName,
                onLogoutClick = onLogoutClick
            )
        }


        // ==========================================
        // MY LISTINGS
        // ==========================================

        composable("listings") {

            ListingsScreen(
                vm = listingViewModel,
                nav = navController
            )
        }


        // ==========================================
        // ADD FOOD LISTING
        // ==========================================

        composable("add") {

            AddListingScreen(
                vm = listingViewModel,
                nav = navController,
                donorId = donorId
            )
        }


        // ==========================================
        // LISTING DETAILS
        // ==========================================

        composable(

            route = "detail/{listingId}",

            arguments = listOf(

                navArgument("listingId") {

                    type =
                        NavType.IntType
                }
            )

        ) { backStackEntry ->

            val listingId =

                backStackEntry.arguments
                    ?.getInt("listingId")
                    ?: return@composable


            DetailScreen(
                id = listingId,
                vm = listingViewModel,
                nav = navController
            )
        }
    }
}