package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.nvkhang96.fossilfileexplorer.BuildConfig
import com.nvkhang96.fossilfileexplorer.core.presentation.BackPressHandler
import com.nvkhang96.fossilfileexplorer.core.presentation.ManageAllFilesPermissionTextProvider
import com.nvkhang96.fossilfileexplorer.core.presentation.PermissionDialog
import com.nvkhang96.fossilfileexplorer.core.presentation.StoragePermissionTextProvider
import com.nvkhang96.fossilfileexplorer.core.presentation.util.MIN_MILLIS_HUMAN_CAN_RECOGNIZE_60_HZ
import com.nvkhang96.fossilfileexplorer.core.presentation.util.UiEvent
import com.nvkhang96.fossilfileexplorer.core.util.findActivity
import com.nvkhang96.fossilfileexplorer.core.util.openAppSettings
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.util.openWithSomeApp
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.FileFolderOrder
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component.ExpandableSearchIcon
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component.FileFolderItem
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component.FileListPath
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.util.getImageVector
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileListScreen(
    state: FileListState,
    searchQuery: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onIntent: (FileListIntent) -> Unit,
    uiEventFlow: SharedFlow<UiEvent>,
    fileListUiEventFlow: SharedFlow<FileListUiEvent>
) {
    var isOrderDropdownExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }
    val pathListState = rememberLazyListState()

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val storagePermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        else
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    val rootPath by lazy {
        Environment.getExternalStorageDirectory().path
    }

    val requestStoragePermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        var areGranted = true
        permissionsMap.forEach { (permission, isGranted) ->
            areGranted = areGranted && isGranted
            onIntent(FileListIntent.PermissionResult(permission, isGranted))
        }

        if (areGranted) {
            onIntent(FileListIntent.InitRoot(rootPath))
        }
    }

    val requestManageAllFilesPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onIntent(FileListIntent.DismissPermissionDialog)

        if (isStoragePermissionGranted(context)) {
            onIntent(FileListIntent.InitRoot(rootPath))
        } else {
            onIntent(
                FileListIntent.PermissionResult(storagePermissions.first(), isGranted = false)
            )
        }
    }

    LaunchedEffect(key1 = true) {
        uiEventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEvent.ExitApp -> {
                    context.findActivity()?.run {
                        finish()
                        exitProcess(0)
                    }
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = true) {
        fileListUiEventFlow.collectLatest { event ->
            when (event) {
                is FileListUiEvent.FileClick -> {
                    val filePath = event.file.path ?: return@collectLatest
                    val file = File(filePath)
                    try {
                        file.openWithSomeApp(context)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Not found support apps", Toast.LENGTH_SHORT).show()
                    }
                }
                is FileListUiEvent.OpenFolderSuccess -> {
                    lifecycleOwner.lifecycleScope.launch {
                        delay(MIN_MILLIS_HUMAN_CAN_RECOGNIZE_60_HZ)
                        coroutineScope.launch {
                            pathListState.animateScrollToItem(
                                (event.pathSize - 1).coerceAtLeast(0)
                            )
                        }
                    }
                }
            }
        }
    }

    BackPressHandler {
        onIntent(FileListIntent.OnBackPressed)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {},
                modifier = Modifier.padding(top = 16.dp),
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = if (!state.isRoot && !state.isSearchExpanded) {
                    {
                        IconButton(
                            onClick = { onIntent(FileListIntent.OnBackPressed) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else null,
                actions = {
                    ExpandableSearchIcon(
                        modifier = Modifier.weight(1f),
                        isSearchExpanded = state.isSearchExpanded,
                        searchQuery = searchQuery,
                        onValueChange = { query ->
                            onIntent(FileListIntent.Search(query))
                        },
                        onToggleSearch = {
                            onIntent(FileListIntent.ToggleSearch)
                        },
                        lifecycleOwner = lifecycleOwner,
                        focusRequester = focusRequester,
                        keyboardController = keyboardController,
                        onLeadingIconClick = {
                            onIntent(FileListIntent.OnBackPressed)
                        }
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            DisposableEffect(key1 = lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        if (isStoragePermissionGranted(context)) {
                            onIntent(FileListIntent.InitRoot(rootPath))
                        } else {
                            onIntent(
                                FileListIntent.PermissionResult(
                                    storagePermissions.first(),
                                    false
                                )
                            )
                        }
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            FileListPath(
                modifier = Modifier,
                paths = state.paths,
                onClick = { path ->
                    onIntent(FileListIntent.ItemClick(FileFolder(path = path)))
                },
                state = pathListState
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")

                TextButton(
                    onClick = { isOrderDropdownExpanded = true },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                    )
                ) {
                    Text(text = state.order.label)

                    DropdownMenu(
                        expanded = isOrderDropdownExpanded,
                        onDismissRequest = { isOrderDropdownExpanded = false }
                    ) {
                        FileFolderOrder.labels.forEach { label ->
                            DropdownMenuItem(onClick = {
                                onIntent(
                                    FileListIntent.Order(
                                        when (label) {
                                            FileFolderOrder.NAME -> FileFolderOrder.Name(state.order.orderType)
                                            FileFolderOrder.DATE -> FileFolderOrder.Date(state.order.orderType)
                                            FileFolderOrder.TYPE -> FileFolderOrder.Type(state.order.orderType)
                                            FileFolderOrder.SIZE -> FileFolderOrder.Size(state.order.orderType)
                                            else -> return@DropdownMenuItem
                                        }
                                    )
                                )
                                isOrderDropdownExpanded = false
                            }) {
                                Row {
                                    Text(
                                        text = label,
                                        color = if (label == state.order.label) MaterialTheme.colors.primary
                                        else MaterialTheme.colors.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (label == state.order.label) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Check",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                IconButton(onClick = { onIntent(FileListIntent.ToggleAscDesc) }) {
                    Icon(
                        imageVector = state.order.orderType.getImageVector(),
                        contentDescription = "OrderType"
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f)) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.folder.children.size) { i ->
                        val fileFolder = state.folder.children[i]
                        FileFolderItem(
                            fileFolder = fileFolder,
                            modifier = Modifier
                                .clickable { onIntent(FileListIntent.ItemClick(fileFolder)) }
                        )
                    }
                }
            }
        }
    }

    state
        .permissionDialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        StoragePermissionTextProvider()
                    }
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                        ManageAllFilesPermissionTextProvider()
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = when (permission) {
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE -> false
                    else -> !(
                            context
                                .findActivity()
                                ?.shouldShowRequestPermissionRationale(permission)
                                ?: false
                            )
                },
                onDismiss = {
                    onIntent(FileListIntent.DismissPermissionDialog)
                    if (isStoragePermissionGranted(context)) {
                        onIntent(FileListIntent.InitRoot(rootPath))
                    } else {
                        onIntent(
                            FileListIntent.PermissionResult(
                                storagePermissions.first(),
                                isGranted = false
                            )
                        )
                    }
                },
                onOkClick = {
                    requestStoragePermission(
                        launcherAfterAndroidR = requestManageAllFilesPermissionLauncher,
                        launcherBeforeAndroidR = requestStoragePermissionsLauncher,
                    )
                },
                onGoToAppSettingsClick = {
                    context.openAppSettings()
                }
            )
        }
}

fun isStoragePermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun requestStoragePermission(
    launcherAfterAndroidR: ManagedActivityResultLauncher<Intent, ActivityResult>,
    launcherBeforeAndroidR: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
        launcherAfterAndroidR.launch(intent)
    } else {
        launcherBeforeAndroidR.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }
}