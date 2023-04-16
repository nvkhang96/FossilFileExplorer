package com.nvkhang96.fossilfileexplorer.core.presentation.util

sealed class UiEvent {
    data class ShowSnackbar(val message: String): UiEvent()
    object ExitApp: UiEvent()
}
