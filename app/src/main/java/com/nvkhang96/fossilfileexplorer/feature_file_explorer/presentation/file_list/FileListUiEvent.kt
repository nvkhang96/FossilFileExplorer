package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder

sealed class FileListUiEvent {
    data class FileClick(val file: FileFolder): FileListUiEvent()
    object OpenFolderSuccess: FileListUiEvent()
}