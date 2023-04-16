package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.util

sealed class FileFolderOrder(val orderType: OrderType) {
    class Name(orderType: OrderType): FileFolderOrder(orderType)
    class Date(orderType: OrderType): FileFolderOrder(orderType)
    class Type(orderType: OrderType): FileFolderOrder(orderType)
    class Size(orderType: OrderType): FileFolderOrder(orderType)

    fun copy(orderType: OrderType): FileFolderOrder {
        return when (this) {
            is Name -> Name(orderType)
            is Date -> Date(orderType)
            is Type -> Type(orderType)
            is Size -> Size(orderType)
        }
    }

    val label: String
        get() {
            return when (this) {
                is Name -> NAME
                is Date -> DATE
                is Type -> TYPE
                is Size -> SIZE
            }
        }

    companion object {
        const val NAME = "Name"
        const val DATE = "Date"
        const val TYPE = "Type"
        const val SIZE = "Size"

        val labels: List<String>
            get() {
                return listOf(NAME, DATE, TYPE, SIZE)
            }
    }
}
