package com.tf.clasificacioncutter.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.BottomNavItem
import com.tf.clasificacioncutter.ui.theme.CutterPrimary

object Screen {
    val BottomNavItems = listOf(
        BottomNavItem(
            labelRes = R.string.search,
            icon = Icons.Filled.Search,
            route = "search"
        ),
        BottomNavItem(
            labelRes = R.string.cutter_table,
            icon = Icons.AutoMirrored.Filled.List,
            route = "table"
        ),
        BottomNavItem(
            labelRes = R.string.settings,
            icon = Icons.Filled.Settings,
            route = "settings"
        ),
        BottomNavItem(
            labelRes = R.string.about,
            icon = Icons.Filled.Info,
            route = "about"
        )
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val isDark = isSystemInDarkTheme()
    val barBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary
    val iconSelectedColor = if (isDark) MaterialTheme.colorScheme.primary else CutterPrimary
    val textSelectedColor = if (isDark) MaterialTheme.colorScheme.primary else Color.White
    val indicatorBgColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else Color.White
    val unselectedColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.7f)

    NavigationBar(
        containerColor = barBgColor,
        contentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Screen.BottomNavItems.forEach { navItem ->

            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = stringResource(navItem.labelRes)
                    )
                },
                label = {
                    Text(text = stringResource(navItem.labelRes))
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = iconSelectedColor,
                    selectedTextColor = textSelectedColor,
                    indicatorColor = indicatorBgColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}
