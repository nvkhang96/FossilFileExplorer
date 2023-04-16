package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case

import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.repository.FileFolderRepository
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFileFolder(
    private val repository: FileFolderRepository
) {

    operator fun invoke(
        path: String,
        order: FileFolderOrder = FileFolderOrder.Name(OrderType.Ascending),
        query: String = "",
    ): Flow<Resource<FileFolder>> {
        return repository.getFileFolder(path)
            .map { result ->
                if (result is Resource.Success) {
                    Resource.Success(
                        result.data?.copy(
                            children = result.data.children
                                .filter { it.name.lowercase().contains(query.lowercase()) }
                        )
                    )
                } else {
                    result
                }
            }
            .map { result ->
                if (result is Resource.Success) {
                    Resource.Success(
                        when (order.orderType) {
                            is OrderType.Ascending -> {
                                when (order) {
                                    is FileFolderOrder.Name -> result.data?.copy(
                                        children = result.data.children.sortedBy { it.name }
                                    )
                                    is FileFolderOrder.Date -> result.data?.copy(
                                        children = result.data.children.sortedBy { it.lastModified }
                                    )
                                    is FileFolderOrder.Type -> result.data?.copy(
                                        children = result.data.children.sortedBy { it.extension }
                                    )
                                    is FileFolderOrder.Size -> result.data?.copy(
                                        children = result.data.children.sortedBy { it.size }
                                    )
                                }
                            }
                            is OrderType.Descending -> {
                                when (order) {
                                    is FileFolderOrder.Name -> result.data?.copy(
                                        children = result.data.children.sortedByDescending { it.name }
                                    )
                                    is FileFolderOrder.Date -> result.data?.copy(
                                        children = result.data.children.sortedByDescending { it.lastModified }
                                    )
                                    is FileFolderOrder.Type -> result.data?.copy(
                                        children = result.data.children.sortedByDescending { it.extension }
                                    )
                                    is FileFolderOrder.Size -> result.data?.copy(
                                        children = result.data.children.sortedByDescending { it.size }
                                    )
                                }
                            }
                        }
                    )
                } else {
                    result
                }
            }
    }
}