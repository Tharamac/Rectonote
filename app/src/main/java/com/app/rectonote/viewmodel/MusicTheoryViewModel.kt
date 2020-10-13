package com.app.rectonote.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class MusicTheoryViewModel(private val files: File) : ViewModel() {

    private external suspend fun startConvert(
        fs: Int,
        audioPath: String,
    ): IntArray

    private val cppOut: MutableLiveData<IntArray> by lazy {
        MutableLiveData<IntArray>().also {
            viewModelScope.launch {
                startConvert(44100, "${files}/voice16bit.pcm")
            }
        }
    }


}