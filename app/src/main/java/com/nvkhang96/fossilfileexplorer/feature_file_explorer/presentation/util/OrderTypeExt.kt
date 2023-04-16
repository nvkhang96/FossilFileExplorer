package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.ui.graphics.vector.ImageVector
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util.OrderType

fun OrderType.getImageVector(): ImageVector {
    return if (this is OrderType.Ascending) Icons.Default.ArrowUpward
    else Icons.Default.ArrowDownward
}