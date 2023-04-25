package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case

import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import kotlinx.coroutines.flow.Flow

interface GetFileFolderUseCase {
    operator fun invoke(
        path: String,
        order: FileFolderOrder,
        query: String,
    ): Flow<Resource<FileFolder>>
}