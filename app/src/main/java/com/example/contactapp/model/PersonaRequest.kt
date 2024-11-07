package com.example.contactapp.model

import com.google.gson.annotations.SerializedName

//separar la lógica de solicitud de creación/actualización de la inclusión de teléfonos y correos electrónicos
data class PersonaRequest(
    val name: String,
    @SerializedName("last_name")
    val lastName: String,
    val company: String? = "",
    val address: String? = "",
    val city: String? = "",
    val state: String? = "",
    @SerializedName("profile_picture")
    val profilePicture: String? = ""
)