package com.example.zusanfoodie.donor

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri

import androidx.lifecycle.AndroidViewModel

import com.example.zusanfoodie.DonorListingsResponse
import com.example.zusanfoodie.RetrofitClient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File


class ListingViewModel(
    app: Application
) : AndroidViewModel(app) {


    // ==========================================
    // DONOR LISTINGS
    // ==========================================

    private val _listings =
        MutableStateFlow<List<FoodListing>>(
            emptyList()
        )

    val listings:
            StateFlow<List<FoodListing>> =
        _listings.asStateFlow()


    // ==========================================
    // LOADING STATE
    // ==========================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading:
            StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ==========================================
    // ERROR MESSAGE
    // ==========================================

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage:
            StateFlow<String?> =
        _errorMessage.asStateFlow()


    // ==========================================
    // LOAD DONOR LISTINGS FROM MYSQL
    // ==========================================

    fun loadDonorListings(
        donorId: Int
    ) {

        if (donorId <= 0) {

            _errorMessage.value =
                "Invalid donor ID"

            return
        }


        _isLoading.value =
            true

        _errorMessage.value =
            null


        RetrofitClient.apiService
            .getDonorListings(
                donorId
            )
            .enqueue(

                object :
                    Callback<DonorListingsResponse> {


                    override fun onResponse(

                        call:
                        Call<DonorListingsResponse>,

                        response:
                        Response<DonorListingsResponse>
                    ) {

                        _isLoading.value =
                            false


                        if (!response.isSuccessful) {

                            _errorMessage.value =
                                "Unable to load listings"

                            return
                        }


                        val body =
                            response.body()


                        if (
                            body == null ||
                            !body.success
                        ) {

                            _errorMessage.value =
                                body?.message
                                    ?: "Unable to load listings"

                            return
                        }


                        // ==================================
                        // DATABASE DATA -> DONOR SCREEN DATA
                        // ==================================

                        _listings.value =
                            body.listings.map { item ->


                                val pickupTime =

                                    if (
                                        item.pickup_end_time
                                            .isNullOrBlank()
                                    ) {

                                        item.pickup_start_time

                                    } else {

                                        "${item.pickup_start_time} - ${item.pickup_end_time}"
                                    }


                                FoodListing(

                                    id =
                                        item.listing_id,

                                    name =
                                        item.food_name,

                                    category =
                                        item.category,

                                    quantity =
                                        item.quantity,

                                    unit =
                                        item.unit,

                                    area =
                                        item.pickup_location,

                                    pickupDate =
                                        item.pickup_date,

                                    pickupTime =
                                        pickupTime,

                                    expiry =
                                        item.safe_until ?: "",

                                    notes =
                                        item.notes ?: "",

                                    imagePath =
                                        item.photo,

                                    status =
                                        databaseStatusToScreenStatus(
                                            item.status
                                        )
                                )
                            }
                    }


                    override fun onFailure(

                        call:
                        Call<DonorListingsResponse>,

                        t:
                        Throwable
                    ) {

                        _isLoading.value =
                            false

                        _errorMessage.value =
                            t.message
                                ?: "Unable to connect to server"
                    }
                }
            )
    }


    // ==========================================
    // DATABASE STATUS -> SCREEN STATUS
    // ==========================================

    private fun databaseStatusToScreenStatus(
        status: String
    ): String {

        return when (
            status.lowercase()
        ) {

            "listed" ->
                "Listed"

            "reserved" ->
                "Reserved"

            "collected" ->
                "Collected"

            "cancelled" ->
                "Cancelled"

            else ->
                status.replaceFirstChar {
                    it.uppercase()
                }
        }
    }


    // ==========================================
    // TEMPORARY LOCAL ADD
    // ==========================================
    //
    // Kept for compatibility with the existing
    // Donor screens.
    //
    // AddListingScreen already saves new food
    // directly to MySQL.
    //
    // ==========================================

    fun add(
        listing: FoodListing
    ) {

        _listings.value =
            listOf(listing) +
                    _listings.value
    }


    // ==========================================
    // TEMPORARY LOCAL UPDATE
    // ==========================================
    //
    // We will replace this with a MySQL API
    // when connecting Donor Detail actions.
    //
    // ==========================================

    fun update(
        listing: FoodListing
    ) {

        _listings.value =
            _listings.value.map {

                if (
                    it.id == listing.id
                ) {

                    listing

                } else {

                    it
                }
            }
    }


    // ==========================================
    // TEMPORARY LOCAL DELETE
    // ==========================================
    //
    // We will replace this with a MySQL API
    // later.
    //
    // ==========================================

    fun delete(
        listing: FoodListing
    ) {

        _listings.value =
            _listings.value.filter {

                it.id != listing.id
            }
    }


    // ==========================================
    // CREATE IMAGE FILE
    // ==========================================

    private fun newFile(): File {

        return File(

            getApplication<Application>()
                .filesDir,

            "food_${System.currentTimeMillis()}.jpg"
        )
    }


    // ==========================================
    // SAVE GALLERY IMAGE
    // ==========================================

    fun saveImage(
        uri: Uri
    ): String? {

        return try {

            val file =
                newFile()


            getApplication<Application>()
                .contentResolver
                .openInputStream(uri)
                ?.use { input ->

                    file.outputStream()
                        .use { output ->

                            input.copyTo(output)
                        }
                }


            file.absolutePath

        } catch (e: Exception) {

            null
        }
    }


    // ==========================================
    // SAVE CAMERA IMAGE
    // ==========================================

    fun saveBitmap(
        bitmap: Bitmap
    ): String? {

        return try {

            val file =
                newFile()


            file.outputStream()
                .use { output ->

                    bitmap.compress(

                        Bitmap.CompressFormat.JPEG,

                        90,

                        output
                    )
                }


            file.absolutePath

        } catch (e: Exception) {

            null
        }
    }
}