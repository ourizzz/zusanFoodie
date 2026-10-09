package com.example.zusanfoodie.donor

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import coil.compose.AsyncImage

import com.example.zusanfoodie.AddFoodListingResponse
import com.example.zusanfoodie.RetrofitClient
import com.example.zusanfoodie.UploadPhotoResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File


@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AddListingScreen(

    vm: ListingViewModel,

    nav: NavController,

    donorId: Int
) {

    val ctx =
        LocalContext.current


    // ==========================================
    // FORM STATES
    // ==========================================

    var name by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf("Meals")
    }

    var qty by rememberSaveable {
        mutableStateOf("")
    }

    var unit by rememberSaveable {
        mutableStateOf(units[0])
    }

    var area by rememberSaveable {
        mutableStateOf("")
    }

    var date by rememberSaveable {
        mutableStateOf("")
    }

    var time by rememberSaveable {
        mutableStateOf("")
    }

    var expiry by rememberSaveable {
        mutableStateOf("")
    }

    var notes by rememberSaveable {
        mutableStateOf("")
    }

    var imagePath by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var isPublishing by remember {
        mutableStateOf(false)
    }


    // ==========================================
    // IMAGE - GALLERY
    // ==========================================

    val gallery =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->

            if (uri != null) {

                imagePath =
                    vm.saveImage(uri)
                        ?: run {

                            Toast.makeText(
                                ctx,
                                "Could not load that photo. Try another.",
                                Toast.LENGTH_SHORT
                            ).show()

                            null
                        }
            }
        }


    // ==========================================
    // IMAGE - CAMERA
    // ==========================================

    val camera =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bmp: Bitmap? ->

            if (bmp != null) {

                imagePath =
                    vm.saveBitmap(bmp)
            }
        }


    // ==========================================
    // VALIDATION
    // ==========================================

    val qtyInt =
        qty.toIntOrNull()


    val nameErr =
        submitted &&
                name.isBlank()


    val qtyErr =
        submitted &&
                (
                        qtyInt == null ||
                                qtyInt < 1
                        )


    val areaErr =
        submitted &&
                area.isBlank()


    val dateErr =
        submitted &&
                (
                        date.isBlank() ||
                                time.isBlank()
                        )


    val expErr =
        submitted &&
                expiry.isBlank()


    // ==========================================
    // SEND LISTING TO MYSQL
    // ==========================================

    fun saveListingToDatabase(serverPhotoPath: String) {

        val mysqlDate = convertDateForMysql(date)
        val mysqlTime = convertTimeForMysql(time)
        val mysqlSafeUntil = convertDateTimeForMysql(expiry)

        if (
            mysqlDate == null ||
            mysqlTime == null ||
            mysqlSafeUntil == null
        ) {
            isPublishing = false

            Toast.makeText(
                ctx,
                "Invalid date or time format.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        RetrofitClient.apiService
            .addFoodListing(
                donorId = donorId,
                foodName = name.trim(),
                category = category,
                quantity = qtyInt!!,
                unit = unit,
                pickupLocation = area,
                pickupDate = mysqlDate,
                pickupStartTime = mysqlTime,
                safeUntil = mysqlSafeUntil,
                notes = notes.trim(),
                photo = serverPhotoPath
            )
            .enqueue(
                object : Callback<AddFoodListingResponse> {

                    override fun onResponse(
                        call: Call<AddFoodListingResponse>,
                        response: Response<AddFoodListingResponse>
                    ) {
                        isPublishing = false

                        if (response.isSuccessful) {
                            val result = response.body()

                            if (result?.success == true) {
                                vm.loadDonorListings(donorId)

                                Toast.makeText(
                                    ctx,
                                    "Listing published successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                nav.popBackStack()
                            } else {
                                Toast.makeText(
                                    ctx,
                                    result?.message ?: "Unable to publish listing.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                ctx,
                                "Server error. Unable to publish listing.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<AddFoodListingResponse>,
                        t: Throwable
                    ) {
                        isPublishing = false

                        Toast.makeText(
                            ctx,
                            "Connection failed. Please check XAMPP.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    fun uploadPhotoThenSave(localImagePath: String) {

        val imageFile = File(localImagePath)

        if (!imageFile.exists()) {
            isPublishing = false

            Toast.makeText(
                ctx,
                "Selected photo could not be found.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val requestBody =
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())

        val photoPart =
            MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestBody
            )

        RetrofitClient.apiService
            .uploadFoodPhoto(photoPart)
            .enqueue(
                object : Callback<UploadPhotoResponse> {

                    override fun onResponse(
                        call: Call<UploadPhotoResponse>,
                        response: Response<UploadPhotoResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            val uploadedPhoto = result?.photo

                            if (
                                result?.success == true &&
                                !uploadedPhoto.isNullOrBlank()
                            ) {
                                saveListingToDatabase(uploadedPhoto)
                            } else {
                                isPublishing = false

                                Toast.makeText(
                                    ctx,
                                    result?.message ?: "Photo upload failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            isPublishing = false

                            Toast.makeText(
                                ctx,
                                "Server error while uploading photo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<UploadPhotoResponse>,
                        t: Throwable
                    ) {
                        isPublishing = false

                        Toast.makeText(
                            ctx,
                            "Photo upload failed. Please check XAMPP.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    fun publishListing() {

        submitted = true

        val ok =
            donorId > 0 &&
                    name.isNotBlank() &&
                    qtyInt != null &&
                    qtyInt >= 1 &&
                    area.isNotBlank() &&
                    date.isNotBlank() &&
                    time.isNotBlank() &&
                    expiry.isNotBlank()

        if (!ok) {
            if (donorId <= 0) {
                Toast.makeText(
                    ctx,
                    "Unable to identify donor account. Please log in again.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    ctx,
                    "Please fix the highlighted fields",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return
        }

        isPublishing = true

        val localPhoto = imagePath

        if (!localPhoto.isNullOrBlank()) {
            uploadPhotoThenSave(localPhoto)
        } else {
            saveListingToDatabase("")
        }
    }


    // ==========================================
    // SCREEN
    // ==========================================

    Column(

        Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {


        // ======================================
        // TOP BAR
        // ======================================

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

                    Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription =
                        "Back",

                    tint =
                        Color.White
                )
            }


            Text(

                text =
                    "New food listing",

                color =
                    Color.White,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.ExtraBold
            )
        }


        // ======================================
        // FORM
        // ======================================

        Column(

            Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {


            // ==================================
            // PHOTO
            // ==================================

            Box(

                Modifier
                    .fillMaxWidth()
                    .height(
                        150.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            14.dp
                        )
                    )
                    .background(
                        AzureLight
                    ),

                contentAlignment =
                    Alignment.Center
            ) {


                val p =
                    imagePath


                if (p != null) {

                    AsyncImage(

                        model =
                            File(p),

                        contentDescription =
                            "Food photo",

                        modifier =
                            Modifier.fillMaxSize(),

                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Text(

                        text =
                            "📷  No photo yet",

                        color =
                            AzureDark,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {


                OutlinedButton(

                    onClick = {

                        gallery.launch(

                            PickVisualMediaRequest(

                                ActivityResultContracts
                                    .PickVisualMedia
                                    .ImageOnly
                            )
                        )
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        "🖼 Gallery"
                    )
                }


                OutlinedButton(

                    onClick = {

                        camera.launch(
                            null
                        )
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        "📷 Camera"
                    )
                }
            }


            // ==================================
            // FOOD NAME
            // ==================================

            OutlinedTextField(

                value =
                    name,

                onValueChange = {
                    name = it
                },

                label = {
                    Text(
                        "Food name"
                    )
                },

                singleLine =
                    true,

                isError =
                    nameErr,

                supportingText = {

                    if (nameErr) {

                        Text(
                            "Enter the food name"
                        )
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()
            )


            // ==================================
            // CATEGORY
            // ==================================

            Text(

                text =
                    "Category",

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    13.sp
            )


            FlowRow(

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {


                categories.forEach {
                        c ->


                    FilterChip(

                        selected =
                            category == c,

                        onClick = {
                            category = c
                        },

                        label = {
                            Text(c)
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


            // ==================================
            // QUANTITY + UNIT
            // ==================================

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    ),

                verticalAlignment =
                    Alignment.Top
            ) {


                OutlinedTextField(

                    value =
                        qty,

                    onValueChange = {

                        qty =
                            it.filter(
                                Char::isDigit
                            )
                    },

                    label = {
                        Text(
                            "Quantity"
                        )
                    },

                    singleLine =
                        true,

                    isError =
                        qtyErr,

                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(

                            keyboardType =
                                KeyboardType.Number
                        ),

                    supportingText = {

                        if (qtyErr) {

                            Text(
                                "Enter 1 or more"
                            )
                        }
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                Dropdown(

                    label =
                        "Unit",

                    value =
                        unit,

                    options =
                        units,

                    isError =
                        false,

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    onSelect = {

                        unit =
                            it
                    }
                )
            }


            // ==================================
            // PICKUP LOCATION
            // ==================================

            Dropdown(

                label =
                    "Pickup Location",

                value =
                    area,

                options =
                    areas,

                isError =
                    areaErr,

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder =
                    "Choose a collection area",

                onSelect = {

                    area =
                        it
                }
            )


            // ==================================
            // PICKUP DATE + TIME
            // ==================================

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {


                OutlinedButton(

                    onClick = {

                        pickDate(ctx) {
                                selectedDate ->

                            date =
                                selectedDate
                        }
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(

                        date.ifBlank {
                            "📅 Pickup date"
                        }
                    )
                }


                OutlinedButton(

                    onClick = {

                        pickTime(ctx) {
                                selectedTime ->

                            time =
                                selectedTime
                        }
                    },

                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(

                        time.ifBlank {
                            "🕘 Pickup time"
                        }
                    )
                }
            }


            if (dateErr) {

                Text(

                    text =
                        "Choose a pickup date and time",

                    color =
                        Color(0xFFD63A3A),

                    fontSize =
                        12.5.sp
                )
            }


            // ==================================
            // SAFE UNTIL
            // ==================================

            OutlinedButton(

                onClick = {

                    pickDate(ctx) {
                            selectedDate ->

                        pickTime(ctx) {
                                selectedTime ->

                            expiry =
                                "$selectedDate $selectedTime"
                        }
                    }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                border =
                    BorderStroke(

                        1.dp,

                        if (expErr) {

                            Color(0xFFD63A3A)

                        } else {

                            Color.Gray
                        }
                    )
            ) {

                Text(

                    expiry.ifBlank {

                        "🕙 Safe to eat until (date & time)"
                    }
                )
            }


            if (expErr) {

                Text(

                    text =
                        "Choose when the food stops being safe to eat",

                    color =
                        Color(0xFFD63A3A),

                    fontSize =
                        12.5.sp
                )
            }


            // ==================================
            // NOTES
            // ==================================

            OutlinedTextField(

                value =
                    notes,

                onValueChange = {
                    notes = it
                },

                label = {

                    Text(
                        "Notes for collector"
                    )
                },

                modifier =
                    Modifier.fillMaxWidth()
            )
        }


        // ======================================
        // PUBLISH BUTTON
        // ======================================

        Surface(

            shadowElevation =
                8.dp,

            color =
                Color.White
        ) {


            if (isPublishing) {

                Button(

                    onClick = {},

                    enabled =
                        false,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                18.dp,
                                10.dp
                            )
                ) {


                    CircularProgressIndicator(

                        modifier =
                            Modifier.size(
                                20.dp
                            ),

                        strokeWidth =
                            2.dp
                    )


                    Spacer(
                        Modifier.width(
                            8.dp
                        )
                    )


                    Text(
                        "Publishing..."
                    )
                }


            } else {


                PrimaryButton(

                    text =
                        "Publish listing",

                    modifier =
                        Modifier.padding(
                            18.dp,
                            10.dp
                        )
                ) {

                    publishListing()
                }
            }
        }
    }
}


// ==========================================
// DROPDOWN
// ==========================================

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun Dropdown(

    label: String,

    value: String,

    options: List<String>,

    isError: Boolean,

    modifier: Modifier,

    placeholder: String = "",

    onSelect: (String) -> Unit
) {

    var open by remember {
        mutableStateOf(false)
    }


    ExposedDropdownMenuBox(

        expanded =
            open,

        onExpandedChange = {
            open = !open
        },

        modifier =
            modifier
    ) {


        OutlinedTextField(

            value =
                value,

            onValueChange = {},

            readOnly =
                true,

            label = {
                Text(label)
            },

            isError =
                isError,

            placeholder = {
                Text(
                    placeholder
                )
            },

            supportingText = {

                if (isError) {

                    Text(
                        "Please choose one"
                    )
                }
            },

            trailingIcon = {

                ExposedDropdownMenuDefaults
                    .TrailingIcon(
                        expanded = open
                    )
            },

            modifier =
                Modifier
                    .menuAnchor()
                    .fillMaxWidth()
        )


        ExposedDropdownMenu(

            expanded =
                open,

            onDismissRequest = {
                open = false
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

                        onSelect(
                            option
                        )

                        open =
                            false
                    }
                )
            }
        }
    }
}


// ==========================================
// DATE CONVERSION FOR MYSQL
// ==========================================

private fun convertDateForMysql(
    value: String
): String? {

    val trimmed =
        value.trim()


    // Already yyyy-MM-dd
    if (
        Regex(
            "^\\d{4}-\\d{2}-\\d{2}$"
        ).matches(trimmed)
    ) {

        return trimmed
    }


    // dd/MM/yyyy
    val slashParts =
        trimmed.split("/")


    if (
        slashParts.size == 3
    ) {

        val day =
            slashParts[0]
                .padStart(
                    2,
                    '0'
                )

        val month =
            slashParts[1]
                .padStart(
                    2,
                    '0'
                )

        val year =
            slashParts[2]


        if (
            year.length == 4
        ) {

            return "$year-$month-$day"
        }
    }


    return null
}


// ==========================================
// TIME CONVERSION FOR MYSQL
// ==========================================

private fun convertTimeForMysql(
    value: String
): String? {

    val trimmed =
        value.trim()


    // HH:mm:ss
    if (
        Regex(
            "^\\d{2}:\\d{2}:\\d{2}$"
        ).matches(trimmed)
    ) {

        return trimmed
    }


    // HH:mm
    if (
        Regex(
            "^\\d{1,2}:\\d{2}$"
        ).matches(trimmed)
    ) {

        val parts =
            trimmed.split(":")


        val hour =
            parts[0]
                .padStart(
                    2,
                    '0'
                )


        return "$hour:${parts[1]}:00"
    }


    return null
}


// ==========================================
// SAFE UNTIL CONVERSION FOR MYSQL
// ==========================================

private fun convertDateTimeForMysql(
    value: String
): String? {

    val lastSpace =
        value.lastIndexOf(" ")


    if (
        lastSpace <= 0 ||
        lastSpace >= value.length - 1
    ) {

        return null
    }


    val datePart =
        value
            .substring(
                0,
                lastSpace
            )
            .trim()


    val timePart =
        value
            .substring(
                lastSpace + 1
            )
            .trim()


    val mysqlDate =
        convertDateForMysql(
            datePart
        )
            ?: return null


    val mysqlTime =
        convertTimeForMysql(
            timePart
        )
            ?: return null


    return "$mysqlDate $mysqlTime"
}
