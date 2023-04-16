package com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.local.file_folder_provider

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder

interface FileFolderProvider {
    fun provideFileFolder(path: String): FileFolder
}