package com.example.androidbasics

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

class CompressionViewModel : ViewModel() {
    var originalUri: Uri? by mutableStateOf(null)
        private set

    var bitmap: Bitmap? by mutableStateOf(null)
        private set

    var workerId: UUID? by mutableStateOf(null)
        private set

    fun updateUri(uri: Uri?) {
        this.originalUri = uri
    }

    fun updateBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    fun updateWorkerId(id: UUID?) {
        this.workerId = id
    }
}