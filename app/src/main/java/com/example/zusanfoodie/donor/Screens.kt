package com.example.zusanfoodie.donor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


// ==========================================
// DONOR HOME
// ==========================================

@Composable
fun HomeScreen(
    vm: ListingViewModel,
    nav: NavController,
    donorName: String,
    onLogoutClick: () -> Unit
) {

    val all by vm.listings.collectAsState()

    val active =
        all.filter {
            it.status != "Collected"
        }


    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {


        // ==========================================
        // HEADER
        // ==========================================

        Column(
            Modifier
                .fillMaxWidth()
                .background(Azure)
                .padding(20.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Good morning,",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text = donorName,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }


                TextButton(
                    onClick = onLogoutClick
                ) {

                    Text(
                        text = "LOGOUT",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        LazyColumn(

            contentPadding =
                PaddingValues(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // ==========================================
            // STATISTICS
            // ==========================================

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    StatBox(
                        "Listed",
                        all.count {
                            it.status == "Listed"
                        },
                        Modifier.weight(1f)
                    )

                    StatBox(
                        "Reserved",
                        all.count {
                            it.status == "Reserved"
                        },
                        Modifier.weight(1f)
                    )

                    StatBox(
                        "Collected",
                        all.count {
                            it.status == "Collected"
                        },
                        Modifier.weight(1f)
                    )
                }
            }


            // ==========================================
            // ADD FOOD
            // ==========================================

            item {

                PrimaryButton(
                    "＋  Share surplus food"
                ) {

                    nav.navigate("add")
                }
            }


            // ==========================================
            // ACTIVE LISTINGS HEADER
            // ==========================================

            item {

                Row(
                    Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        "Active listings",
                        fontWeight =
                            FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Ink
                    )

                    Text(
                        "See all",
                        color = AzureDark,
                        fontWeight =
                            FontWeight.SemiBold,

                        modifier =
                            Modifier.clickable {

                                nav.navigate(
                                    "listings"
                                )
                            }
                    )
                }
            }


            // ==========================================
            // EMPTY STATE
            // ==========================================

            if (active.isEmpty()) {

                item {

                    EmptyState(
                        "No active listings yet. Tap “Share surplus food” to add one."
                    )
                }
            }


            // ==========================================
            // ACTIVE LISTINGS
            // ==========================================

            items(
                active.take(4),
                key = {
                    it.id
                }
            ) { listing ->

                ListingCard(
                    listing
                ) {

                    nav.navigate(
                        "detail/${listing.id}"
                    )
                }
            }
        }
    }
}


// ==========================================
// STAT BOX
// ==========================================

@Composable
fun StatBox(
    label: String,
    value: Int,
    modifier: Modifier
) {

    Card(
        modifier,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        shape =
            RoundedCornerShape(14.dp)
    ) {

        Column(
            Modifier.padding(12.dp)
        ) {

            Text(
                "$value",
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = AzureDark
            )

            Text(
                label,
                fontSize = 12.sp,
                color = Muted
            )
        }
    }
}


// ==========================================
// LISTINGS SCREEN
// ==========================================

@Composable
fun ListingsScreen(
    vm: ListingViewModel,
    nav: NavController
) {

    val all by
    vm.listings.collectAsState()

    var query by remember {
        mutableStateOf("")
    }

    var cat by remember {
        mutableStateOf("All")
    }


    val shown =
        all.filter {

            (cat == "All" ||
                    it.category == cat) &&

                    (
                            query.isBlank() ||
                                    it.name.contains(
                                        query,
                                        true
                                    ) ||
                                    it.area.contains(
                                        query,
                                        true
                                    )
                            )
        }


    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        Text(
            "My listings",

            Modifier
                .background(Azure)
                .fillMaxWidth()
                .padding(20.dp),

            color =
                Color.White,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.ExtraBold
        )


        OutlinedTextField(

            value =
                query,

            onValueChange = {
                query = it
            },

            singleLine =
                true,

            placeholder = {

                Text(
                    "Search food or collection area"
                )
            },

            leadingIcon = {

                Icon(
                    Icons.Default.Search,
                    null
                )
            },

            shape =
                RoundedCornerShape(12.dp),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp,
                        14.dp,
                        18.dp,
                        0.dp
                    )
        )


        LazyRow(

            contentPadding =
                PaddingValues(
                    18.dp,
                    8.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            items(
                listOf("All") +
                        categories
            ) { category ->

                FilterChip(

                    selected =
                        cat == category,

                    onClick = {

                        cat =
                            category
                    },

                    label = {

                        Text(
                            category
                        )
                    },

                    colors =
                        FilterChipDefaults
                            .filterChipColors(

                                selectedContainerColor =
                                    Azure,

                                selectedLabelColor =
                                    Color.White
                            )
                )
            }
        }


        LazyColumn(

            contentPadding =
                PaddingValues(
                    18.dp,
                    0.dp,
                    18.dp,
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            if (
                shown.isEmpty()
            ) {

                item {

                    EmptyState(
                        "No listings match your search."
                    )
                }
            }


            items(
                shown,
                key = {
                    it.id
                }
            ) { listing ->

                ListingCard(
                    listing
                ) {

                    nav.navigate(
                        "detail/${listing.id}"
                    )
                }
            }
        }
    }
}


// ==========================================
// STATUS LIST SCREEN
// ==========================================

@Composable
fun StatusListScreen(
    vm: ListingViewModel,
    nav: NavController,
    title: String,
    status: String,
    empty: String
) {

    val allListings by
    vm.listings.collectAsState()

    val shown =
        allListings.filter {
            it.status == status
        }


    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        Text(
            title,

            Modifier
                .background(Azure)
                .fillMaxWidth()
                .padding(20.dp),

            color =
                Color.White,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.ExtraBold
        )


        LazyColumn(

            contentPadding =
                PaddingValues(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            if (
                shown.isEmpty()
            ) {

                item {

                    EmptyState(
                        empty
                    )
                }
            }


            items(
                shown,
                key = {
                    it.id
                }
            ) { listing ->

                ListingCard(
                    listing
                ) {

                    nav.navigate(
                        "detail/${listing.id}"
                    )
                }
            }
        }
    }
}


// ==========================================
// DETAIL SCREEN
// ==========================================

@Composable
fun DetailScreen(
    id: Int,
    vm: ListingViewModel,
    nav: NavController
) {

    val ctx =
        LocalContext.current

    val listing =
        vm.listings
            .collectAsState()
            .value
            .find {
                it.id == id
            }
            ?: return


    var showReserve by remember {
        mutableStateOf(false)
    }

    var showDelete by remember {
        mutableStateOf(false)
    }

    var recipient by remember {
        mutableStateOf("")
    }

    var volunteer by remember {
        mutableStateOf("")
    }

    var recErr by remember {
        mutableStateOf(false)
    }


    val toast = {
            message: String ->

        Toast
            .makeText(
                ctx,
                message,
                Toast.LENGTH_SHORT
            )
            .show()
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(
                rememberScrollState()
            )
    ) {


        // ==========================================
        // HEADER
        // ==========================================

        Row(

            Modifier
                .fillMaxWidth()
                .background(Azure)
                .padding(
                    8.dp,
                    14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    nav.popBackStack()
                }
            ) {

                Icon(
                    Icons.AutoMirrored
                        .Filled
                        .ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        Color.White
                )
            }


            Text(
                "Listing details",
                color =
                    Color.White,
                fontSize =
                    20.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                modifier =
                    Modifier.weight(1f)
            )


            TextButton(
                onClick = {
                    showDelete = true
                }
            ) {

                Text(
                    "Delete",
                    color =
                        Color.White
                )
            }
        }


        FoodThumb(
            listing,
            Modifier
                .fillMaxWidth()
                .height(180.dp)
        )


        Column(

            Modifier.padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            Text(
                "${listing.name} (${listing.quantity} ${listing.unit})",
                fontSize =
                    19.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    Ink
            )


            StatusChip(
                listing.status
            )


            Stepper(
                listing.status
            )


            Card(

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(
                        16.dp
                    )
            ) {

                Column(

                    Modifier.padding(
                        14.dp
                    ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    Info(
                        "Category",
                        listing.category
                    )

                    Info(
                        "Recipient",
                        listing.recipient
                            .ifBlank {
                                "Not reserved yet"
                            }
                    )

                    Info(
                        "Volunteer",
                        listing.volunteer
                            .ifBlank {
                                "Not assigned"
                            }
                    )

                    Info(
                        "Pickup slot",
                        "${listing.pickupDate}, ${listing.pickupTime}"
                    )

                    Info(
                        "Location",
                        "${listing.area}, Kuching"
                    )

                    Info(
                        "Safe until",
                        listing.expiry
                    )

                    Info(
                        "Notes",
                        listing.notes
                            .ifBlank {
                                "-"
                            }
                    )
                }
            }


            when (
                listing.status
            ) {

                "Listed" -> {

                    PrimaryButton(
                        "Mark as reserved"
                    ) {

                        showReserve =
                            true
                    }

                    SecondaryButton(
                        "Reschedule pickup"
                    ) {

                        reschedule(
                            ctx,
                            vm,
                            listing,
                            toast
                        )
                    }
                }


                "Reserved" -> {

                    PrimaryButton(
                        "✔ Confirm handover"
                    ) {

                        vm.update(

                            listing.copy(
                                status =
                                    "Collected"
                            )
                        )

                        toast(
                            "Marked as collected"
                        )

                        nav.popBackStack()
                    }


                    SecondaryButton(
                        "Reschedule pickup"
                    ) {

                        reschedule(
                            ctx,
                            vm,
                            listing,
                            toast
                        )
                    }
                }


                else -> {

                    Text(
                        "This food has been collected. Thank you for sharing!",
                        color =
                            Muted
                    )
                }
            }
        }
    }


    // ==========================================
    // RESERVE DIALOG
    // ==========================================

    if (
        showReserve
    ) {

        AlertDialog(

            onDismissRequest = {

                showReserve =
                    false
            },

            title = {

                Text(
                    "Reserve for recipient"
                )
            },

            text = {

                Column(

                    verticalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    OutlinedTextField(

                        value =
                            recipient,

                        onValueChange = {

                            recipient =
                                it

                            recErr =
                                false
                        },

                        label = {

                            Text(
                                "Recipient name"
                            )
                        },

                        isError =
                            recErr,

                        supportingText = {

                            if (
                                recErr
                            ) {

                                Text(
                                    "Enter the recipient's name"
                                )
                            }
                        },

                        singleLine =
                            true
                    )


                    OutlinedTextField(

                        value =
                            volunteer,

                        onValueChange = {

                            volunteer =
                                it
                        },

                        label = {

                            Text(
                                "Volunteer (optional)"
                            )
                        },

                        singleLine =
                            true
                    )
                }
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        if (
                            recipient.isBlank()
                        ) {

                            recErr =
                                true

                        } else {

                            vm.update(

                                listing.copy(

                                    status =
                                        "Reserved",

                                    recipient =
                                        recipient.trim(),

                                    volunteer =
                                        volunteer.trim()
                                )
                            )

                            showReserve =
                                false

                            toast(
                                "Reserved for ${recipient.trim()}"
                            )
                        }
                    }
                ) {

                    Text(
                        "Save"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showReserve =
                            false
                    }
                ) {

                    Text(
                        "Cancel"
                    )
                }
            }
        )
    }


    // ==========================================
    // DELETE DIALOG
    // ==========================================

    if (
        showDelete
    ) {

        AlertDialog(

            onDismissRequest = {

                showDelete =
                    false
            },

            title = {

                Text(
                    "Delete this listing?"
                )
            },

            text = {

                Text(
                    "This cannot be undone."
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        vm.delete(
                            listing
                        )

                        toast(
                            "Listing deleted"
                        )

                        nav.popBackStack()
                    }
                ) {

                    Text(
                        "Delete",
                        color =
                            Color(
                                0xFFD63A3A
                            )
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDelete =
                            false
                    }
                ) {

                    Text(
                        "Cancel"
                    )
                }
            }
        )
    }
}


// ==========================================
// RESCHEDULE
// ==========================================

private fun reschedule(
    ctx: android.content.Context,
    vm: ListingViewModel,
    listing: FoodListing,
    toast: (String) -> Unit
) {

    pickDate(
        ctx
    ) { date ->

        pickTime(
            ctx
        ) { time ->

            vm.update(

                listing.copy(

                    pickupDate =
                        date,

                    pickupTime =
                        time
                )
            )

            toast(
                "Pickup moved to $date, $time"
            )
        }
    }
}


// ==========================================
// INFO
// ==========================================

@Composable
fun Info(
    key: String,
    value: String
) {

    Row(

        Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            key,
            color =
                Muted,
            fontSize =
                14.sp
        )

        Text(
            value,
            fontWeight =
                FontWeight.Bold,
            fontSize =
                14.sp,
            color =
                Ink,
            modifier =
                Modifier.padding(
                    start = 16.dp
                )
        )
    }
}


// ==========================================
// STEPPER
// ==========================================

@Composable
fun Stepper(
    status: String
) {

    val steps =
        listOf(
            "Listed",
            "Reserved",
            "Collected"
        )

    val index =
        steps.indexOf(
            status
        )


    Card(

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Row(

            Modifier
                .fillMaxWidth()
                .padding(14.dp),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            steps.forEachIndexed {
                    i,
                    step ->

                val done =
                    i < index ||
                            index == 2


                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Box(

                        Modifier
                            .size(30.dp)
                            .clip(
                                CircleShape
                            )
                            .background(

                                if (
                                    done
                                ) {

                                    Color(
                                        0xFF1E9E63
                                    )

                                } else if (
                                    i == index
                                ) {

                                    Azure

                                } else {

                                    Color(
                                        0xFFDCE5F0
                                    )
                                }
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            if (
                                done
                            ) {
                                "✓"
                            } else {
                                "${i + 1}"
                            },

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    Text(
                        step,
                        fontSize =
                            12.5.sp,
                        color =
                            Ink,
                        modifier =
                            Modifier.padding(
                                top = 6.dp
                            )
                    )
                }
            }
        }
    }
}