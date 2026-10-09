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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zusanfoodie.R


// =========================
// COLORS
// =========================

private val BackgroundBlue = Color(0xFF3035B5)
private val CompletedGreen = Color(0xFFA6CDAA)
private val DetailButton = Color(0xFFA8B4CF)


// =========================
// HISTORY DATA
// =========================

data class HistoryItem(
    val id: String = "",
    val foodName: String = "",
    val location: String = "",
    val time: String = "",
    val date: String = ""
)


// =========================
// HISTORY SCREEN
// =========================

@Composable
fun CollectorHistoryScreen(
    historyList: List<HistoryItem> = emptyList(),
    onBackClick: () -> Unit = {},
    onViewDetailsClick: (HistoryItem) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 40.dp)
            .padding(top = 30.dp, bottom = 18.dp)
    ) {


        // =========================
        // HISTORY LIST
        // =========================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                bottom = 30.dp
            ),

            verticalArrangement = Arrangement.spacedBy(38.dp)
        ) {

            items(historyList) { history ->

                HistoryCard(
                    history = history,
                    onViewDetailsClick = {
                        onViewDetailsClick(history)
                    }
                )
            }
        }


        // =========================
        // BOTTOM NAVIGATION
        // =========================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // HOME

            Button(
                onClick = {
                    onBackClick()
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
                        id = R.drawable.home_icon
                    ),
                    contentDescription = "Home",
                    modifier = Modifier.size(25.dp)
                )
            }


            // HISTORY

            Button(
                onClick = {
                    // Already on History
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
                        id = R.drawable.history_icon
                    ),
                    contentDescription = "History",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}


// =========================
// ONE HISTORY CARD
// =========================

@Composable
private fun HistoryCard(
    history: HistoryItem,
    onViewDetailsClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(23.dp)
            )
    ) {

        // COMPLETED LABEL

        Box(
            modifier = Modifier
                .padding(
                    start = 27.dp,
                    top = 12.dp
                )
                .width(120.dp)
                .height(27.dp)
                .background(
                    color = CompletedGreen,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "COMPLETED",
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }


        // INFORMATION

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 27.dp,
                    top = 57.dp,
                    end = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            HistoryRow(
                icon = R.drawable.food_icon,
                value = history.foodName
            )

            HistoryRow(
                icon = R.drawable.location_icon,
                value = history.location
            )

            HistoryRow(
                icon = R.drawable.time_icon,
                value = history.time
            )

            HistoryRow(
                icon = R.drawable.date_icon,
                value = history.date
            )
        }


        // VIEW DETAILS BUTTON

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
                    color = DetailButton,
                    shape = RoundedCornerShape(8.dp)
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


// =========================
// INFORMATION ROW
// =========================

@Composable
private fun HistoryRow(
    icon: Int,
    value: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = icon),
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