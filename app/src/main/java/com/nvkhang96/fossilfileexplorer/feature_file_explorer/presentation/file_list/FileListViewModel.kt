package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nvkhang96.fossilfileexplorer.core.presentation.util.INSTANT_SEARCH_DELAY
import com.nvkhang96.fossilfileexplorer.core.presentation.util.UiEvent
import com.nvkhang96.fossilfileexplorer.core.util.Resource
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.use_case.GetFileFolderUseCase
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
    private val getFileFolderUseCase: GetFileFolderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FileListState())
    val state: StateFlow<FileListState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _fileListEvent = MutableSharedFlow<FileListUiEvent>()
    val fileListEvent = _fileListEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val paths: StateFlow<List<Pair<String, String>>> = _state.map { state ->
        state.folder.path?.let {path ->
            path
                .drop(_rootPath.length)
                .split("/")
                .filter { it.isNotBlank() }
                .runningFold(Pair("Internal storage", _rootPath)) { acc, s ->
                    Pair(s, "${acc.second}/$s")
                }
        }?: emptyList()
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000)
    )

    private var _rootPath = ""

    private var _getFileFolderJob: Job? = null
    private var _searchJob: Job? = null

    fun onEvent(event: FileListEvent) {
        when (event) {
            is FileListEvent.InitRoot -> {
                _state.value = state.value.copy(permissionDialogQueue = emptyList())
                if (_rootPath.isNotEmpty()) return

                _rootPath = event.rootPath
                val rootFileFolder = FileFolder(path = event.rootPath)
                onEvent(FileListEvent.ItemClick(rootFileFolder))
            }
            is FileListEvent.ItemClick -> {
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
            is FileListEvent.OnBackPressed -> {
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
                            onEvent(FileListEvent.ItemClick(FileFolder(path = parentPath)))
                        }
                    }
                }
            }
            is FileListEvent.DismissPermissionDialog -> {
                if (state.value.permissionDialogQueue.isEmpty()) return
                val copyQueue = ArrayList(state.value.permissionDialogQueue)
                copyQueue.removeFirst()

                _state.value = state.value.copy(
                    permissionDialogQueue = copyQueue
                )
            }
            is FileListEvent.PermissionResult -> {
                if (!event.isGranted && !state.value.permissionDialogQueue.contains(event.permission)) {
                    val copyQueue = ArrayList(state.value.permissionDialogQueue)
                    copyQueue.add(event.permission)

                    _state.value = state.value.copy(
                        permissionDialogQueue = copyQueue
                    )
                }
            }
            is FileListEvent.Order -> {
                if (state.value.order::class == event.order::class
                    && state.value.order.orderType == event.order.orderType
                ) {
                    return
                }
                _state.value = state.value.copy(
                    order = event.order
                )
                onEvent(FileListEvent.ItemClick(state.value.folder))
            }
            is FileListEvent.ToggleAscDesc -> {
                _state.value = state.value.copy(
                    order = state.value.order.copy(
                        if (state.value.order.orderType is OrderType.Ascending)
                            OrderType.Descending
                        else
                            OrderType.Ascending
                    )
                )
                onEvent(FileListEvent.ItemClick(state.value.folder))
            }
            is FileListEvent.ToggleSearch -> {
                _state.value = state.value.copy(isSearchExpanded = !state.value.isSearchExpanded)
            }
            is FileListEvent.Search -> {
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
                            _state.value = state.value.copy(
                                folder = newFileFolder,
                                isRoot = newFileFolder.path == _rootPath,
                                isLoading = false
                            )
                            _fileListEvent.emit(FileListUiEvent.OpenFolderSuccess)
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