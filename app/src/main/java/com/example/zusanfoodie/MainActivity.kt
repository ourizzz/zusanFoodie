package com.example.zusanfoodie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Azure, onPrimary = Color.White,
                primaryContainer = AzureLight, onPrimaryContainer = AzureDark)) { App() }
        }
    }
}

fun NavController.goTab(route: String) = navigate(route) {
    popUpTo("home") { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun App(vm: ListingViewModel = viewModel()) {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val tabs = listOf(
        Triple("home", "Home", Icons.Default.Home),
        Triple("listings", "Listings", Icons.AutoMirrored.Filled.List),
        Triple("pickups", "Pickups", Icons.Default.LocationOn),
        Triple("history", "History", Icons.Default.DateRange))

    Scaffold(bottomBar = {
        if (tabs.any { it.first == route }) NavigationBar(containerColor = Color.White) {
            tabs.forEach { (r, label, icon) ->
                NavigationBarItem(selected = route == r, onClick = { nav.goTab(r) }, icon = { Icon(icon, label) }, label = { Text(label) })
            }
        }
    }) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") { HomeScreen(vm, nav) }
            composable("listings") { ListingsScreen(vm, nav) }
            composable("pickups") { StatusListScreen(vm, nav, "Pickups", "Reserved", "No reserved pickups right now.") }
            composable("history") { StatusListScreen(vm, nav, "Donation history", "Collected", "Collected donations will appear here.") }
            composable("add") { AddListingScreen(vm, nav) }
            composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
                DetailScreen(it.arguments!!.getInt("id"), vm, nav)
            }
        }
    }
}