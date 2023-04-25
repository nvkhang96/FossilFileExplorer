package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nvkhang96.fossilfileexplorer.feature_file_explorer.domain.model.FileFolder
import com.nvkhang96.fossilfileexplorer.core.util.DateUtils

@Composable
fun FileFolderItem(
    fileFolder: FileFolder,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            fileFolder.isDirectory -> {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = "Folder",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colors.primary,
                )
            }
            fileFolder.isSupportedImage() -> {
                Image(
                    painter = rememberAsyncImagePainter(model = fileFolder.path),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = "File",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = fileFolder.name,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = DateUtils.formatReadableDate(fileFolder.lastModified),
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                )
                Text(
                    text = if (fileFolder.isDirectory) fileFolder.readableChildrenCount
                    else fileFolder.readableSize,
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.Gray,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
        }
    }
}