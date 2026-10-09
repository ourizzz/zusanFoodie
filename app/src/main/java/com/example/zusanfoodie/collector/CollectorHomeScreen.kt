package com.example.zusanfoodie.collector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun CollectorHomeScreen(
    collectorName: String,
    pickups: List<CollectorPickup>,
    onViewDetailsClick: (CollectorPickup) -> Unit = {},
    onAvailablePickupsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
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

        // ================= LOGOUT =================

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    onLogoutClick()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 10.dp,
                    vertical = 0.dp
                )
            ) {

                Text(
                    text = "LOGOUT",
                    color = Color(0xFF3036B5),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ================= GREETING =================

        Text(
            text = "Hi $collectorName!",
            color = Color.White,
            fontSize = 22.sp
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "Your list of upcoming pickups",
            color = Color.White,
            fontSize = 14.sp
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // ================= AVAILABLE PICKUPS BUTTON =================

        Button(
            onClick = {
                onAvailablePickupsClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {

            Text(
                text = "AVAILABLE PICKUPS",
                color = Color(0xFF3036B5),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ================= PICKUP LIST =================

        if (pickups.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "No upcoming pickups",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    bottom = 25.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    25.dp
                )
            ) {

                items(
                    items = pickups,
                    key = { pickup ->
                        pickup.pickup_id
                    }
                ) { pickup ->

                    PickupCard(
                        pickup = pickup,
                        onViewDetailsClick = {
                            onViewDetailsClick(pickup)
                        }
                    )
                }
            }
        }


        // ================= BOTTOM NAVIGATION =================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // HOME

            Button(
                onClick = {
                    // Already on Home
                },
                modifier = Modifier
                    .width(105.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7E8195)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.home_icon
                    ),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            }


            // HISTORY

            Button(
                onClick = {
                    onHistoryClick()
                },
                modifier = Modifier
                    .width(105.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.history_icon
                    ),
                    contentDescription = "History",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}


// =============================================
// PICKUP CARD
// =============================================

@Composable
private fun PickupCard(
    pickup: CollectorPickup,
    onViewDetailsClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(23.dp)
            )
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 27.dp,
                    top = 27.dp,
                    end = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(
                13.dp
            )
        ) {

            // FOOD

            PickupRow(
                icon = R.drawable.food_icon,
                value = pickup.food_name
            )


            // PICKUP LOCATION

            PickupRow(
                icon = R.drawable.location_icon,
                value = pickup.pickup_location
            )


            // TIME

            PickupRow(
                icon = R.drawable.time_icon,
                value = formatPickupTime(
                    pickup.pickup_start_time,
                    pickup.pickup_end_time
                )
            )


            // DATE

            PickupRow(
                icon = R.drawable.date_icon,
                value = pickup.pickup_date
            )
        }


        // VIEW DETAILS

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 18.dp,
                    bottom = 11.dp
                )
                .width(70.dp)
                .height(27.dp)
                .background(
                    Color(0xFFA8B4CF),
                    RoundedCornerShape(8.dp)
                )
                .clickable {
                    onViewDetailsClick()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "View\nDetails",
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontSize = 9.sp,
                lineHeight = 9.sp
            )
        }
    }
}


// =============================================
// CARD ROW
// =============================================

@Composable
private fun PickupRow(
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
            maxLines = 1
        )
    }
}


// =============================================
// TIME FORMAT
// =============================================

private fun formatPickupTime(
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