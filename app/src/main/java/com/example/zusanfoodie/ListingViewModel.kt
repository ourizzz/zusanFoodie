package com.example.zusanfoodie

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class ListingViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dao()

    val listings = dao.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Realistic sample data on first launch
        viewModelScope.launch {
            if (dao.count() == 0) {
                dao.insert(FoodListing(name = "Nasi lemak packs", category = "Meals", quantity = 12, unit = "packs",
                    area = "Padungan", pickupDate = "08/10/2026", pickupTime = "20:00", expiry = "08/10/2026 23:00",
                    notes = "Halal, contains peanuts", imagePath = null))
                dao.insert(FoodListing(name = "Fresh vegetables", category = "Produce", quantity = 5, unit = "kg",
                    area = "Satok", pickupDate = "08/10/2026", pickupTime = "18:00", expiry = "09/10/2026 12:00",
                    notes = "Mixed greens", imagePath = null, status = "Reserved", recipient = "Aiman Yusuf", volunteer = "Siti N."))
                dao.insert(FoodListing(name = "Bread loaves", category = "Bakery", quantity = 20, unit = "loaves",
                    area = "Petra Jaya", pickupDate = "07/10/2026", pickupTime = "09:00", expiry = "07/10/2026 18:00",
                    notes = "", imagePath = null, status = "Collected", recipient = "Rumah Kasih", volunteer = "Daniel L."))
            }
        }
    }

    fun add(l: FoodListing) = viewModelScope.launch { dao.insert(l) }
    fun update(l: FoodListing) = viewModelScope.launch { dao.update(l) }
    fun delete(l: FoodListing) = viewModelScope.launch { dao.delete(l) }

    private fun newFile() = File(getApplication<Application>().filesDir, "food_${System.currentTimeMillis()}.jpg")

    /** Copy picked gallery image into app internal storage */
    fun saveImage(uri: Uri): String? = try {
        val f = newFile()
        getApplication<Application>().contentResolver.openInputStream(uri)?.use { i -> f.outputStream().use { o -> i.copyTo(o) } }
        f.absolutePath
    } catch (e: Exception) { null }

    /** Save camera photo into internal storage */
    fun saveBitmap(bmp: Bitmap): String? = try {
        val f = newFile()
        f.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        f.absolutePath
    } catch (e: Exception) { null }
}
