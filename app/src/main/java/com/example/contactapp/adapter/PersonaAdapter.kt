package com.example.contactapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contactapp.R
import com.example.contactapp.databinding.ItemContactoBinding
import com.example.contactapp.model.Persona


class PersonaAdapter(

    private val onPersonaClick: (Persona) -> Unit,
    private val onEliminarClick: (Persona) -> Unit
) : ListAdapter<Persona, PersonaAdapter.PersonaViewHolder>(PersonaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val binding = ItemContactoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PersonaViewHolder(private val binding: ItemContactoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(persona: Persona) {
            binding.tvNombreApellido.text = "${persona.name} ${persona.lastName}"
            binding.tvEmpresa.text = persona.company ?: ""

            Glide.with(binding.imgPerfil.context)
                .load(persona.profilePicture)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgPerfil)

            binding.root.setOnClickListener { onPersonaClick(persona) }
            binding.btnEliminarContacto.setOnClickListener { onEliminarClick(persona) }
        }
    }

    class PersonaDiffCallback : DiffUtil.ItemCallback<Persona>() {
        override fun areItemsTheSame(oldItem: Persona, newItem: Persona): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Persona, newItem: Persona): Boolean = oldItem == newItem
    }
}