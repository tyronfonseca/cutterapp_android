package com.tf.clasificacioncutter.ui.screens.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.ui.components.BottomNavigationBar
import com.tf.clasificacioncutter.ui.screens.about.AboutScreen
import com.tf.clasificacioncutter.ui.screens.cuttertable.CutterTableScreen
import com.tf.clasificacioncutter.ui.screens.search.SearchScreen
import com.tf.clasificacioncutter.ui.screens.search.SearchViewModel
import com.tf.clasificacioncutter.ui.screens.search.edit.EditSearchItemScreen
import com.tf.clasificacioncutter.ui.screens.settings.SettingsScreen
import com.tf.clasificacioncutter.ui.screens.settings.tables.SelectCutterTableScreen
import com.tf.clasificacioncutter.ui.screens.settings.tables.add.AddCutterTableScreen
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import com.tf.clasificacioncutter.utils.extensions.lockPortraitOnPhones

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockPortraitOnPhones()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {
            CutterTheme {
                val navController = rememberNavController()
                val isDark = isSystemInDarkTheme()
                val rootBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary

                CompositionLocalProvider(LocalMainViewModel provides viewModel) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = rootBgColor
                    ) {
                        Scaffold(
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            containerColor = MaterialTheme.colorScheme.background,
                            bottomBar = {
                                BottomNavigationBar(navController = navController)
                            },
                            content = { padding ->
                                NavHostContainer(
                                    navController = navController,
                                    padding = padding
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun NavHostContainer(
        navController: NavHostController,
        padding: PaddingValues
    ) {
        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier.padding(paddingValues = padding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable("search") {
                SearchScreen(navController)
            }
            composable("table") {
                CutterTableScreen(navController)
            }
            composable("settings") {
                SettingsScreen(navController)
            }
            composable("about") {
                AboutScreen()
            }
            composable("select_table") {
                SelectCutterTableScreen(navController)
            }
            composable("add_table") {
                AddCutterTableScreen(navController)
            }
            composable("edit_search_item") {
                val item = navController.previousBackStackEntry?.savedStateHandle?.get<CutterData>("cutter_data")
                if (item != null) {
                    val viewModel: SearchViewModel = viewModel(
                        factory = SearchViewModel.provideFactory(
                            AppDatabase.getDatabase(LocalContext.current).cutterDataDao(),
                            AppDatabase.getDatabase(LocalContext.current).isbnCacheDao()
                        )
                    )
                    EditSearchItemScreen(
                        navController = navController,
                        item = item,
                        onSave = { updatedItem ->
                            viewModel.updateItem(updatedItem)
                        },
                        onDelete = { id ->
                            viewModel.deleteItem(id)
                        }
                    )
                }
            }
        }
    }
}
