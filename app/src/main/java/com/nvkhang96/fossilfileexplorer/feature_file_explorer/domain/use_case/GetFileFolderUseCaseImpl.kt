package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case

import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.repository.FileFolderRepository
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFileFolderUseCaseImpl @Inject constructor(
    private val repository: FileFolderRepository
) : GetFileFolderUseCase {

    override operator fun invoke(
        path: String,
        order: FileFolderOrder,
        query: String,
    ): Flow<Resource<FileFolder>> {
        return repository.getFileFolder(path)
            .map { result -> result.filterByQuery(query) }
            .map { result -> result.sortByOrder(order) }
    }

    private fun Resource<FileFolder>.filterByQuery(query: String): Resource<FileFolder> {
        if (this !is Resource.Success) return this

        return Resource.Success(
            data?.copy(
                children = data.children.filter {
                    it.name.lowercase()
                        .contains(query.lowercase())
                }
            )
        )
    }

    private fun Resource<FileFolder>.sortByOrder(order: FileFolderOrder): Resource<FileFolder> {
        if (this !is Resource.Success) return this

        return Resource.Success(
            data?.copy(
                children = when (order.orderType) {
                    is OrderType.Ascending -> {
                        when (order) {
                            is FileFolderOrder.Name -> data.children.sortedBy { it.name }
                            is FileFolderOrder.Date -> data.children.sortedBy { it.lastModified }
                            is FileFolderOrder.Type -> data.children.sortedBy { it.extension }
                            is FileFolderOrder.Size -> data.children.sortedBy { it.size }
                        }
                    }
                    is OrderType.Descending -> {
                        when (order) {
                            is FileFolderOrder.Name -> data.children.sortedByDescending { it.name }
                            is FileFolderOrder.Date -> data.children.sortedByDescending { it.lastModified }
                            is FileFolderOrder.Type -> data.children.sortedByDescending { it.extension }
                            is FileFolderOrder.Size -> data.children.sortedByDescending { it.size }
                        }
                    }
                }
            )
        )
    }
}