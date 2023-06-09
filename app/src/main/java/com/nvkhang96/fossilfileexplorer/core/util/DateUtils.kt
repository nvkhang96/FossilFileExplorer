package com.nvkhang96.fossilfileexplorer.core.util

import android.text.format.DateFormat
import java.util.*

object DateUtils {
    fun formatReadableDate(millis: Long): String {
        return DateFormat
            .format("MMM dd, yyyy hh:mm", Date(millis))
            .toString()
    }
}