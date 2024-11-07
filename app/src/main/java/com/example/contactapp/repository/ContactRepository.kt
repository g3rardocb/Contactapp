package com.example.contactapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contactapp.api.ApiService
import com.example.contactapp.model.Email
import com.example.contactapp.model.Persona
import com.example.contactapp.model.PersonaRequest
import com.example.contactapp.model.Telefono
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ContactRepository(private val apiService: ApiService) {

    private val _personas = MutableLiveData<List<Persona>>()
    val personas: LiveData<List<Persona>> get() = _personas

    private val _persona = MutableLiveData<Persona>()
    val persona: LiveData<Persona> get() = _persona

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    suspend fun fetchPersonas(search: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPersonas(search)
                Log.d("ContactRepository", "Respuesta de getPersonas: ${response.code()}")
                if (response.isSuccessful) {
                    val personas = response.body()
                    Log.d("ContactRepository", "Personas obtenidas: $personas")
                    _personas.postValue(personas!!)
                } else {
                    _errorMessage.postValue("Error al obtener personas: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ContactRepository", "Exception en fetchPersonas: ${e.localizedMessage}")
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun fetchPersona(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPersona(id)
                if (response.isSuccessful) {
                    _persona.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error al obtener persona: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun savePersona(persona: Persona) {
        withContext(Dispatchers.IO) {
            try {
                val personaRequest = PersonaRequest(
                    name = persona.name,
                    lastName = persona.lastName,
                    company = persona.company,
                    address = persona.address,
                    city = persona.city,
                    state = persona.state,
                    profilePicture = persona.profilePicture
                )
                val response = if (persona.id == 0) {
                    apiService.createPersona(personaRequest)
                } else {
                    apiService.updatePersona(persona.id, personaRequest)
                }
                Log.d("ContactRepository", "savePersona response code: ${response.code()}")
                if (response.isSuccessful) {
                    val savedPersona = response.body()
                    Log.d("ContactRepository", "savePersona savedPersona: $savedPersona")
                    savedPersona?.let { personaSaved ->
                        // Agregar teléfonos
                        persona.phones.forEach { telefono ->
                            val telefonoToAdd = telefono.copy(personaId = personaSaved.id)
                            Log.d("ContactRepository", "Adding telefono: $telefonoToAdd")
                            val telefonoResponse = apiService.addTelefono(telefonoToAdd)
                            Log.d("ContactRepository", "addTelefono response code: ${telefonoResponse.code()}")
                            if (!telefonoResponse.isSuccessful) {
                                _errorMessage.postValue("Error al agregar teléfono: ${telefonoResponse.code()}")
                            }
                        }
                        // Agregar emails
                        persona.emails.forEach { email ->
                            val emailToAdd = email.copy(personaId = personaSaved.id)
                            Log.d("ContactRepository", "Adding email: $emailToAdd")
                            val emailResponse = apiService.addEmail(emailToAdd)
                            Log.d("ContactRepository", "addEmail response code: ${emailResponse.code()}")
                            if (!emailResponse.isSuccessful) {
                                _errorMessage.postValue("Error al agregar email: ${emailResponse.code()}")
                            }
                        }
                        fetchPersonas()
                    }
                } else {
                    _errorMessage.postValue("Error al guardar persona: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ContactRepository", "Exception in savePersona: ${e.localizedMessage}")
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun deletePersona(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.deletePersona(id)
                if (response.isSuccessful) {
                    fetchPersonas()
                } else {
                    _errorMessage.postValue("Error al eliminar persona: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    // Opcional: Funciones para manejar teléfonos y emails individualmente
    suspend fun addTelefono(telefono: Telefono) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.addTelefono(telefono)
                if (!response.isSuccessful) {
                    _errorMessage.postValue("Error al agregar teléfono: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun addEmail(email: Email) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.addEmail(email)
                if (!response.isSuccessful) {
                    _errorMessage.postValue("Error al agregar email: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    // Implementar funciones para actualizar y eliminar teléfonos y emails si es necesario
}