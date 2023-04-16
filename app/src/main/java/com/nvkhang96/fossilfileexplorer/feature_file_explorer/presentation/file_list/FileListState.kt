package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.OrderType

data class FileListState (
    val folder: FileFolder = FileFolder(),
    val permissionDialogQueue: List<String> = emptyList(),
    val isRoot: Boolean = true,
    val order: FileFolderOrder = FileFolderOrder.Name(OrderType.Ascending),
    val isSearchExpanded: Boolean = false,
    val isLoading: Boolean = false,
)