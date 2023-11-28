package com.nmwilkinson.workmanager

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private val strings = MutableStateFlow(listOf<String>())
    val stringsFlow = strings.asStateFlow()

    fun addString(string: String) {
        Log.d("WRK", "addString $string")
        strings.update {
            val mutableList = it.toMutableList()
            mutableList.add(string)
            mutableList
        }
    }
}