package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.util

sealed class Screen(val route: String) {
    object FileListScreen: Screen("file_list_screen")
}