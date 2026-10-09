
package com.example.zusanfoodie.recipient
import com.example.zusanfoodie.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

private val NavBlue = Color(0xFF3035A4)
private val NavOrange = Color(0xFFFF9818)

@Composable
fun FoodRescueBottomNav(
    onHomeClick: () -> Unit,
    onReservationClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBlue)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(width = 70.dp, height = 52.dp)
                .clickable(onClick = onHomeClick),
            color = Color.White,
            shape = RoundedCornerShape(15.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.ic_home_figma),
                    contentDescription = "Home",
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        FoodRescueNavButton(
            icon = Icons.Default.ShoppingCart,
            description = "Reservations",
            onClick = onReservationClick
        )

        FoodRescueNavButton(
            icon = Icons.Default.Search,
            description = "Search",
            onClick = onSearchClick
        )
    }
}

@Composable
private fun FoodRescueNavButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 70.dp, height = 52.dp)
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(15.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = NavOrange,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

