package com.nvkhang96.fossilfileexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                            val viewModel = hiltViewModel<FileListViewModel>()
                            val state by viewModel.state.collectAsState()
                            val searchQuery by viewModel.searchQuery.collectAsState()

                            FileListScreen(
                                state = state,
                                searchQuery = searchQuery,
                                onIntent = viewModel::onIntent,
                                uiEventFlow = viewModel.eventFlow,
                                fileListUiEventFlow = viewModel.fileListEvent
                            )
                        }
                    }
                }
            }
        }
    }
}