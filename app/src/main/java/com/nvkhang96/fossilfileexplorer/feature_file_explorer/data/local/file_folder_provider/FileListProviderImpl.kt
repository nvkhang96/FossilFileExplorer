package com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.local.file_folder_provider

import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.util.toFileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import java.io.File

class FileFolderProviderImpl : FileFolderProvider {

    override fun provideFileFolder(path: String): FileFolder {
        val file = File(path)
        val fileChildren = file.listFiles() ?: emptyArray()

        return FileFolder(
            name = file.name,
            path = file.path,
            parentPath = file.parent,
            childrenCount = fileChildren.size,
            children = fileChildren.map { it.toFileFolder() }
        )
    }
}