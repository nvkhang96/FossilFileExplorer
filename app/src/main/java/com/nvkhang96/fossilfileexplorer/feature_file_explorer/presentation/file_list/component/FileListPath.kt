package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FileListPath(
    modifier: Modifier = Modifier,
    paths: List<Pair<String, String>>,
    onClick: (String) -> Unit,
    state: LazyListState,
) {
    LazyRow(
        modifier = modifier,
        state = state
    ) {
        items(paths.size) { i ->
            Text(
                text = "${if (i > 0) ">  " else ""}${paths[i].first}",
                modifier = Modifier
                    .clickable {
                        onClick(paths[i].second)
                    }
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                style = MaterialTheme.typography.button
            )
        }
    }
}