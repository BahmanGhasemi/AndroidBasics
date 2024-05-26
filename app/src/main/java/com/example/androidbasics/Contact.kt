package com.example.androidbasics

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String? = null
)