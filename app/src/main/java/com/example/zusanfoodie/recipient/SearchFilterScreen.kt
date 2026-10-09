package com.example.zusanfoodie.recipient
import com.example.zusanfoodie.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SearchBlue = Color(0xFF3035A4)
private val SearchCoral = Color(0xFFD45F59)

// Stores the user's selected search filters
data class FoodFilter(
    val searchText: String = "",
    val category: String = "All categories",
    val area: String = "All collection areas",
    val availableOnly: Boolean = false
)

@Composable
fun SearchFilterScreen(
    onHomeClick: () -> Unit = {},
    onReservationsClick: () -> Unit = {},
    onApplyFilter: (FoodFilter) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All categories") }
    var area by remember { mutableStateOf("All collection areas") }
    var availableOnly by remember { mutableStateOf(false) }
    var applied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SearchBlue)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Search & Filter",
                fontSize = 28.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find Available Food 🔔",
                fontSize = 17.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SEARCH FOOD
            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    applied = false
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(14.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search food...",
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CATEGORY
            Text(
                text = "Category",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterDropdown(
                selected = category,
                options = listOf(
                    "All categories",
                    "Pizza",
                    "Sushi",
                    "Noodles",
                    "Rice"
                ),
                onSelected = {
                    category = it
                    applied = false
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // COLLECTION AREA
            Text(
                text = "Collection area",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterDropdown(
                selected = area,
                options = listOf(
                    "All collection areas",
                    "City centre",
                    "Taman Jaya",
                    "Taman Indah",
                    "Rawang"
                ),
                onSelected = {
                    area = it
                    applied = false
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // AVAILABLE ONLY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Available only",
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Show food ready for collection",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = availableOnly,
                    onCheckedChange = {
                        availableOnly = it
                        applied = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // APPLY FILTER BUTTON
            Button(
                onClick = {
                    applied = true

                    val selectedFilter = FoodFilter(
                        searchText = searchText.trim(),
                        category = category,
                        area = area,
                        availableOnly = availableOnly
                    )

                    onApplyFilter(selectedFilter)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SearchCoral
                )
            ) {
                Text(
                    text = "Apply Filter",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (applied) {
                    "Filters applied successfully"
                } else {
                    "Choose filters to find available food"
                },
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // EXPLORE CATEGORIES
            Text(
                text = "Explore categories",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                listOf(
                    "🍕" to "Pizza",
                    "🍔" to "Burger",
                    "🍣" to "Sushi",
                    "🍜" to "Noodles"
                ).forEach { (emoji, name) ->

                    Column(
                        modifier = Modifier
                            .width(68.dp)
                            .background(
                                Color.White,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                category = name
                                applied = false
                            }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = emoji,
                            fontSize = 25.sp
                        )

                        Text(
                            text = name,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOTTOM NAVIGATION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            FoodRescueBottomNav(
                onHomeClick = onHomeClick,
                onReservationClick = onReservationsClick,
                onSearchClick = {}
            )
        }
    }
}

// DROPDOWN COMPONENT
@Composable
private fun FilterDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(10.dp)
                )
                .clickable {
                    expanded = true
                }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = selected,
                fontSize = 14.sp
            )

            Text(
                text = "⌄",
                fontSize = 16.sp
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// BOTTOM NAVIGATION BUTTON
@Composable
private fun SearchNavigationTile(
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(
                width = 70.dp,
                height = 52.dp
            )
            .background(
                Color.White,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = emoji,
            fontSize = 25.sp
        )
    }
}
