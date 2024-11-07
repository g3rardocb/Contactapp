package com.example.contactapp.model

import com.google.gson.annotations.SerializedName


data class Telefono(
    val id: Int = 0,
    val number: String,
    @SerializedName("persona_id")
    val personaId: Int = 0,
    val label: String
)
