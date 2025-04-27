package com.seon_schedule

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun saveInt(context: Context, key: String, value: Int) {
    val sharedPref = context.getSharedPreferences("seonSchedule", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt(key, value)
        apply()
    }
}

fun loadInt(context: Context, key: String, defaultValue: Int = 1): Int {
    val sharedPref = context.getSharedPreferences("seonSchedule", Context.MODE_PRIVATE)
    return sharedPref.getInt(key, defaultValue)
}
