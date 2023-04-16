package com.nvkhang96.fossilfileexplorer.feature_file_explorer.presentation.file_list.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.nvkhang96.fossilfileexplorer.core.presentation.util.MIN_MILLIS_HUMAN_CAN_RECOGNIZE_60_HZ
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpandableSearchIcon(
    modifier: Modifier = Modifier,
    isSearchExpanded: Boolean,
    searchQuery: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    onToggleSearch: () -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
    lifecycleOwner: LifecycleOwner,
    onLeadingIconClick: () -> Unit,
) {
    if (isSearchExpanded) {
        TextField(
            value = searchQuery,
            onValueChange = onValueChange,
            modifier = modifier
                .focusRequester(focusRequester ?: FocusRequester()),
            placeholder = {
                Text(text = "Search...")
            },
            leadingIcon = {
                IconButton(onClick = onLeadingIconClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = "Back"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onValueChange(searchQuery)
                    keyboardController?.hide()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                leadingIconColor = Color.Black
            )
        )
    } else {
        IconButton(onClick = {
            onToggleSearch()

            lifecycleOwner.lifecycleScope.launch {
                delay(MIN_MILLIS_HUMAN_CAN_RECOGNIZE_60_HZ)
                focusRequester?.requestFocus()
            }
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    }
}