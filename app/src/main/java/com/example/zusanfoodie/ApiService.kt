package com.example.zusanfoodie

import okhttp3.MultipartBody

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


// ==========================================
// LOGIN RESPONSE
// ==========================================

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user_id: Int?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val role: String?
)


// ==========================================
// COLLECTOR PICKUP DATA
// ==========================================

data class CollectorPickup(
    val pickup_id: Int,
    val pickup_status: String,

    val reservation_id: Int,

    val food_name: String,
    val quantity: Int,
    val unit: String,

    val pickup_location: String,
    val delivery_location: String,

    val pickup_date: String,
    val pickup_start_time: String,
    val pickup_end_time: String?,

    val donor_id: Int,
    val donor_name: String,
    val donor_phone: String?,

    val recipient_id: Int,
    val recipient_name: String,
    val recipient_phone: String?,

    val completed_at: String? = null
)


// ==========================================
// COLLECTOR PICKUPS RESPONSE
// ==========================================

data class CollectorPickupsResponse(
    val success: Boolean,
    val pickups: List<CollectorPickup>,
    val message: String? = null
)


// ==========================================
// UPDATE PICKUP STATUS RESPONSE
// ==========================================

data class UpdatePickupStatusResponse(
    val success: Boolean,
    val message: String
)


// ==========================================
// ASSIGN PICKUP RESPONSE
// ==========================================

data class AssignPickupResponse(
    val success: Boolean,
    val message: String
)


// ==========================================
// RECIPIENT FOOD DATA
// ==========================================

data class RecipientFoodListing(
    val listing_id: Int,
    val donor_id: Int,

    val donor_name: String,
    val donor_phone: String?,

    val food_name: String,
    val category: String,

    val quantity: Int,
    val unit: String,

    val pickup_location: String,
    val pickup_date: String,
    val pickup_start_time: String,
    val pickup_end_time: String?,

    val safe_until: String?,
    val notes: String?,
    val photo: String?,

    val status: String,
    val created_at: String
)


// ==========================================
// RECIPIENT FOODS RESPONSE
// ==========================================

data class RecipientFoodsResponse(
    val success: Boolean,
    val foods: List<RecipientFoodListing>,
    val message: String? = null
)


// ==========================================
// CREATE RESERVATION RESPONSE
// ==========================================

data class CreateReservationResponse(
    val success: Boolean,
    val message: String,

    val reservation_id: Int? = null,
    val pickup_id: Int? = null,
    val remaining_quantity: Int? = null
)


// ==========================================
// RECIPIENT RESERVATION DATA
// ==========================================

data class RecipientReservation(
    val reservation_id: Int,
    val listing_id: Int,
    val recipient_id: Int,

    val quantity: Int,
    val delivery_location: String,
    val status: String,
    val created_at: String,

    val food_name: String,
    val category: String,
    val unit: String,

    val pickup_location: String,
    val pickup_date: String,
    val pickup_start_time: String,
    val pickup_end_time: String?,

    val safe_until: String?,
    val notes: String?,
    val photo: String?,

    val donor_id: Int,
    val donor_name: String,
    val donor_phone: String?,

    val pickup_id: Int?,
    val collector_id: Int?,
    val pickup_status: String?,
    val completed_at: String?
)


// ==========================================
// RECIPIENT RESERVATIONS RESPONSE
// ==========================================

data class RecipientReservationsResponse(
    val success: Boolean,
    val reservations: List<RecipientReservation>,
    val message: String? = null
)


// ==========================================
// DONOR FOOD LISTING DATA
// ==========================================

data class DonorListing(
    val listing_id: Int,
    val donor_id: Int,

    val food_name: String,
    val category: String,

    val quantity: Int,
    val unit: String,

    val pickup_location: String,
    val pickup_date: String,
    val pickup_start_time: String,
    val pickup_end_time: String?,

    val safe_until: String?,
    val notes: String?,
    val photo: String?,

    val status: String,
    val created_at: String
)


// ==========================================
// DONOR LISTINGS RESPONSE
// ==========================================

data class DonorListingsResponse(
    val success: Boolean,
    val listings: List<DonorListing>,
    val message: String? = null
)


// ==========================================
// ADD FOOD LISTING RESPONSE
// ==========================================

data class AddFoodListingResponse(
    val success: Boolean,
    val message: String,
    val listing_id: Int? = null
)


// ==========================================
// UPLOAD PHOTO RESPONSE
// ==========================================

data class UploadPhotoResponse(
    val success: Boolean,
    val message: String,
    val photo: String? = null
)


// ==========================================
// API SERVICE
// ==========================================

interface ApiService {


    // ======================================
    // LOGIN
    // ======================================

    @FormUrlEncoded
    @POST("login.php")
    fun login(

        @Field("email")
        email: String,

        @Field("password")
        password: String

    ): Call<LoginResponse>


    // ======================================
    // COLLECTOR HOME
    // ======================================

    @GET("collector_pickups.php")
    fun getCollectorPickups(

        @Query("collector_id")
        collectorId: Int

    ): Call<CollectorPickupsResponse>


    // ======================================
    // COLLECTOR - AVAILABLE PICKUPS
    // ======================================

    @GET("available_pickups.php")
    fun getAvailablePickups():
            Call<CollectorPickupsResponse>


    // ======================================
    // COLLECTOR - ACCEPT PICKUP
    // ======================================

    @FormUrlEncoded
    @POST("assign_pickup.php")
    fun assignPickup(

        @Field("pickup_id")
        pickupId: Int,

        @Field("collector_id")
        collectorId: Int

    ): Call<AssignPickupResponse>


    // ======================================
    // COLLECTOR HISTORY
    // ======================================

    @GET("collector_history.php")
    fun getCollectorHistory(

        @Query("collector_id")
        collectorId: Int

    ): Call<CollectorPickupsResponse>


    // ======================================
    // UPDATE PICKUP STATUS
    // ======================================

    @FormUrlEncoded
    @POST("update_pickup_status.php")
    fun updatePickupStatus(

        @Field("pickup_id")
        pickupId: Int,

        @Field("pickup_status")
        pickupStatus: String

    ): Call<UpdatePickupStatusResponse>


    // ======================================
    // RECIPIENT HOME
    // ======================================

    @GET("recipient_foods.php")
    fun getRecipientFoods():
            Call<RecipientFoodsResponse>


    // ======================================
    // CREATE RESERVATION
    // ======================================

    @FormUrlEncoded
    @POST("create_reservation.php")
    fun createReservation(

        @Field("listing_id")
        listingId: Int,

        @Field("recipient_id")
        recipientId: Int,

        @Field("quantity")
        quantity: Int,

        @Field("delivery_location")
        deliveryLocation: String

    ): Call<CreateReservationResponse>


    // ======================================
    // RECIPIENT RESERVATION HISTORY
    // ======================================

    @GET("recipient_reservations.php")
    fun getRecipientReservations(

        @Query("recipient_id")
        recipientId: Int

    ): Call<RecipientReservationsResponse>


    // ======================================
    // DONOR - GET MY LISTINGS
    // ======================================

    @GET("donor_listings.php")
    fun getDonorListings(

        @Query("donor_id")
        donorId: Int

    ): Call<DonorListingsResponse>


    // ======================================
    // DONOR - UPLOAD FOOD PHOTO
    // ======================================

    @Multipart
    @POST("upload_food_photo.php")
    fun uploadFoodPhoto(

        @Part
        photo: MultipartBody.Part

    ): Call<UploadPhotoResponse>


    // ======================================
    // DONOR - ADD FOOD LISTING
    // ======================================

    @FormUrlEncoded
    @POST("add_food_listing.php")
    fun addFoodListing(

        @Field("donor_id")
        donorId: Int,

        @Field("food_name")
        foodName: String,

        @Field("category")
        category: String,

        @Field("quantity")
        quantity: Int,

        @Field("unit")
        unit: String,

        @Field("pickup_location")
        pickupLocation: String,

        @Field("pickup_date")
        pickupDate: String,

        @Field("pickup_start_time")
        pickupStartTime: String,

        @Field("safe_until")
        safeUntil: String,

        @Field("notes")
        notes: String,

        @Field("photo")
        photo: String

    ): Call<AddFoodListingResponse>
}