package com.example.zusanfoodie.recipient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.zusanfoodie.RecipientReservation
import com.example.zusanfoodie.RecipientReservationsResponse
import com.example.zusanfoodie.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ReservationHistoryDialog(
    recipientId: Int,
    onDismiss: () -> Unit
) {

    var reservations by remember {
        mutableStateOf<List<RecipientReservation>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ==========================================
    // LOAD RESERVATIONS FROM MYSQL
    // ==========================================

    LaunchedEffect(recipientId) {

        isLoading = true
        errorMessage = null

        RetrofitClient.apiService
            .getRecipientReservations(recipientId)
            .enqueue(

                object : Callback<RecipientReservationsResponse> {

                    override fun onResponse(
                        call: Call<RecipientReservationsResponse>,
                        response: Response<RecipientReservationsResponse>
                    ) {

                        isLoading = false

                        val result = response.body()

                        if (
                            response.isSuccessful &&
                            result?.success == true
                        ) {

                            reservations = result.reservations

                        } else {

                            reservations = emptyList()

                            errorMessage =
                                result?.message
                                    ?: "Unable to load reservations."
                        }
                    }


                    override fun onFailure(
                        call: Call<RecipientReservationsResponse>,
                        t: Throwable
                    ) {

                        isLoading = false
                        reservations = emptyList()

                        errorMessage =
                            t.message
                                ?: "Unable to connect to server."
                    }
                }
            )
    }


    // ==========================================
    // DIALOG
    // ==========================================

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Reservation History")
        },

        text = {

            when {

                // ==================================
                // LOADING
                // ==================================

                isLoading -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                    ) {

                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(35.dp),
                            color = Color(0xFF3035A4)
                        )
                    }
                }


                // ==================================
                // ERROR
                // ==================================

                errorMessage != null -> {

                    Text(
                        text = errorMessage
                            ?: "Unable to load reservations.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }


                // ==================================
                // EMPTY
                // ==================================

                reservations.isEmpty() -> {

                    Text(
                        text =
                            "No reservations yet. Select a food and tap Reserve to reserve it."
                    )
                }


                // ==================================
                // RESERVATION LIST
                // ==================================

                else -> {

                    LazyColumn(

                        modifier = Modifier
                            .heightIn(max = 450.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)

                    ) {

                        items(
                            items = reservations,
                            key = { it.reservation_id }
                        ) { reservation ->


                            val pickupTime =

                                if (
                                    reservation.pickup_end_time
                                        .isNullOrBlank()
                                ) {

                                    reservation.pickup_start_time

                                } else {

                                    "${reservation.pickup_start_time} - ${reservation.pickup_end_time}"
                                }


                            // ==================================
                            // DISPLAY STATUS
                            // ==================================

                            val displayStatus =

                                when (
                                    reservation.pickup_status
                                        ?.lowercase()
                                ) {

                                    "upcoming" ->
                                        "Waiting for Collector"

                                    "collected" ->
                                        "On the Way"

                                    "completed" ->
                                        "Completed"

                                    else ->
                                        reservation.status
                                            .replaceFirstChar {
                                                it.uppercase()
                                            }
                                }


                            Card(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(10.dp),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Color(0xFFF4F4FA)
                                    )
                            ) {

                                Column(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),

                                    verticalArrangement =
                                        Arrangement.spacedBy(4.dp)
                                ) {


                                    // FOOD

                                    Text(
                                        text =
                                            reservation.food_name,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium
                                    )


                                    // QUANTITY

                                    Text(
                                        text =
                                            "Quantity: " +
                                                    "${reservation.quantity} " +
                                                    reservation.unit,

                                        fontSize =
                                            13.sp
                                    )


                                    // DONOR

                                    Text(
                                        text =
                                            "Donor: ${reservation.donor_name}",

                                        fontSize =
                                            13.sp
                                    )


                                    // PICKUP LOCATION

                                    Text(
                                        text =
                                            "Pickup Location: " +
                                                    reservation.pickup_location,

                                        fontSize =
                                            13.sp
                                    )


                                    // DELIVERY LOCATION

                                    Text(
                                        text =
                                            "Delivery Location: " +
                                                    reservation.delivery_location,

                                        fontSize =
                                            13.sp
                                    )


                                    // PICKUP DATE + TIME

                                    Text(
                                        text =
                                            "Pickup: " +
                                                    "${reservation.pickup_date}, " +
                                                    pickupTime,

                                        fontSize =
                                            13.sp
                                    )


                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )


                                    // STATUS

                                    Text(
                                        text =
                                            "Status: $displayStatus",

                                        color =
                                            Color(0xFF3035A4),

                                        fontSize =
                                            13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },


        // ==========================================
        // CLOSE BUTTON
        // ==========================================

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}