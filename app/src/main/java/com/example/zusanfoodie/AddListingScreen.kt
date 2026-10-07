package com.example.zusanfoodie

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
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(vm: ListingViewModel, nav: NavController) {
    val ctx = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Meals") }
    var qty by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf(units[0]) }
    var area by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var expiry by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var imagePath by rememberSaveable { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) imagePath = vm.saveImage(uri) ?: run { Toast.makeText(ctx, "Could not load that photo. Try another.", Toast.LENGTH_SHORT).show(); null }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        if (bmp != null) imagePath = vm.saveBitmap(bmp)
    }

    val qtyInt = qty.toIntOrNull()
    val nameErr = submitted && name.isBlank()
    val qtyErr = submitted && (qtyInt == null || qtyInt < 1)
    val areaErr = submitted && area.isBlank()
    val dateErr = submitted && (date.isBlank() || time.isBlank())
    val expErr = submitted && expiry.isBlank()

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(Modifier.fillMaxWidth().background(Azure).padding(8.dp, 14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
            Text("New food listing", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Photo
            Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)).background(AzureLight), contentAlignment = Alignment.Center) {
                val p = imagePath
                if (p != null) AsyncImage(File(p), "Food photo", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Text("📷  No photo yet", color = AzureDark, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton({ gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    Modifier.weight(1f)) { Text("🖼 Gallery") }
                OutlinedButton({ camera.launch(null) }, Modifier.weight(1f)) { Text("📷 Camera") }
            }

            OutlinedTextField(name, { name = it }, label = { Text("Food name") }, singleLine = true, isError = nameErr,
                supportingText = { if (nameErr) Text("Enter the food name") }, modifier = Modifier.fillMaxWidth())

            Text("Category", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { c ->
                    FilterChip(category == c, { category = c }, { Text(c) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Azure, selectedLabelColor = Color.White))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                OutlinedTextField(qty, { qty = it.filter(Char::isDigit) }, label = { Text("Quantity") }, singleLine = true, isError = qtyErr,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { if (qtyErr) Text("Enter 1 or more") }, modifier = Modifier.weight(1f))
                Dropdown("Unit", unit, units, false, Modifier.weight(1f)) { unit = it }
            }
            Dropdown("Collection area", area, areas, areaErr, Modifier.fillMaxWidth(), "Choose a collection area") { area = it }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton({ pickDate(ctx) { date = it } }, Modifier.weight(1f)) { Text(date.ifBlank { "📅 Pickup date" }) }
                OutlinedButton({ pickTime(ctx) { time = it } }, Modifier.weight(1f)) { Text(time.ifBlank { "🕘 Pickup time" }) }
            }
            if (dateErr) Text("Choose a pickup date and time", color = Color(0xFFD63A3A), fontSize = 12.5.sp)

            OutlinedButton({ pickDate(ctx) { d -> pickTime(ctx) { t -> expiry = "$d $t" } } }, Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, if (expErr) Color(0xFFD63A3A) else Color.Gray)) { Text(expiry.ifBlank { "🕙 Safe to eat until (date & time)" }) }
            if (expErr) Text("Choose when the food stops being safe to eat", color = Color(0xFFD63A3A), fontSize = 12.5.sp)

            OutlinedTextField(notes, { notes = it }, label = { Text("Notes for collector") }, modifier = Modifier.fillMaxWidth())
        }
        Surface(shadowElevation = 8.dp, color = Color.White) {
            PrimaryButton("Publish listing", Modifier.padding(18.dp, 10.dp)) {
                submitted = true
                val ok = name.isNotBlank() && qtyInt != null && qtyInt >= 1 && area.isNotBlank() &&
                        date.isNotBlank() && time.isNotBlank() && expiry.isNotBlank()
                if (!ok) {
                    Toast.makeText(ctx, "Please fix the highlighted fields", Toast.LENGTH_SHORT).show()
                } else {
                    vm.add(FoodListing(name = name.trim(), category = category, quantity = qtyInt!!, unit = unit, area = area,
                        pickupDate = date, pickupTime = time, expiry = expiry, notes = notes.trim(), imagePath = imagePath))
                    Toast.makeText(ctx, "Listing published", Toast.LENGTH_SHORT).show()
                    nav.popBackStack()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(label: String, value: String, options: List<String>, isError: Boolean, modifier: Modifier,
             placeholder: String = "", onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(open, { open = !open }, modifier) {
        OutlinedTextField(value, {}, readOnly = true, label = { Text(label) }, isError = isError,
            placeholder = { Text(placeholder) },
            supportingText = { if (isError) Text("Please choose one") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(open) },
            modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(open, { open = false }) {
            options.forEach { o -> DropdownMenuItem({ Text(o) }, { onSelect(o); open = false }) }
        }
    }
}