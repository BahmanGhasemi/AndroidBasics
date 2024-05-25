package com.example.androidbasics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    var images by mutableStateOf(emptyList<Image>())
        private set

    fun updateList(images: List<Image>) {
        this.images = images
    }
}