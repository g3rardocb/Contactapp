package com.example.contactapp.activities

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.contactapp.R
import com.example.contactapp.api.RetrofitClient
import com.example.contactapp.databinding.ActivityDetalleContactoBinding
import com.example.contactapp.model.Email
import com.example.contactapp.model.Persona
import com.example.contactapp.model.Telefono
import com.example.contactapp.repository.ContactRepository
import com.example.contactapp.viewmodel.ContactosViewModel
import com.example.contactapp.viewmodel.ContactosViewModelFactory


class DetalleContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleContactoBinding
    private val contactosViewModel: ContactosViewModel by viewModels {
        ContactosViewModelFactory(ContactRepository(RetrofitClient.apiService))
    }
    private var personaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personaId = intent.getIntExtra("persona_id", -1).takeIf { it != -1 }

        personaId?.let { cargarPersona(it) }

        binding.btnAgregarTelefono.setOnClickListener { agregarCampoTelefono() }
        binding.btnAgregarEmail.setOnClickListener { agregarCampoEmail() }
        binding.btnGuardarContacto.setOnClickListener { guardarPersona() }

        contactosViewModel.errorMessage.observe(this, Observer { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        })
    }

    private fun cargarPersona(id: Int) {
        contactosViewModel.getPersona(id)
        contactosViewModel.persona.observe(this, Observer { persona ->
            persona?.let {
                binding.apply {
                    etNombre.setText(it.name)
                    etApellido.setText(it.lastName)
                    etEmpresa.setText(it.company)
                    etDireccion.setText(it.address)
                    etCiudad.setText(it.city)
                    etEstado.setText(it.state)
                    etFotoPerfil.setText(it.profilePicture)

                    it.phones.forEach { telefono -> agregarCampoTelefono(telefono) }
                    it.emails.forEach { email -> agregarCampoEmail(email) }
                }
            }
        })
    }

    private fun agregarCampoTelefono(telefono: Telefono? = null) {
        val itemTelefono = layoutInflater.inflate(R.layout.item_telefono_email, binding.listaTelefonos, false)
        val etValor = itemTelefono.findViewById<EditText>(R.id.etValor)
        val spinnerEtiqueta = itemTelefono.findViewById<Spinner>(R.id.spinnerEtiqueta)
        val btnEliminar = itemTelefono.findViewById<ImageButton>(R.id.btnEliminar)

        configurarSpinner(spinnerEtiqueta, telefono?.label)
        etValor.setText(telefono?.number)

        btnEliminar.setOnClickListener {
            binding.listaTelefonos.removeView(itemTelefono)
        }

        binding.listaTelefonos.addView(itemTelefono)
    }

    private fun agregarCampoEmail(email: Email? = null) {
        val itemEmail = layoutInflater.inflate(R.layout.item_telefono_email, binding.listaEmails, false)
        val etValor = itemEmail.findViewById<EditText>(R.id.etValor)
        val spinnerEtiqueta = itemEmail.findViewById<Spinner>(R.id.spinnerEtiqueta)
        val btnEliminar = itemEmail.findViewById<ImageButton>(R.id.btnEliminar)

        configurarSpinner(spinnerEtiqueta, email?.label)
        etValor.setText(email?.email)

        btnEliminar.setOnClickListener {
            binding.listaEmails.removeView(itemEmail)
        }

        binding.listaEmails.addView(itemEmail)
    }

    private fun configurarSpinner(spinner: Spinner, etiquetaSeleccionada: String?) {
        val etiquetasArray = resources.getStringArray(R.array.etiquetas).toMutableList()
        etiquetaSeleccionada?.let {
            if (!etiquetasArray.contains(it)) etiquetasArray.add(it)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetasArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        etiquetaSeleccionada?.let {
            val position = etiquetasArray.indexOf(it)
            if (position >= 0) spinner.setSelection(position)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (etiquetasArray[position] == "Personalizado") {
                    mostrarDialogoEtiquetaPersonalizada(spinner, etiquetasArray, position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mostrarDialogoEtiquetaPersonalizada(spinner: Spinner, etiquetasArray: MutableList<String>, position: Int) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Etiqueta personalizada")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val etiquetaPersonalizada = input.text.toString().trim()
                if (etiquetaPersonalizada.isNotEmpty()) {
                    etiquetasArray[position] = etiquetaPersonalizada
                    (spinner.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                    spinner.setSelection(position)
                } else {
                    spinner.setSelection(0)
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                spinner.setSelection(0)
            }
            .show()
    }

    private fun guardarPersona() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val empresa = binding.etEmpresa.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val ciudad = binding.etCiudad.text.toString().trim()
        val estado = binding.etEstado.text.toString().trim()
        val fotoPerfil = binding.etFotoPerfil.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val telefonos = obtenerTelefonos()
        val emails = obtenerEmails()

        Log.d("DetalleContacto", "Telefonos: $telefonos")
        Log.d("DetalleContacto", "Emails: $emails")

        val persona = Persona(
            id = personaId ?: 0,
            name = nombre,
            lastName = apellido,
            company = empresa,
            address = direccion,
            city = ciudad,
            state = estado,
            profilePicture = fotoPerfil,
            phones = telefonos,
            emails = emails
        )

        contactosViewModel.savePersona(persona)
        finish()
    }

    private fun obtenerTelefonos(): List<Telefono> {
        val listaTelefonos = mutableListOf<Telefono>()
        for (i in 0 until binding.listaTelefonos.childCount) {
            val view = binding.listaTelefonos.getChildAt(i)
            val etValor = view.findViewById<EditText>(R.id.etValor).text.toString().trim()
            val etiqueta = view.findViewById<Spinner>(R.id.spinnerEtiqueta).selectedItem.toString()
            if (etValor.isNotEmpty()) {
                listaTelefonos.add(Telefono(number = etValor, label = etiqueta))
            }
        }
        return listaTelefonos
    }

    private fun obtenerEmails(): List<Email> {
        val listaEmails = mutableListOf<Email>()
        for (i in 0 until binding.listaEmails.childCount) {
            val view = binding.listaEmails.getChildAt(i)
            val etValor = view.findViewById<EditText>(R.id.etValor).text.toString().trim()
            val etiqueta = view.findViewById<Spinner>(R.id.spinnerEtiqueta).selectedItem.toString()
            if (etValor.isNotEmpty()) {
                listaEmails.add(Email(email = etValor, label = etiqueta))
            }
        }
        return listaEmails
    }
}