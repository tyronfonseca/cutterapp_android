package com.tf.clasificacioncutter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tf.clasificacioncutter.data.BottomNavItem
import com.tf.clasificacioncutter.ui.components.Screen.BottomNavItems

object Screen {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Search",
            icon = Icons.Filled.Search,
            route = "search"
        ),
        BottomNavItem(
            label = "About",
            icon = Icons.Filled.Info,
            route = "about"
        ),
        BottomNavItem(
            label = "Settings",
            icon = Icons.Filled.Settings,
            route = "settings"
        )
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavItems.forEach { navItem ->

            NavigationBarItem(

                selected = currentRoute == navItem.route,

                onClick = {
                    navController.navigate(navItem.route)
                },

                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },

                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = true
            )
        }
    }
}