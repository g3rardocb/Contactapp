package com.example.contactapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.api.RetrofitClient
import com.example.contactapp.adapter.PersonaAdapter
import com.example.contactapp.databinding.ActivityMainBinding
import com.example.contactapp.model.Persona
import com.example.contactapp.repository.ContactRepository
import com.example.contactapp.viewmodel.ContactosViewModel
import com.example.contactapp.viewmodel.ContactosViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contactosViewModel: ContactosViewModel by viewModels {
        ContactosViewModelFactory(ContactRepository(RetrofitClient.apiService))
    }
    private lateinit var personaAdapter: PersonaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarObservadores()
        configurarBusqueda()
        configurarBotones()

        contactosViewModel.getPersonas()
    }

    private fun configurarRecyclerView() {
        personaAdapter = PersonaAdapter(
            onPersonaClick = { persona ->
                val intent = Intent(this, DetalleContactoActivity::class.java).apply {
                    putExtra("persona_id", persona.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { persona ->
                mostrarDialogoEliminar(persona)
            }
        )
        binding.rvContactos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = personaAdapter
        }
    }

    private fun mostrarDialogoEliminar(persona: Persona) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que deseas eliminar a ${persona.name} ${persona.lastName}?")
            .setPositiveButton("Sí") { _, _ ->
                contactosViewModel.deletePersona(persona.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun configurarObservadores() {
        contactosViewModel.personas.observe(this, Observer { personas ->
            Log.d("MainActivity", "Personas observadas: $personas")
            personaAdapter.submitList(personas)
        })

        contactosViewModel.errorMessage.observe(this, Observer { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        })
    }

    private fun configurarBusqueda() {
        binding.etBuscar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                // Filtrar la lista de personas según el texto ingresado
                val textoBusqueda = s.toString().trim()
                if (textoBusqueda.isNotEmpty()) {
                    val listaFiltrada = contactosViewModel.personas.value?.filter { persona ->
                        persona.name.lowercase().contains(textoBusqueda) ||
                                persona.lastName.lowercase().contains(textoBusqueda) ||
                                persona.company?.lowercase()?.contains(textoBusqueda) == true
                    } ?: emptyList()
                    personaAdapter.submitList(listaFiltrada)
                } else {
                    // Si no hay texto, mostrar la lista completa
                    personaAdapter.submitList(contactosViewModel.personas.value ?: emptyList())
                }

            Log.d("MainActivity", "Texto de búsqueda: $textoBusqueda")
            contactosViewModel.getPersonas(textoBusqueda)
        }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun configurarBotones() {
        binding.btnAgregarContacto.setOnClickListener {
            startActivity(Intent(this, DetalleContactoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        contactosViewModel.getPersonas()
    }
}