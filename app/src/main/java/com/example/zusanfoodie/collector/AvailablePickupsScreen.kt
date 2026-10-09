package com.example.zusanfoodie.collector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zusanfoodie.CollectorPickup
import com.example.zusanfoodie.R


@Composable
fun AvailablePickupsScreen(
    pickups: List<CollectorPickup>,
    isLoading: Boolean = false,
    onAcceptClick: (CollectorPickup) -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3036B5))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 42.dp)
            .padding(top = 30.dp, bottom = 18.dp)
    ) {

        // ==========================================
        // BACK BUTTON
        // ==========================================

        Button(
            onClick = onBackClick,
            modifier = Modifier
                .width(45.dp)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {

            Text(
                text = "←",
                color = Color(0xFF3036B5),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        // ==========================================
        // HEADER
        // ==========================================

        Text(
            text = "Available Pickups",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "Choose a pickup you would like to collect",
            color = Color.White,
            fontSize = 14.sp
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        // ==========================================
        // LOADING
        // ==========================================

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.White
                )
            }

        } else if (pickups.isEmpty()) {

            // ======================================
            // NO AVAILABLE PICKUPS
            // ======================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "No available pickups",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )


                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )


                    Text(
                        text = "New pickup requests will appear here.",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            // ======================================
            // AVAILABLE PICKUP LIST
            // ======================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    bottom = 25.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    20.dp
                )
            ) {

                items(
                    items = pickups,
                    key = { pickup ->
                        pickup.pickup_id
                    }
                ) { pickup ->

                    AvailablePickupCard(
                        pickup = pickup,
                        onAcceptClick = {
                            onAcceptClick(pickup)
                        }
                    )
                }
            }
        }
    }
}


// =============================================
// AVAILABLE PICKUP CARD
// =============================================

@Composable
private fun AvailablePickupCard(
    pickup: CollectorPickup,
    onAcceptClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(23.dp)
            )
            .padding(
                start = 25.dp,
                end = 25.dp,
                top = 23.dp,
                bottom = 18.dp
            )
    ) {

        // ==========================================
        // FOOD
        // ==========================================

        AvailablePickupRow(
            icon = R.drawable.food_icon,
            value = pickup.food_name
        )


        Spacer(
            modifier = Modifier.height(13.dp)
        )


        // ==========================================
        // QUANTITY
        // ==========================================

        Text(
            text = "Quantity: ${pickup.quantity} ${pickup.unit}",
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            fontSize = 10.sp
        )


        Spacer(
            modifier = Modifier.height(13.dp)
        )


        // ==========================================
        // PICKUP LOCATION
        // ==========================================

        AvailablePickupRow(
            icon = R.drawable.location_icon,
            value = pickup.pickup_location
        )


        Spacer(
            modifier = Modifier.height(13.dp)
        )


        // ==========================================
        // DELIVERY LOCATION
        // ==========================================

        Text(
            text = "Deliver to: ${pickup.delivery_location}",
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            fontSize = 10.sp
        )


        Spacer(
            modifier = Modifier.height(13.dp)
        )


        // ==========================================
        // TIME
        // ==========================================

        AvailablePickupRow(
            icon = R.drawable.time_icon,
            value = formatAvailablePickupTime(
                pickup.pickup_start_time,
                pickup.pickup_end_time
            )
        )


        Spacer(
            modifier = Modifier.height(13.dp)
        )


        // ==========================================
        // DATE
        // ==========================================

        AvailablePickupRow(
            icon = R.drawable.date_icon,
            value = pickup.pickup_date
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ==========================================
        // ACCEPT BUTTON
        // ==========================================

        Button(
            onClick = onAcceptClick,
            modifier = Modifier
                .align(Alignment.End)
                .width(95.dp)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3036B5)
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {

            Text(
                text = "ACCEPT",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// =============================================
// CARD ROW
// =============================================

@Composable
private fun AvailablePickupRow(
    icon: Int,
    value: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(
                id = icon
            ),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )


        Spacer(
            modifier = Modifier.width(10.dp)
        )


        Text(
            text = value,
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            fontSize = 10.sp,
            maxLines = 2
        )
    }
}


// =============================================
// TIME FORMAT
// =============================================

private fun formatAvailablePickupTime(
    startTime: String,
    endTime: String?
): String {

    return if (
        endTime.isNullOrBlank()
    ) {

        startTime

    } else {

        "$startTime - $endTime"
    }
}