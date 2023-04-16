package com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.repository

import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.local.file_folder_provider.FileFolderProvider
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.repository.FileFolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FileFolderRepositoryImpl(
    private val provider: FileFolderProvider
): FileFolderRepository {

    override fun getFileFolder(path: String): Flow<Resource<FileFolder>> = flow {
        emit(Resource.Loading())
        val fileFolders = provider.provideFileFolder(path)
        emit(Resource.Success(fileFolders))
    }
}