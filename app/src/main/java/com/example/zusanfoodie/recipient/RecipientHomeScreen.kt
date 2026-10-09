package com.example.zusanfoodie.recipient

import com.example.zusanfoodie.RecipientFoodListing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val RecipientBlue = Color(0xFF3036B3)
private val RecipientCoral = Color(0xFFD95F59)
private val RecipientWhite = Color(0xFFF9F9F9)


// ==========================================
// RECIPIENT FOOD MODEL
// ==========================================

data class RecipientFood(

    val listingId: Int = 0,

    val donorId: Int = 0,

    val name: String,

    val description: String,

    val rating: String = "",

    val category: String = "",

    val area: String = "",

    val isAvailable: Boolean = true,

    val foodName: String = "",

    val availablePacks: Int = 0,

    val unit: String = "",

    val pickupLocation: String = "",

    val pickupDate: String = "",

    val pickupStartTime: String = "",

    val pickupEndTime: String? = null,

    val donorPhone: String? = null,

    val notes: String? = null
)


// ==========================================
// RECIPIENT HOME
// ==========================================

@Composable
fun RecipientHomeScreen(
    databaseFoods: List<RecipientFoodListing>,
    isLoading: Boolean = false,
    onReserveClick: (RecipientFood) -> Unit,
    onSearchClick: () -> Unit = {},
    onFoodClick: (RecipientFood) -> Unit = {},
    onReservationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    activeFilter: FoodFilter = FoodFilter()
){


    // ======================================
    // DATABASE DATA -> SCREEN DATA
    // ======================================

    val foods = databaseFoods.map { item ->

        RecipientFood(

            listingId =
                item.listing_id,

            donorId =
                item.donor_id,

            name =
                item.donor_name,

            description =
                "${item.quantity} ${item.unit} available",

            category =
                item.category,

            area =
                item.pickup_location,

            isAvailable =
                item.status == "listed" &&
                        item.quantity > 0,

            foodName =
                item.food_name,

            availablePacks =
                item.quantity,

            unit =
                item.unit,

            pickupLocation =
                item.pickup_location,

            pickupDate =
                item.pickup_date,

            pickupStartTime =
                item.pickup_start_time,

            pickupEndTime =
                item.pickup_end_time,

            donorPhone =
                item.donor_phone,

            notes =
                item.notes
        )
    }


    val categories = listOf(

        "🍱" to "Meals",

        "🥐" to "Bakery",

        "🥬" to "Produce",

        "📦" to "Package"
    )


    // ======================================
    // FILTER
    // ======================================

    fun matchesFilter(
        food: RecipientFood
    ): Boolean {

        val query =
            activeFilter.searchText.trim()


        val matchesSearch =

            query.isBlank() ||

                    food.foodName.contains(
                        query,
                        ignoreCase = true
                    ) ||

                    food.category.contains(
                        query,
                        ignoreCase = true
                    ) ||

                    food.name.contains(
                        query,
                        ignoreCase = true
                    )


        val matchesCategory =

            activeFilter.category ==
                    "All categories" ||

                    food.category.equals(
                        activeFilter.category,
                        ignoreCase = true
                    )


        val matchesArea =

            activeFilter.area ==
                    "All collection areas" ||

                    food.area.equals(
                        activeFilter.area,
                        ignoreCase = true
                    )


        val matchesAvailability =

            !activeFilter.availableOnly ||
                    food.isAvailable


        return matchesSearch &&
                matchesCategory &&
                matchesArea &&
                matchesAvailability
    }


    val filteredFoods =
        foods.filter {
            matchesFilter(it)
        }


    // ======================================
    // SCREEN
    // ======================================

    Scaffold(

        containerColor =
            RecipientBlue,

        bottomBar = {

            FoodRescueBottomNav(

                onHomeClick = {},

                onReservationClick =
                    onReservationClick,

                onSearchClick =
                    onSearchClick
            )
        }

    ) { innerPadding ->


        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .background(
                    RecipientBlue
                )

                .padding(
                    innerPadding
                ),

            contentPadding =
                PaddingValues(

                    start = 20.dp,

                    end = 20.dp,

                    top = 8.dp,

                    bottom = 24.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {


            // ======================================
            // HEADER
            // ======================================

            item {

                Spacer(
                    Modifier.height(
                        12.dp
                    )
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello! 👋",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium
                    )

                    TextButton(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "LOGOUT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    Modifier.height(
                        4.dp
                    )
                )


                Text(

                    text =
                        "Find Available Food 🔔",

                    color =
                        Color.White,

                    fontSize =
                        17.sp
                )
            }


            // ======================================
            // SEARCH
            // ======================================

            item {

                Surface(

                    modifier = Modifier

                        .fillMaxWidth()

                        .clickable {

                            onSearchClick()
                        },

                    shape =
                        RoundedCornerShape(
                            8.dp
                        ),

                    color =
                        Color.White
                ) {


                    Row(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(
                                14.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {


                        Text(

                            text =

                                if (
                                    activeFilter
                                        .searchText
                                        .isBlank()
                                ) {

                                    "Search food..."

                                } else {

                                    activeFilter
                                        .searchText
                                },

                            color =
                                Color.Gray,

                            fontSize =
                                14.sp
                        )


                        Icon(

                            imageVector =
                                Icons.Default.Search,

                            contentDescription =
                                "Search",

                            tint =
                                Color.Black
                        )
                    }
                }
            }


            // ======================================
            // CATEGORIES
            // ======================================

            item {

                Text(

                    text =
                        "Categories",

                    color =
                        Color.White,

                    fontSize =
                        15.sp
                )


                Spacer(
                    Modifier.height(
                        10.dp
                    )
                )


                LazyRow(

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            18.dp
                        )
                ) {

                    items(
                        categories
                    ) { category ->

                        CategoryCard(

                            emoji =
                                category.first,

                            name =
                                category.second
                        )
                    }
                }
            }


            // ======================================
            // AVAILABLE FOOD
            // ======================================

            item {

                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )


                Text(

                    text =
                        "Available Food",

                    color =
                        Color.White,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Medium
                )


                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )
            }


            // ======================================
            // LOADING
            // ======================================

            if (isLoading) {

                item {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(
                                40.dp
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(

                            color =
                                Color.White
                        )
                    }
                }
            }


            // ======================================
            // NO FOOD
            // ======================================

            else if (foods.isEmpty()) {

                item {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults.cardColors(

                                containerColor =
                                    Color.White
                            ),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    ) {


                        Column(

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(
                                    30.dp
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {


                            Text(

                                text =
                                    "🍽️",

                                fontSize =
                                    40.sp
                            )


                            Spacer(
                                Modifier.height(
                                    10.dp
                                )
                            )


                            Text(

                                text =
                                    "No food available at the moment",

                                color =
                                    Color.DarkGray,

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )


                            Spacer(
                                Modifier.height(
                                    4.dp
                                )
                            )


                            Text(

                                text =
                                    "New food donated will appear here.",

                                color =
                                    Color.Gray,

                                fontSize =
                                    12.sp
                            )
                        }
                    }
                }
            }


            // ======================================
            // FILTER RETURNS NOTHING
            // ======================================

            else if (
                filteredFoods.isEmpty()
            ) {

                item {

                    Text(

                        text =
                            "No matching food found. Try another filter.",

                        color =
                            Color.White,

                        fontSize =
                            14.sp,

                        modifier =
                            Modifier.padding(
                                vertical = 20.dp
                            )
                    )
                }
            }


            // ======================================
            // REAL DATABASE FOOD
            // ======================================

            else {

                items(
                    filteredFoods
                ) { food ->


                    FoodCard(

                        food =
                            food,

                        modifier =
                            Modifier.fillMaxWidth(),

                        onReserveClick = {

                            onReserveClick(
                                food
                            )
                        }
                    )
                }
            }


            item {

                Spacer(
                    Modifier.height(
                        20.dp
                    )
                )
            }
        }
    }
}


// ==========================================
// CATEGORY CARD
// ==========================================

@Composable
private fun CategoryCard(

    emoji: String,

    name: String
) {

    Card(

        modifier = Modifier

            .width(
                68.dp
            )

            .height(
                74.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    RecipientWhite
            ),

        shape =
            RoundedCornerShape(
                9.dp
            )
    ) {


        Column(

            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {


            Text(

                text =
                    emoji,

                fontSize =
                    29.sp
            )


            Spacer(
                Modifier.height(
                    3.dp
                )
            )


            Text(

                text =
                    name,

                fontSize =
                    11.sp,

                color =
                    Color.DarkGray
            )
        }
    }
}


// ==========================================
// FOOD CARD
// ==========================================

@Composable
private fun FoodCard(

    food: RecipientFood,

    modifier: Modifier = Modifier,

    onReserveClick: () -> Unit
) {

    Card(

        modifier =
            modifier.clickable(

                onClick =
                    onReserveClick
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            ),

        shape =
            RoundedCornerShape(
                10.dp
            )
    ) {


        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(
                    10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Spacer(
                Modifier.width(
                    12.dp
                )
            )


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {


                Text(

                    text =
                        food.foodName,

                    fontSize =
                        16.sp,

                    color =
                        Color.Black,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    Modifier.height(
                        3.dp
                    )
                )


                Text(

                    text =
                        "Donor: ${food.name}",

                    fontSize =
                        12.sp,

                    color =
                        Color.Gray
                )


                Text(

                    text =
                        "${food.availablePacks} ${food.unit} available",

                    fontSize =
                        12.sp,

                    color =
                        Color.Gray
                )


                Text(

                    text =
                        food.pickupLocation,

                    fontSize =
                        12.sp,

                    color =
                        Color.Gray
                )


                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )


                Button(

                    onClick =
                        onReserveClick,

                    modifier =
                        Modifier.height(
                            34.dp
                        ),

                    contentPadding =
                        PaddingValues(

                            horizontal =
                                16.dp,

                            vertical =
                                0.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                RecipientCoral
                        )
                ) {


                    Text(

                        text =
                            "Reserve",

                        fontSize =
                            12.sp
                    )
                }
            }
        }
    }
}