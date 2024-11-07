package com.example.contactapp.model

import com.google.gson.annotations.SerializedName

data class Persona(
    val id: Int = 0,
    val name: String,
    @SerializedName("last_name")
    val lastName: String,
    val company: String? = "",
    val address: String? = "",
    val city: String? = "",
    val state: String? = "",
    @SerializedName("profile_picture")
    val profilePicture: String? = "",
    val phones: List<Telefono> = emptyList(),
    val emails: List<Email> = emptyList()
)