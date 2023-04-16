package com.nvkhang96.fossilfileexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.FileListScreen
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.FileListViewModel
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.util.Screen
import com.nvkhang96.fossilfileexplorer.ui.theme.FossilFileExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FossilFileExplorerTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.FileListScreen.route,
                    ) {
                        composable(route = Screen.FileListScreen.route) {
                            FileListScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}