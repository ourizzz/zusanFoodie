package com.example.zusanfoodie

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

// ==========================================
// COLLECTOR
// ==========================================

import com.example.zusanfoodie.collector.AvailablePickupsScreen
import com.example.zusanfoodie.collector.CollectorHistoryScreen
import com.example.zusanfoodie.collector.CollectorHomeScreen
import com.example.zusanfoodie.collector.HistoryItem
import com.example.zusanfoodie.collector.PickupDetails
import com.example.zusanfoodie.collector.PickupDetailsScreen

// ==========================================
// RECIPIENT
// ==========================================

import com.example.zusanfoodie.recipient.ConfirmReservationScreen
import com.example.zusanfoodie.recipient.FoodFilter
import com.example.zusanfoodie.recipient.RecipientFood
import com.example.zusanfoodie.recipient.RecipientHomeScreen
import com.example.zusanfoodie.recipient.ReservationHistoryDialog
import com.example.zusanfoodie.recipient.SearchFilterScreen

// ==========================================
// DONOR
// ==========================================

import com.example.zusanfoodie.donor.DonorApp

// ==========================================
// THEME
// ==========================================

import com.example.zusanfoodie.ui.theme.ZusanFoodieTheme

// ==========================================
// RETROFIT
// ==========================================

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            ZusanFoodieTheme {

                // ==========================================
                // CURRENT SCREEN
                // ==========================================

                var currentScreen by remember {
                    mutableStateOf("login")
                }


                // ==========================================
                // LOGGED IN USER
                // ==========================================

                var loggedInUserId by remember {
                    mutableStateOf<Int?>(null)
                }

                var loggedInUserName by remember {
                    mutableStateOf("")
                }

                var loggedInUserRole by remember {
                    mutableStateOf("")
                }


                // ==========================================
                // COLLECTOR STATE
                // ==========================================

                var collectorPickups by remember {
                    mutableStateOf<List<CollectorPickup>>(
                        emptyList()
                    )
                }

                var availablePickups by remember {
                    mutableStateOf<List<CollectorPickup>>(
                        emptyList()
                    )
                }

                var availablePickupsLoading by remember {
                    mutableStateOf(false)
                }

                var collectorHistory by remember {
                    mutableStateOf<List<CollectorPickup>>(
                        emptyList()
                    )
                }

                var selectedPickup by remember {
                    mutableStateOf<CollectorPickup?>(
                        null
                    )
                }


                // ==========================================
                // RECIPIENT STATE
                // ==========================================

                var selectedRecipientFood by remember {
                    mutableStateOf<RecipientFood?>(
                        null
                    )
                }

                var recipientActiveFilter by remember {
                    mutableStateOf(
                        FoodFilter()
                    )
                }

                var showRecipientReservationHistory by remember {
                    mutableStateOf(false)
                }

                var recipientFoods by remember {
                    mutableStateOf<List<RecipientFoodListing>>(
                        emptyList()
                    )
                }

                var recipientFoodsLoading by remember {
                    mutableStateOf(false)
                }


                // ==========================================
                // LOAD COLLECTOR PICKUPS
                // ==========================================

                fun loadCollectorPickups() {

                    val collectorId =
                        loggedInUserId

                    if (collectorId == null) {

                        collectorPickups =
                            emptyList()

                        return
                    }

                    RetrofitClient
                        .apiService
                        .getCollectorPickups(
                            collectorId
                        )
                        .enqueue(

                            object :
                                Callback<CollectorPickupsResponse> {

                                override fun onResponse(
                                    call: Call<CollectorPickupsResponse>,
                                    response: Response<CollectorPickupsResponse>
                                ) {

                                    val result =
                                        response.body()

                                    collectorPickups =

                                        if (
                                            response.isSuccessful &&
                                            result?.success == true
                                        ) {

                                            result.pickups

                                        } else {

                                            emptyList()
                                        }
                                }

                                override fun onFailure(
                                    call: Call<CollectorPickupsResponse>,
                                    t: Throwable
                                ) {

                                    collectorPickups =
                                        emptyList()
                                }
                            }
                        )
                }


                // ==========================================
                // LOAD AVAILABLE PICKUPS
                // ==========================================

                fun loadAvailablePickups() {

                    availablePickupsLoading =
                        true

                    RetrofitClient
                        .apiService
                        .getAvailablePickups()
                        .enqueue(

                            object :
                                Callback<CollectorPickupsResponse> {

                                override fun onResponse(
                                    call: Call<CollectorPickupsResponse>,
                                    response: Response<CollectorPickupsResponse>
                                ) {

                                    availablePickupsLoading =
                                        false

                                    val result =
                                        response.body()

                                    availablePickups =

                                        if (
                                            response.isSuccessful &&
                                            result?.success == true
                                        ) {

                                            result.pickups

                                        } else {

                                            emptyList()
                                        }
                                }

                                override fun onFailure(
                                    call: Call<CollectorPickupsResponse>,
                                    t: Throwable
                                ) {

                                    availablePickupsLoading =
                                        false

                                    availablePickups =
                                        emptyList()
                                }
                            }
                        )
                }


                // ==========================================
                // ASSIGN PICKUP TO COLLECTOR
                // ==========================================

                fun assignPickup(
                    pickup: CollectorPickup
                ) {

                    val collectorId =
                        loggedInUserId ?: return

                    RetrofitClient
                        .apiService
                        .assignPickup(
                            pickupId = pickup.pickup_id,
                            collectorId = collectorId
                        )
                        .enqueue(

                            object :
                                Callback<AssignPickupResponse> {

                                override fun onResponse(
                                    call: Call<AssignPickupResponse>,
                                    response: Response<AssignPickupResponse>
                                ) {

                                    val result =
                                        response.body()

                                    if (
                                        response.isSuccessful &&
                                        result?.success == true
                                    ) {

                                        availablePickups =
                                            availablePickups.filterNot {

                                                it.pickup_id ==
                                                        pickup.pickup_id
                                            }

                                        loadCollectorPickups()

                                        currentScreen =
                                            "collector_home"
                                    }
                                }

                                override fun onFailure(
                                    call: Call<AssignPickupResponse>,
                                    t: Throwable
                                ) {
                                    // Keep current screen
                                }
                            }
                        )
                }


                // ==========================================
                // LOAD COLLECTOR HISTORY
                // ==========================================

                fun loadCollectorHistory() {

                    val collectorId =
                        loggedInUserId

                    if (collectorId == null) {

                        collectorHistory =
                            emptyList()

                        return
                    }

                    RetrofitClient
                        .apiService
                        .getCollectorHistory(
                            collectorId
                        )
                        .enqueue(

                            object :
                                Callback<CollectorPickupsResponse> {

                                override fun onResponse(
                                    call: Call<CollectorPickupsResponse>,
                                    response: Response<CollectorPickupsResponse>
                                ) {

                                    val result =
                                        response.body()

                                    collectorHistory =

                                        if (
                                            response.isSuccessful &&
                                            result?.success == true
                                        ) {

                                            result.pickups

                                        } else {

                                            emptyList()
                                        }
                                }

                                override fun onFailure(
                                    call: Call<CollectorPickupsResponse>,
                                    t: Throwable
                                ) {

                                    collectorHistory =
                                        emptyList()
                                }
                            }
                        )
                }


                // ==========================================
                // LOAD RECIPIENT FOODS
                // ==========================================

                fun loadRecipientFoods() {

                    recipientFoodsLoading =
                        true

                    RetrofitClient
                        .apiService
                        .getRecipientFoods()
                        .enqueue(

                            object :
                                Callback<RecipientFoodsResponse> {

                                override fun onResponse(
                                    call: Call<RecipientFoodsResponse>,
                                    response: Response<RecipientFoodsResponse>
                                ) {

                                    recipientFoodsLoading =
                                        false

                                    val result =
                                        response.body()

                                    recipientFoods =

                                        if (
                                            response.isSuccessful &&
                                            result?.success == true
                                        ) {

                                            result.foods

                                        } else {

                                            emptyList()
                                        }
                                }

                                override fun onFailure(
                                    call: Call<RecipientFoodsResponse>,
                                    t: Throwable
                                ) {

                                    recipientFoodsLoading =
                                        false

                                    recipientFoods =
                                        emptyList()
                                }
                            }
                        )
                }


                // ==========================================
                // UPDATE COLLECTOR PICKUP STATUS
                // ==========================================

                fun updatePickupStatus(
                    pickup: CollectorPickup,
                    newStatus: String,
                    onSuccess: () -> Unit
                ) {

                    RetrofitClient
                        .apiService
                        .updatePickupStatus(
                            pickupId = pickup.pickup_id,
                            pickupStatus = newStatus
                        )
                        .enqueue(

                            object :
                                Callback<UpdatePickupStatusResponse> {

                                override fun onResponse(
                                    call: Call<UpdatePickupStatusResponse>,
                                    response: Response<UpdatePickupStatusResponse>
                                ) {

                                    val result =
                                        response.body()

                                    if (
                                        response.isSuccessful &&
                                        result?.success == true
                                    ) {

                                        onSuccess()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<UpdatePickupStatusResponse>,
                                    t: Throwable
                                ) {
                                    // Keep current screen
                                }
                            }
                        )
                }


                // ==========================================
                // MAIN NAVIGATION
                // ==========================================

                when (currentScreen) {


                    // ======================================
                    // LOGIN
                    // ======================================

                    "login" -> {

                        LoginScreen(

                            onLoginSuccess = {
                                    userId,
                                    name,
                                    role ->

                                loggedInUserId =
                                    userId

                                loggedInUserName =
                                    name

                                loggedInUserRole =
                                    role

                                when (role) {

                                    "collector" -> {

                                        currentScreen =
                                            "collector_home"
                                    }

                                    "recipient" -> {

                                        currentScreen =
                                            "recipient_home"
                                    }

                                    "donor" -> {

                                        currentScreen =
                                            "donor"
                                    }
                                }
                            }
                        )
                    }


                    // ======================================
                    // COLLECTOR HOME
                    // ======================================

                    "collector_home" -> {

                        LaunchedEffect(
                            loggedInUserId
                        ) {

                            loadCollectorPickups()
                        }

                        CollectorHomeScreen(

                            collectorName =
                                loggedInUserName,

                            pickups =
                                collectorPickups,

                            onViewDetailsClick = {
                                    pickup ->

                                selectedPickup =
                                    pickup

                                currentScreen =
                                    "collector_details"
                            },

                            onAvailablePickupsClick = {

                                currentScreen =
                                    "collector_available_pickups"
                            },

                            onHistoryClick = {

                                currentScreen =
                                    "collector_history"
                            },

                            onLogoutClick = {

                                loggedInUserId =
                                    null

                                loggedInUserName =
                                    ""

                                loggedInUserRole =
                                    ""

                                collectorPickups =
                                    emptyList()

                                availablePickups =
                                    emptyList()

                                collectorHistory =
                                    emptyList()

                                selectedPickup =
                                    null

                                currentScreen =
                                    "login"
                            }
                        )
                    }


                    // ======================================
                    // COLLECTOR AVAILABLE PICKUPS
                    // ======================================

                    "collector_available_pickups" -> {

                        LaunchedEffect(Unit) {

                            loadAvailablePickups()
                        }

                        AvailablePickupsScreen(

                            pickups =
                                availablePickups,

                            isLoading =
                                availablePickupsLoading,

                            onAcceptClick = {
                                    pickup ->

                                assignPickup(pickup)
                            },

                            onBackClick = {

                                currentScreen =
                                    "collector_home"
                            }
                        )
                    }


                    // ======================================
                    // COLLECTOR DETAILS
                    // ======================================

                    "collector_details" -> {

                        val pickup =
                            selectedPickup

                        if (pickup != null) {

                            val pickupTime =

                                if (
                                    pickup.pickup_end_time
                                        .isNullOrBlank()
                                ) {

                                    pickup.pickup_start_time

                                } else {

                                    "${pickup.pickup_start_time} - ${pickup.pickup_end_time}"
                                }

                            val details =
                                PickupDetails(

                                    food =
                                        pickup.food_name,

                                    total =
                                        "${pickup.quantity} ${pickup.unit}",

                                    pickupLocation =
                                        pickup.pickup_location,

                                    deliveryLocation =
                                        pickup.delivery_location,

                                    pickupDate =
                                        pickup.pickup_date,

                                    pickupTime =
                                        pickupTime,

                                    donorName =
                                        pickup.donor_name,

                                    donorPhone =
                                        pickup.donor_phone
                                            ?: "",

                                    recipientName =
                                        pickup.recipient_name,

                                    recipientPhone =
                                        pickup.recipient_phone
                                            ?: ""
                                )

                            PickupDetailsScreen(

                                details =
                                    details,

                                isCollected =
                                    pickup.pickup_status ==
                                            "collected",

                                isHistoryView =
                                    false,

                                onCollectedClick = {

                                    updatePickupStatus(

                                        pickup =
                                            pickup,

                                        newStatus =
                                            "collected",

                                        onSuccess = {

                                            val updatedPickup =
                                                pickup.copy(
                                                    pickup_status =
                                                        "collected"
                                                )

                                            selectedPickup =
                                                updatedPickup

                                            collectorPickups =
                                                collectorPickups.map {

                                                    if (
                                                        it.pickup_id ==
                                                        pickup.pickup_id
                                                    ) {

                                                        updatedPickup

                                                    } else {

                                                        it
                                                    }
                                                }
                                        }
                                    )
                                },

                                onDeliveredClick = {

                                    updatePickupStatus(

                                        pickup =
                                            pickup,

                                        newStatus =
                                            "completed",

                                        onSuccess = {

                                            collectorPickups =
                                                collectorPickups.filter {

                                                    it.pickup_id !=
                                                            pickup.pickup_id
                                                }

                                            selectedPickup =
                                                null

                                            currentScreen =
                                                "collector_history"
                                        }
                                    )
                                },

                                onBackClick = {

                                    selectedPickup =
                                        null

                                    currentScreen =
                                        "collector_home"
                                }
                            )

                        } else {

                            LaunchedEffect(Unit) {

                                currentScreen =
                                    "collector_home"
                            }
                        }
                    }


                    // ======================================
                    // COLLECTOR HISTORY
                    // ======================================

                    "collector_history" -> {

                        LaunchedEffect(
                            loggedInUserId
                        ) {

                            loadCollectorHistory()
                        }

                        val historyItems =
                            collectorHistory.map {
                                    pickup ->

                                val time =

                                    if (
                                        pickup.pickup_end_time
                                            .isNullOrBlank()
                                    ) {

                                        pickup.pickup_start_time

                                    } else {

                                        "${pickup.pickup_start_time} - ${pickup.pickup_end_time}"
                                    }

                                HistoryItem(

                                    id =
                                        pickup.pickup_id
                                            .toString(),

                                    foodName =
                                        pickup.food_name,

                                    location =
                                        pickup.pickup_location,

                                    time =
                                        time,

                                    date =
                                        pickup.pickup_date
                                )
                            }

                        CollectorHistoryScreen(

                            historyList =
                                historyItems,

                            onBackClick = {

                                currentScreen =
                                    "collector_home"
                            },

                            onViewDetailsClick = {
                                    historyItem ->

                                val pickup =
                                    collectorHistory.find {

                                        it.pickup_id
                                            .toString() ==
                                                historyItem.id
                                    }

                                if (pickup != null) {

                                    selectedPickup =
                                        pickup

                                    currentScreen =
                                        "collector_history_details"
                                }
                            }
                        )
                    }


                    // ======================================
                    // COLLECTOR HISTORY DETAILS
                    // ======================================

                    "collector_history_details" -> {

                        val pickup =
                            selectedPickup

                        if (pickup != null) {

                            val pickupTime =

                                if (
                                    pickup.pickup_end_time
                                        .isNullOrBlank()
                                ) {

                                    pickup.pickup_start_time

                                } else {

                                    "${pickup.pickup_start_time} - ${pickup.pickup_end_time}"
                                }

                            val details =
                                PickupDetails(

                                    food =
                                        pickup.food_name,

                                    total =
                                        "${pickup.quantity} ${pickup.unit}",

                                    pickupLocation =
                                        pickup.pickup_location,

                                    deliveryLocation =
                                        pickup.delivery_location,

                                    pickupDate =
                                        pickup.pickup_date,

                                    pickupTime =
                                        pickupTime,

                                    donorName =
                                        pickup.donor_name,

                                    donorPhone =
                                        pickup.donor_phone
                                            ?: "",

                                    recipientName =
                                        pickup.recipient_name,

                                    recipientPhone =
                                        pickup.recipient_phone
                                            ?: ""
                                )

                            PickupDetailsScreen(

                                details =
                                    details,

                                isCollected =
                                    true,

                                isHistoryView =
                                    true,

                                onBackClick = {

                                    selectedPickup =
                                        null

                                    currentScreen =
                                        "collector_history"
                                }
                            )

                        } else {

                            LaunchedEffect(Unit) {

                                currentScreen =
                                    "collector_history"
                            }
                        }
                    }


                    // ======================================
                    // RECIPIENT HOME
                    // ======================================

                    "recipient_home" -> {

                        LaunchedEffect(Unit) {

                            loadRecipientFoods()
                        }

                        RecipientHomeScreen(

                            databaseFoods =
                                recipientFoods,

                            isLoading =
                                recipientFoodsLoading,

                            onReserveClick = {
                                    food ->

                                selectedRecipientFood =
                                    food

                                currentScreen =
                                    "recipient_reservation"
                            },

                            onSearchClick = {

                                currentScreen =
                                    "recipient_search"
                            },

                            onReservationClick = {

                                showRecipientReservationHistory =
                                    true
                            },

                            onLogoutClick = {

                                loggedInUserId =
                                    null

                                loggedInUserName =
                                    ""

                                loggedInUserRole =
                                    ""

                                selectedRecipientFood =
                                    null

                                recipientFoods =
                                    emptyList()

                                recipientActiveFilter =
                                    FoodFilter()

                                showRecipientReservationHistory =
                                    false

                                currentScreen =
                                    "login"
                            },

                            activeFilter =
                                recipientActiveFilter
                        )
                    }


                    // ======================================
                    // RECIPIENT SEARCH
                    // ======================================

                    "recipient_search" -> {

                        SearchFilterScreen(

                            onHomeClick = {

                                currentScreen =
                                    "recipient_home"
                            },

                            onReservationsClick = {

                                showRecipientReservationHistory =
                                    true
                            },

                            onApplyFilter = {
                                    filter ->

                                recipientActiveFilter =
                                    filter

                                currentScreen =
                                    "recipient_home"
                            }
                        )
                    }


                    // ======================================
                    // RECIPIENT RESERVATION
                    // ======================================

                    "recipient_reservation" -> {

                        val food =
                            selectedRecipientFood

                        val recipientId =
                            loggedInUserId

                        if (
                            food != null &&
                            recipientId != null
                        ) {

                            ConfirmReservationScreen(

                                food =
                                    food,

                                recipientId =
                                    recipientId,

                                recipientAccountName =
                                    loggedInUserName,

                                onBackClick = {

                                    currentScreen =
                                        "recipient_home"
                                },

                                onHomeClick = {

                                    currentScreen =
                                        "recipient_home"
                                },

                                onSearchClick = {

                                    currentScreen =
                                        "recipient_search"
                                },

                                onReservationsClick = {

                                    showRecipientReservationHistory =
                                        true
                                },

                                onReservationSuccess = {

                                    loadRecipientFoods()
                                }
                            )

                        } else {

                            LaunchedEffect(Unit) {

                                currentScreen =
                                    "recipient_home"
                            }
                        }
                    }


                    // ======================================
                    // DONOR
                    // ======================================

                    "donor" -> {

                        val donorId =
                            loggedInUserId

                        if (donorId != null) {

                            DonorApp(

                                donorId =
                                    donorId,

                                donorName =
                                    loggedInUserName,

                                onLogoutClick = {

                                    loggedInUserId =
                                        null

                                    loggedInUserName =
                                        ""

                                    loggedInUserRole =
                                        ""

                                    currentScreen =
                                        "login"
                                }
                            )

                        } else {

                            LaunchedEffect(Unit) {

                                currentScreen =
                                    "login"
                            }
                        }
                    }
                }


                // ==========================================
                // RECIPIENT RESERVATION HISTORY
                // ==========================================

                if (
                    showRecipientReservationHistory
                ) {

                    val recipientId =
                        loggedInUserId

                    if (recipientId != null) {

                        ReservationHistoryDialog(

                            recipientId =
                                recipientId,

                            onDismiss = {

                                showRecipientReservationHistory =
                                    false
                            }
                        )
                    }
                }
            }
        }
    }
}