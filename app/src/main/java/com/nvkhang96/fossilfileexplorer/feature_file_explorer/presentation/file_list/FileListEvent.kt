package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder

sealed class FileListEvent {
    data class ItemClick(val item: FileFolder): FileListEvent()

    object OnBackPressed: FileListEvent()

    data class InitRoot(val rootPath: String): FileListEvent()

    object DismissPermissionDialog: FileListEvent()

    data class PermissionResult(val permission: String, val isGranted: Boolean): FileListEvent()

    data class Order(val order: FileFolderOrder): FileListEvent()

    object ToggleAscDesc: FileListEvent()

    object ToggleSearch: FileListEvent()

    data class Search(val query: String): FileListEvent()
}
