package com.example.contactapp.api

import com.example.contactapp.model.Email
import com.example.contactapp.model.Persona
import com.example.contactapp.model.PersonaRequest
import com.example.contactapp.model.Telefono
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    // Obtener lista de personas
    @GET("personas")
    suspend fun getPersonas(@Query("q") search: String? = null): Response<List<Persona>>
    // Obtener una persona específica
    @GET("personas/{id}")
    suspend fun getPersona(@Path("id") id: Int): Response<Persona>

    // Crear una nueva persona
    @POST("personas")
    suspend fun createPersona(@Body persona: PersonaRequest): Response<Persona>

    // Actualizar una persona existente
    @PUT("personas/{id}")
    suspend fun updatePersona(@Path("id") id: Int, @Body persona: PersonaRequest): Response<Persona>

    // Eliminar una persona
    @DELETE("personas/{id}")
    suspend fun deletePersona(@Path("id") id: Int): Response<Void>

    // Agregar teléfono
    @POST("phones")
    suspend fun addTelefono(@Body telefono: Telefono): Response<Telefono>

    // Actualizar teléfono
    @PUT("phones/{id}")
    suspend fun updateTelefono(@Path("id") telefonoId: Int, @Body telefono: Telefono): Response<Telefono>

    // Eliminar teléfono
    @DELETE("phones/{id}")
    suspend fun deleteTelefono(@Path("id") telefonoId: Int): Response<Void>

    // Agregar email
    @POST("emails")
    suspend fun addEmail(@Body email: Email): Response<Email>

    // Actualizar email
    @PUT("emails/{id}")
    suspend fun updateEmail(@Path("id") emailId: Int, @Body email: Email): Response<Email>

    // Eliminar email
    @DELETE("emails/{id}")
    suspend fun deleteEmail(@Path("id") emailId: Int): Response<Void>
}