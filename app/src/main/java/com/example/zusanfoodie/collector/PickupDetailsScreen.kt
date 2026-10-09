package com.example.zusanfoodie.collector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.zusanfoodie.R


// =====================
// COLORS
// =====================

private val BackgroundBlue = Color(0xFF3035B5)
private val PinkCard = Color(0xFFD2A3A4)
private val ButtonRed = Color(0xFFD75E58)
private val DisabledButton = Color(0xFFBDBDBD)


// =====================
// DATABASE DATA
// =====================

data class PickupDetails(
    val food: String = "",
    val total: String = "",
    val pickupLocation: String = "",
    val deliveryLocation: String = "",
    val pickupDate: String = "",
    val pickupTime: String = "",
    val donorName: String = "",
    val donorPhone: String = "",
    val recipientName: String = "",
    val recipientPhone: String = ""
)


// =====================
// SCREEN
// =====================

@Composable
fun PickupDetailsScreen(
    details: PickupDetails = PickupDetails(),

    isCollected: Boolean = false,

    isHistoryView: Boolean = false,

    onCollectedClick: () -> Unit = {},

    onDeliveredClick: () -> Unit = {},

    onBackClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =====================
            // BACK BUTTON
            // =====================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 20.dp,
                            top = 0.dp
                        )
                        .clickable {
                            onBackClick()
                        }
                )
            }


            // =====================
            // WHITE CARD
            // =====================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = 42.dp,
                        end = 42.dp,
                        bottom = 22.dp
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(
                        start = 25.dp,
                        end = 25.dp,
                        top = 24.dp,
                        bottom = 20.dp
                    )
            ) {

                // =====================
                // FOOD DETAILS
                // =====================

                SectionTitle(
                    icon = R.drawable.food_icon,
                    title = "FOOD DETAILS"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.3f)
                        .background(
                            PinkCard,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 13.dp,
                            vertical = 10.dp
                        ),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    DetailLine(
                        "Food",
                        details.food
                    )

                    DetailLine(
                        "Total",
                        details.total
                    )

                    DetailLine(
                        "Pickup Location",
                        details.pickupLocation
                    )

                    DetailLine(
                        "Delivery Location",
                        details.deliveryLocation
                    )

                    DetailLine(
                        "Pickup Date",
                        details.pickupDate
                    )

                    DetailLine(
                        "Pickup Time",
                        details.pickupTime
                    )
                }


                Spacer(modifier = Modifier.height(17.dp))


                // =====================
                // DONOR DETAILS
                // =====================

                SectionTitle(
                    icon = R.drawable.user_icon,
                    title = "DONOR DETAILS"
                )

                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.85f)
                        .background(
                            PinkCard,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 13.dp,
                            vertical = 7.dp
                        ),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    DetailLine(
                        "Name",
                        details.donorName
                    )

                    DetailLine(
                        "Phone",
                        details.donorPhone
                    )
                }


                Spacer(modifier = Modifier.height(17.dp))


                // =====================
                // RECIPIENT DETAILS
                // =====================

                SectionTitle(
                    icon = R.drawable.user_icon,
                    title = "RECIPIENT DETAILS"
                )

                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.85f)
                        .background(
                            PinkCard,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 13.dp,
                            vertical = 7.dp
                        ),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    DetailLine(
                        "Name",
                        details.recipientName
                    )

                    DetailLine(
                        "Phone",
                        details.recipientPhone
                    )
                }


                // =========================================
                // BUTTONS ONLY SHOW FROM HOME VIEW DETAILS
                // =========================================

                if (!isHistoryView) {

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(27.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // =====================
                        // COLLECTED
                        // =====================

                        StatusButton(
                            text = "COLLECTED",

                            enabled = !isCollected,

                            onClick = {
                                onCollectedClick()
                            }
                        )


                        // =====================
                        // DELIVERED
                        // =====================

                        StatusButton(
                            text = "DELIVERED",

                            enabled = isCollected,

                            onClick = {
                                onDeliveredClick()
                            }
                        )
                    }
                }
            }
        }
    }
}


// =====================
// SECTION TITLE
// =====================

@Composable
private fun SectionTitle(
    icon: Int,
    title: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = title,
            color = Color.Black,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
    }
}


// =====================
// DETAILS
// =====================

@Composable
private fun DetailLine(
    label: String,
    value: String
) {

    Text(
        text = "$label : $value",
        color = Color.Black,
        fontSize = 9.sp,
        fontFamily = FontFamily.Serif,
        maxLines = 1
    )
}


// =====================
// STATUS BUTTON
// =====================

@Composable
private fun StatusButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(101.dp)
            .fillMaxHeight()
            .background(
                color = if (enabled) {
                    ButtonRed
                } else {
                    DisabledButton
                },
                shape = RoundedCornerShape(9.dp)
            )
            .then(
                if (enabled) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black
        )
    }
}