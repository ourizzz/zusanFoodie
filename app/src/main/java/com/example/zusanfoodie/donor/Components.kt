package com.example.zusanfoodie.donor
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import java.io.File
import java.util.Calendar

val Azure = Color(0xFF2B2FA0)
val AzureDark = Color(0xFF0059B8)
val AzureLight = Color(0xFFE3F0FF)
val ScreenBg = Color(0xFFF4F8FD)
val Ink = Color(0xFF0F1E33)
val Muted = Color(0xFF5C6B80)

val categories = listOf("Meals", "Bakery", "Produce", "Package")
val units = listOf("packs", "kg", "loaves", "boxes", "items")
val areas = listOf("Sungai Buloh", "Petaling Jaya", "Rawang", "Cheras", "Kuala Selangor", "Shah Alam")

fun emojiFor(cat: String) = when (cat) { "Meals" -> "🍛"; "Bakery" -> "🍞"; "Produce" -> "🥬"; else -> "📦" }

@Composable
fun FoodThumb(l: FoodListing, modifier: Modifier) {
    val path = l.imagePath
    if (path != null && File(path).exists()) {
        AsyncImage(model = File(path), contentDescription = l.name, contentScale = ContentScale.Crop, modifier = modifier)
    } else {
        Box(modifier.background(AzureLight), contentAlignment = Alignment.Center) { Text(emojiFor(l.category), fontSize = 36.sp) }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "Reserved" -> Color(0xFFFFF1D6) to Color(0xFF9A5C00)
        "Collected" -> Color(0xFFDDF5E8) to Color(0xFF14724A)
        else -> AzureLight to AzureDark
    }
    Text(status, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bg).padding(horizontal = 10.dp, vertical = 3.dp))
}

@Composable
fun ListingCard(l: FoodListing, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDCE5F0)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FoodThumb(l, Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("${l.name} (${l.quantity} ${l.unit})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                Text("${l.category} · ${l.area}", fontSize = 12.5.sp, color = Muted)
                Text("Pickup ${l.pickupDate}, ${l.pickupTime}", fontSize = 12.5.sp, color = Muted)
                StatusChip(l.status)
            }
        }
    }
}

@Composable
fun EmptyState(msg: String) {
    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(msg, color = Muted, fontSize = 14.sp)
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, shape = RoundedCornerShape(14.dp), modifier = modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Azure)) { Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AzureLight, contentColor = AzureDark)) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

fun pickDate(ctx: Context, onPicked: (String) -> Unit) {
    val c = Calendar.getInstance()
    DatePickerDialog(ctx, { _, y, m, d -> onPicked("%02d/%02d/%04d".format(d, m + 1, y)) },
        c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]).apply { datePicker.minDate = System.currentTimeMillis() - 1000 }.show()
}

fun pickTime(ctx: Context, onPicked: (String) -> Unit) {
    TimePickerDialog(ctx, { _, h, m -> onPicked("%02d:%02d".format(h, m)) }, 18, 0, true).show()
}
