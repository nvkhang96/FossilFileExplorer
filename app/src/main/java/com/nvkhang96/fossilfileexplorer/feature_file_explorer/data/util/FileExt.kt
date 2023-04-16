package com.nvkhang96.fossilfileexplorer.feature_file_explorer.data.util

import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import java.io.File

fun File.toFileFolder(): FileFolder {
    return FileFolder(
        name = this.name,
        path = this.path,
        isDirectory = this.isDirectory,
        childrenCount = this.listFiles()?.size,
        lastModified = this.lastModified(),
        extension = this.extension,
        size = this.length()
    )
}

fun File.openWithSomeApp(context: Context) {
    val myMime: MimeTypeMap = MimeTypeMap.getSingleton()
    val newIntent = Intent(Intent.ACTION_VIEW)
    val mimeType: String =
        myMime.getMimeTypeFromExtension(extension).toString()

    newIntent.setDataAndType(
        FileProvider.getUriForFile(
            context.applicationContext,
            context.applicationContext.packageName.toString() + ".fileprovider",
            this
        ), mimeType
    )

    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(newIntent)
}