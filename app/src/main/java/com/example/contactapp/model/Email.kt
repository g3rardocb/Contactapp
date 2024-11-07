package com.example.contactapp.model

import com.google.gson.annotations.SerializedName



data class Email(
    val id: Int = 0,
    val email: String,
    @SerializedName("persona_id")
    val personaId: Int = 0,
    val label: String
)