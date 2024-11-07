package com.example.contactapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.contactapp.model.Persona
import com.example.contactapp.repository.ContactRepository
import kotlinx.coroutines.launch

class ContactosViewModel(private val repository: ContactRepository) : ViewModel() {

    val personas: LiveData<List<Persona>> = repository.personas
    val persona: LiveData<Persona> = repository.persona
    val errorMessage: LiveData<String> = repository.errorMessage

    fun getPersonas(search: String? = null) {
        Log.d("ContactosViewModel", "Buscar personas con: $search")
        viewModelScope.launch {
            repository.fetchPersonas(search)
        }
    }

    fun getPersona(id: Int) {
        viewModelScope.launch {
            repository.fetchPersona(id)
        }
    }

    fun savePersona(persona: Persona) {
        viewModelScope.launch {
            repository.savePersona(persona)
        }
    }

    fun deletePersona(id: Int) {
        viewModelScope.launch {
            repository.deletePersona(id)
        }
    }
}

class ContactosViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}