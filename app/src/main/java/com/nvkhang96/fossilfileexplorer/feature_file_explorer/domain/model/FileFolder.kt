package com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model

import kotlin.math.roundToInt

data class FileFolder(
    val name: String = "",
    val path: String? = null,
    val parentPath: String? = null,
    val children: List<FileFolder> = emptyList(),

    val lastModified: Long = 0,
    val isDirectory: Boolean = true,
    val size: Long = 0,
    val childrenCount: Int? = null,
    val extension: String = "",
) {

    val readableChildrenCount: String
        get() {
            val count = childrenCount ?: 0
            return "$count item${if (count == 1) "" else "s"}"
        }

    val readableSize: String
        get() {
            val units = listOf("Bytes", "KB", "MB", "GB", "TB")
            var count = 0
            var convertedSize = size.toDouble()

            while (convertedSize > 1024) {
                convertedSize /= 1024
                count++
            }

            val roundSize = (convertedSize * 100.0).roundToInt() / 100.0

            return "$roundSize ${units[count]}"
        }

    fun isSupportedImage(): Boolean {
        val supportedExtensions = listOf("bmp", "jpg", "jpeg", "png")
        return supportedExtensions.contains(extension.lowercase())
    }
}