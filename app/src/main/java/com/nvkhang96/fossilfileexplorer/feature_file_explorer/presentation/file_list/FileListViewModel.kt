package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvkhang96.fossilfileexplorer.core.presentation.util.INSTANT_SEARCH_DELAY
import com.nvkhang96.fossilfileexplorer.core.presentation.util.UiEvent
import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case.GetFileFolderUseCase
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case.GetPathListUseCase
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileListViewModel @Inject constructor(
    private val getFileFolderUseCase: GetFileFolderUseCase,
    private val getPathListUseCase: GetPathListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FileListState())
    val state: StateFlow<FileListState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _fileListEvent = MutableSharedFlow<FileListUiEvent>()
    val fileListEvent = _fileListEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var _rootPath = ""

    private var _getFileFolderJob: Job? = null
    private var _searchJob: Job? = null

    fun onIntent(event: FileListIntent) {
        when (event) {
            is FileListIntent.InitRoot -> {
                _state.value = state.value.copy(permissionDialogQueue = emptyList())
                if (_rootPath.isNotEmpty()) return

                _rootPath = event.rootPath
                val rootFileFolder = FileFolder(path = event.rootPath)
                onIntent(FileListIntent.ItemClick(rootFileFolder))
            }
            is FileListIntent.ItemClick -> {
                _state.value = state.value.copy(isSearchExpanded = false)
                _searchQuery.value = ""

                if (event.item.isDirectory) {
                    event.item.path?.let { path ->
                        getFileFolder(path, state.value.order)
                    }
                } else {
                    viewModelScope.launch {
                        _fileListEvent.emit(
                            FileListUiEvent.FileClick(event.item)
                        )
                    }
                }
            }
            is FileListIntent.OnBackPressed -> {
                state.value.folder.parentPath?.let { parentPath ->
                    when {
                        state.value.isSearchExpanded -> {
                            _state.value = state.value.copy(isSearchExpanded = false)
                            _searchQuery.value = ""
                            state.value.folder.path?.let { path ->
                                getFileFolder(path = path, order = state.value.order)
                            }
                        }
                        parentPath < _rootPath -> {
                            viewModelScope.launch {
                                _eventFlow.emit(UiEvent.ExitApp)
                            }
                        }
                        else -> {
                            onIntent(FileListIntent.ItemClick(FileFolder(path = parentPath)))
                        }
                    }
                }
            }
            is FileListIntent.DismissPermissionDialog -> {
                if (state.value.permissionDialogQueue.isEmpty()) return
                val copyQueue = ArrayList(state.value.permissionDialogQueue)
                copyQueue.removeFirst()

                _state.value = state.value.copy(
                    permissionDialogQueue = copyQueue
                )
            }
            is FileListIntent.PermissionResult -> {
                if (!event.isGranted && !state.value.permissionDialogQueue.contains(event.permission)) {
                    val copyQueue = ArrayList(state.value.permissionDialogQueue)
                    copyQueue.add(event.permission)

                    _state.value = state.value.copy(
                        permissionDialogQueue = copyQueue
                    )
                }
            }
            is FileListIntent.Order -> {
                if (state.value.order::class == event.order::class
                    && state.value.order.orderType == event.order.orderType
                ) {
                    return
                }
                _state.value = state.value.copy(
                    order = event.order
                )
                onIntent(FileListIntent.ItemClick(state.value.folder))
            }
            is FileListIntent.ToggleAscDesc -> {
                _state.value = state.value.copy(
                    order = state.value.order.copy(
                        if (state.value.order.orderType is OrderType.Ascending)
                            OrderType.Descending
                        else
                            OrderType.Ascending
                    )
                )
                onIntent(FileListIntent.ItemClick(state.value.folder))
            }
            is FileListIntent.ToggleSearch -> {
                _state.value = state.value.copy(isSearchExpanded = !state.value.isSearchExpanded)
            }
            is FileListIntent.Search -> {
                _searchQuery.value = event.query
                state.value.folder.path?.let { path ->
                    searchFileFolder(
                        path = path,
                        order = state.value.order,
                        query = event.query
                    )
                }
            }
        }
    }

    private fun searchFileFolder(
        path: String,
        order: FileFolderOrder,
        query: String = "",
    ) {
        _searchJob?.cancel()
        _searchJob = viewModelScope.launch {
            delay(INSTANT_SEARCH_DELAY)
            getFileFolder(path, order, query)
        }
    }

    private fun getFileFolder(
        path: String,
        order: FileFolderOrder,
        query: String = "",
    ) {
        _getFileFolderJob?.cancel()
        _getFileFolderJob = getFileFolderUseCase(path, order, query)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { newFileFolder ->
                            val pathList = newFileFolder.path?.let {
                                getPathListUseCase.invoke(it, _rootPath)
                            } ?: emptyList()
                            _state.value = state.value.copy(
                                folder = newFileFolder,
                                isRoot = newFileFolder.path == _rootPath,
                                isLoading = false,
                                paths = pathList
                            )
                            _fileListEvent.emit(
                                FileListUiEvent.OpenFolderSuccess(newFileFolder.path?.length ?: 0)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(isLoading = false)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Unknown error"))
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
    }
}