package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}
