package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder

sealed class FileListIntent {
    data class ItemClick(val item: FileFolder): FileListIntent()

    object OnBackPressed: FileListIntent()

    data class InitRoot(val rootPath: String): FileListIntent()

    object DismissPermissionDialog: FileListIntent()

    data class PermissionResult(val permission: String, val isGranted: Boolean): FileListIntent()

    data class Order(val order: FileFolderOrder): FileListIntent()

    object ToggleAscDesc: FileListIntent()

    object ToggleSearch: FileListIntent()

    data class Search(val query: String): FileListIntent()
}
