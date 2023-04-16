package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.repository

import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import kotlinx.coroutines.flow.Flow

interface FileFolderRepository {
    fun getFileFolder(path: String): Flow<Resource<FileFolder>>
}