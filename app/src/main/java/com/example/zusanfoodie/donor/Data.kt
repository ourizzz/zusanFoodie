package com.example.zusanfoodie.donor


// ==========================================
// DONOR FOOD LISTING MODEL
// ==========================================
//
// This model is used by the Donor screens.
//
// Room database has been removed because
// ZusanFoodie now uses:
//
// Android App
//      ↓
// Retrofit
//      ↓
// PHP API
//      ↓
// MySQL
//
// ==========================================

data class FoodListing(

    val id: Int = 0,

    val name: String = "",

    val category: String = "",

    val quantity: Int = 0,

    val unit: String = "",

    val area: String = "",

    val pickupDate: String = "",

    val pickupTime: String = "",

    val expiry: String = "",

    val notes: String = "",

    val imagePath: String? = null,

    val status: String = "Listed",

    val recipient: String = "",

    val volunteer: String = "",

    val createdAt: Long = System.currentTimeMillis()
)