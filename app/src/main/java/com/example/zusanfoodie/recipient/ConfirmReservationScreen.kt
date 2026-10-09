package com.example.zusanfoodie.recipient

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.zusanfoodie.CreateReservationResponse
import com.example.zusanfoodie.R
import com.example.zusanfoodie.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val ReservationBlue = Color(0xFF3035A4)
private val ReservationCoral = Color(0xFFD45F59)


@Composable
fun ConfirmReservationScreen(

    food: RecipientFood,

    recipientId: Int,

    recipientAccountName: String = "",

    onBackClick: () -> Unit,

    onHomeClick: () -> Unit,

    onSearchClick: () -> Unit,

    onReservationsClick: () -> Unit = {},

    onReservationSuccess: () -> Unit = {}
) {

    // ==========================================
    // FORM STATES
    // ==========================================

    var quantity by remember(food.listingId) {
        mutableIntStateOf(1)
    }

    var recipientName by remember {
        mutableStateOf(recipientAccountName)
    }

    var phoneNumber by remember {
        mutableStateOf("")
    }

    var deliveryLocation by remember {
        mutableStateOf("Rawang")
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var confirmed by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }


    // ==========================================
    // ACTUAL AVAILABLE QUANTITY
    // ==========================================

    val maximumQuantity =
        if (food.availablePacks > 0) {
            food.availablePacks
        } else {
            1
        }


    // ==========================================
    // VALIDATE FORM
    // ==========================================

    fun validateReservation(): Boolean {

        errorMessage = ""

        when {

            recipientId <= 0 -> {

                errorMessage =
                    "Unable to identify your account. Please log in again."
            }


            food.listingId <= 0 -> {

                errorMessage =
                    "Unable to identify this food listing."
            }


            recipientName.isBlank() -> {

                errorMessage =
                    "Please enter your name."
            }


            phoneNumber.isBlank() -> {

                errorMessage =
                    "Please enter your phone number."
            }


            !phoneNumber.matches(
                Regex("^\\+?[0-9]{9,15}$")
            ) -> {

                errorMessage =
                    "Please enter a valid phone number."
            }


            quantity < 1 -> {

                errorMessage =
                    "Please select at least 1 item."
            }


            quantity > maximumQuantity -> {

                errorMessage =
                    "Only $maximumQuantity ${food.unit} available."
            }


            deliveryLocation.isBlank() -> {

                errorMessage =
                    "Please select a delivery location."
            }


            else -> {

                return true
            }
        }

        return false
    }


    // ==========================================
    // CREATE RESERVATION IN MYSQL
    // ==========================================

    fun createReservation() {

        if (!validateReservation()) {
            return
        }


        isSaving = true
        confirmed = false
        errorMessage = ""


        RetrofitClient.apiService
            .createReservation(

                listingId =
                    food.listingId,

                recipientId =
                    recipientId,

                quantity =
                    quantity,

                deliveryLocation =
                    deliveryLocation
            )
            .enqueue(

                object :
                    Callback<CreateReservationResponse> {


                    override fun onResponse(

                        call:
                        Call<CreateReservationResponse>,

                        response:
                        Response<CreateReservationResponse>
                    ) {

                        isSaving = false


                        if (response.isSuccessful) {

                            val result =
                                response.body()


                            if (result?.success == true) {

                                confirmed = true

                                errorMessage = ""

                                onReservationSuccess()

                            } else {

                                errorMessage =
                                    result?.message
                                        ?: "Unable to create reservation."
                            }

                        } else {

                            errorMessage =
                                "Unable to create reservation. Please try again."
                        }
                    }


                    override fun onFailure(

                        call:
                        Call<CreateReservationResponse>,

                        t:
                        Throwable
                    ) {

                        isSaving = false

                        errorMessage =
                            "Connection failed. Please check your server."
                    }
                }
            )
    }


    // ==========================================
    // TEMPORARY FOOD IMAGE
    // ==========================================

    val imageRes =
        R.drawable.pizza_spice


    // ==========================================
    // SCREEN
    // ==========================================

    Scaffold(

        containerColor =
            ReservationBlue,

        bottomBar = {

            Row(

                modifier = Modifier

                    .fillMaxWidth()

                    .background(
                        ReservationBlue
                    )

                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {


                ReservationNavButton(

                    icon =
                        Icons.Default.Home,

                    description =
                        "Home",

                    onClick =
                        onHomeClick
                )


                ReservationNavButton(

                    icon =
                        Icons.Default.ShoppingCart,

                    description =
                        "Reservations",

                    onClick =
                        onReservationsClick
                )


                ReservationNavButton(

                    icon =
                        Icons.Default.Search,

                    description =
                        "Search",

                    onClick =
                        onSearchClick
                )
            }
        }

    ) { innerPadding ->


        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .padding(
                    innerPadding
                ),

            contentPadding =
                PaddingValues(

                    start = 18.dp,

                    end = 18.dp,

                    top = 16.dp,

                    bottom = 50.dp
                )
        ) {


            // ==================================
            // TITLE
            // ==================================

            item {

                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Surface(

                        modifier = Modifier

                            .size(
                                34.dp
                            )

                            .clickable {

                                onBackClick()
                            },

                        shape =
                            RoundedCornerShape(
                                8.dp
                            ),

                        color =
                            Color.White
                    ) {


                        Box(

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(

                                text =
                                    "←",

                                color =
                                    ReservationCoral,

                                fontSize =
                                    22.sp
                            )
                        }
                    }


                    Spacer(
                        Modifier.width(
                            12.dp
                        )
                    )


                    Text(

                        text =
                            "Confirm Reservation",

                        fontSize =
                            23.sp,

                        color =
                            Color.White,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }


                Spacer(
                    Modifier.height(
                        18.dp
                    )
                )
            }


            // ==================================
            // SELECTED FOOD
            // ==================================

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
                            10.dp
                        )
                ) {


                    Row(

                        modifier =
                            Modifier.padding(
                                10.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Image(

                            painter =
                                painterResource(
                                    id = imageRes
                                ),

                            contentDescription =
                                food.foodName,

                            contentScale =
                                ContentScale.Crop,

                            modifier = Modifier

                                .size(
                                    76.dp
                                )

                                .clip(
                                    RoundedCornerShape(
                                        8.dp
                                    )
                                )
                        )


                        Spacer(
                            Modifier.width(
                                12.dp
                            )
                        )


                        Column {


                            Text(

                                text =
                                    "Selected food",

                                color =
                                    Color.Gray,

                                fontSize =
                                    11.sp
                            )


                            Text(

                                text =
                                    food.foodName,

                                fontSize =
                                    16.sp,

                                fontWeight =
                                    FontWeight.Medium,

                                color =
                                    Color.Black
                            )


                            Text(

                                text =
                                    "Donor: ${food.name}",

                                fontSize =
                                    11.sp,

                                color =
                                    Color.Gray
                            )


                            Spacer(
                                Modifier.height(
                                    5.dp
                                )
                            )


                            Surface(

                                color =
                                    Color(0xFFFFF0DC),

                                shape =
                                    RoundedCornerShape(
                                        20.dp
                                    )
                            ) {


                                Text(

                                    text =
                                        "Available: ${food.availablePacks} ${food.unit}",

                                    modifier =
                                        Modifier.padding(

                                            horizontal =
                                                9.dp,

                                            vertical =
                                                4.dp
                                        ),

                                    fontSize =
                                        11.sp,

                                    color =
                                        Color.DarkGray
                                )
                            }
                        }
                    }
                }


                Spacer(
                    Modifier.height(
                        12.dp
                    )
                )
            }


            // ==================================
            // QUANTITY
            // ==================================

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
                            10.dp
                        )
                ) {


                    Column(

                        modifier =
                            Modifier.padding(
                                12.dp
                            )
                    ) {


                        Text(

                            text =
                                "Quantity",

                            fontSize =
                                13.sp,

                            color =
                                Color.Black
                        )


                        Spacer(
                            Modifier.height(
                                8.dp
                            )
                        )


                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {


                            Button(

                                onClick = {

                                    if (
                                        quantity > 1
                                    ) {

                                        quantity--

                                        confirmed =
                                            false
                                    }
                                },

                                enabled =
                                    quantity > 1 &&
                                            !isSaving,

                                modifier =
                                    Modifier.size(
                                        40.dp
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        8.dp
                                    ),

                                contentPadding =
                                    PaddingValues(
                                        0.dp
                                    ),

                                colors =
                                    ButtonDefaults.buttonColors(

                                        containerColor =
                                            Color(0xFFF4F4F8),

                                        contentColor =
                                            ReservationCoral
                                    )
                            ) {


                                Text(

                                    text =
                                        "−",

                                    fontSize =
                                        22.sp
                                )
                            }


                            Text(

                                text =
                                    quantity.toString(),

                                fontSize =
                                    23.sp,

                                color =
                                    Color.Black
                            )


                            Button(

                                onClick = {

                                    if (
                                        quantity <
                                        maximumQuantity
                                    ) {

                                        quantity++

                                        confirmed =
                                            false
                                    }
                                },

                                enabled =
                                    quantity <
                                            maximumQuantity &&
                                            !isSaving,

                                modifier =
                                    Modifier.size(
                                        40.dp
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        8.dp
                                    ),

                                contentPadding =
                                    PaddingValues(
                                        0.dp
                                    ),

                                colors =
                                    ButtonDefaults.buttonColors(

                                        containerColor =
                                            ReservationCoral
                                    )
                            ) {


                                Text(

                                    text =
                                        "+",

                                    fontSize =
                                        22.sp
                                )
                            }
                        }
                    }
                }


                Spacer(
                    Modifier.height(
                        16.dp
                    )
                )
            }


            // ==================================
            // RECIPIENT NAME
            // ==================================

            item {

                ReservationLabel(
                    "Recipient Name"
                )


                Spacer(
                    Modifier.height(
                        5.dp
                    )
                )


                OutlinedTextField(

                    value =
                        recipientName,

                    onValueChange = {

                        recipientName =
                            it

                        confirmed =
                            false

                        errorMessage =
                            ""
                    },

                    placeholder = {

                        Text(

                            text =
                                "Recipient name:",

                            fontSize =
                                13.sp
                        )
                    },

                    singleLine =
                        true,

                    modifier = Modifier

                        .fillMaxWidth()

                        .height(
                            54.dp
                        ),

                    shape =
                        RoundedCornerShape(
                            9.dp
                        ),

                    colors =
                        reservationTextFieldColors()
                )


                Spacer(
                    Modifier.height(
                        12.dp
                    )
                )
            }


            // ==================================
            // PHONE
            // ==================================

            item {

                ReservationLabel(
                    "Phone Number"
                )


                Spacer(
                    Modifier.height(
                        5.dp
                    )
                )


                OutlinedTextField(

                    value =
                        phoneNumber,

                    onValueChange = {

                        phoneNumber =
                            it

                        confirmed =
                            false

                        errorMessage =
                            ""
                    },

                    placeholder = {

                        Text(

                            text =
                                "Phone number:",

                            fontSize =
                                13.sp
                        )
                    },

                    singleLine =
                        true,

                    keyboardOptions =
                        KeyboardOptions(

                            keyboardType =
                                KeyboardType.Phone
                        ),

                    modifier = Modifier

                        .fillMaxWidth()

                        .height(
                            54.dp
                        ),

                    shape =
                        RoundedCornerShape(
                            9.dp
                        ),

                    colors =
                        reservationTextFieldColors()
                )


                Spacer(
                    Modifier.height(
                        12.dp
                    )
                )
            }


            // ==================================
            // DELIVERY LOCATION
            // ==================================

            item {

                ReservationLabel(
                    "Delivery Location"
                )


                Spacer(
                    Modifier.height(
                        5.dp
                    )
                )


                ReservationDropdown(

                    selected =
                        deliveryLocation,

                    options =
                        listOf(

                            "Rawang",

                            "Taman Jaya",

                            "Taman Indah",

                            "City centre"
                        ),

                    leadingIcon =
                        "📍",

                    onSelected = {

                        deliveryLocation =
                            it

                        confirmed =
                            false

                        errorMessage =
                            ""
                    }
                )


                Spacer(
                    Modifier.height(
                        18.dp
                    )
                )
            }


            // ==================================
            // CONFIRM RESERVATION
            // ==================================

            item {

                Button(

                    onClick = {

                        createReservation()
                    },

                    enabled =
                        !confirmed &&
                                !isSaving,

                    modifier = Modifier

                        .fillMaxWidth()

                        .height(
                            48.dp
                        ),

                    shape =
                        RoundedCornerShape(
                            30.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                ReservationCoral
                        )
                ) {


                    if (isSaving) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.size(
                                    20.dp
                                ),

                            strokeWidth =
                                2.dp,

                            color =
                                Color.White
                        )


                        Spacer(
                            Modifier.width(
                                8.dp
                            )
                        )
                    }


                    Text(

                        text = when {

                            isSaving ->
                                "Saving..."

                            confirmed ->
                                "Reservation Saved ✓"

                            else ->
                                "Confirm Reservation ✓"
                        },

                        fontSize =
                            14.sp,

                        color =
                            Color.White
                    )
                }


                Spacer(
                    Modifier.height(
                        10.dp
                    )
                )


                Text(

                    text = when {

                        errorMessage.isNotEmpty() ->
                            errorMessage

                        confirmed ->
                            "Reservation confirmed successfully!"

                        else ->
                            "Select your delivery location to complete the reservation."
                    },

                    color =

                        if (
                            errorMessage.isNotEmpty()
                        ) {

                            Color(0xFFFFD6D6)

                        } else {

                            Color.White
                        },

                    fontSize =
                        11.sp,

                    modifier =
                        Modifier.fillMaxWidth()
                )


                Spacer(
                    Modifier.height(
                        30.dp
                    )
                )
            }
        }
    }
}


// ==========================================
// LABEL
// ==========================================

@Composable
private fun ReservationLabel(
    label: String
) {

    Text(

        text =
            label,

        fontSize =
            13.sp,

        color =
            Color.White
    )
}


// ==========================================
// TEXT FIELD COLORS
// ==========================================

@Composable
private fun reservationTextFieldColors() =
    OutlinedTextFieldDefaults.colors(

        focusedContainerColor =
            Color.White,

        unfocusedContainerColor =
            Color.White,

        focusedBorderColor =
            Color.White,

        unfocusedBorderColor =
            Color.White,

        focusedTextColor =
            Color.Black,

        unfocusedTextColor =
            Color.Black,

        cursorColor =
            ReservationCoral
    )


// ==========================================
// DELIVERY LOCATION DROPDOWN
// ==========================================

@Composable
private fun ReservationDropdown(

    selected: String,

    options: List<String>,

    leadingIcon: String,

    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }


    Box {


        Row(

            modifier = Modifier

                .fillMaxWidth()

                .height(
                    44.dp
                )

                .background(

                    Color.White,

                    RoundedCornerShape(
                        9.dp
                    )
                )

                .clickable {

                    expanded =
                        true
                }

                .padding(
                    horizontal =
                        12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Text(

                text =
                    leadingIcon,

                fontSize =
                    17.sp,

                color =
                    ReservationCoral
            )


            Spacer(
                Modifier.width(
                    10.dp
                )
            )


            Text(

                text =
                    selected,

                modifier =
                    Modifier.weight(
                        1f
                    ),

                fontSize =
                    13.sp,

                color =
                    Color.DarkGray
            )


            Text(

                text =
                    "⌄",

                fontSize =
                    16.sp,

                color =
                    Color.Gray
            )
        }


        DropdownMenu(

            expanded =
                expanded,

            onDismissRequest = {

                expanded =
                    false
            }
        ) {


            options.forEach {
                    option ->


                DropdownMenuItem(

                    text = {

                        Text(
                            option
                        )
                    },

                    onClick = {

                        onSelected(
                            option
                        )

                        expanded =
                            false
                    }
                )
            }
        }
    }
}


// ==========================================
// BOTTOM NAVIGATION BUTTON
// ==========================================

@Composable
private fun ReservationNavButton(

    icon: ImageVector,

    description: String,

    onClick: () -> Unit
) {

    Box(

        modifier = Modifier

            .size(

                width =
                    70.dp,

                height =
                    52.dp
            )

            .background(

                Color.White,

                RoundedCornerShape(
                    15.dp
                )
            )

            .clickable(
                onClick =
                    onClick
            ),

        contentAlignment =
            Alignment.Center
    ) {


        Icon(

            imageVector =
                icon,

            contentDescription =
                description,

            tint =
                Color(0xFFFF9818),

            modifier =
                Modifier.size(
                    30.dp
                )
        )
    }
}