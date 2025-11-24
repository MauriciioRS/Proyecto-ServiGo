package com.example.proyectofinal11.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal11.R
// CORRECCIÓN 1: Se corrigió el error tipográfico en 'example'.
import com.example.proyectofinal11.data.local.entity.ProfesionalEntity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

// Definimos los tipos de clics que el adaptador puede manejar
data class ProfesionalClickListeners(
    val onFavoritoClick: (ProfesionalEntity) -> Unit,
    val onVerPerfilClick: (ProfesionalEntity) -> Unit
)

class ProfesionalAdapter(
    private val listeners: ProfesionalClickListeners
) : ListAdapter<ProfesionalEntity, ProfesionalAdapter.ProfesionalViewHolder>(ProfesionalDiffCallback()) {

    inner class ProfesionalViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        // CORRECCIÓN 2: Se eliminó la línea duplicada que causaba un error.
        private val imagenFondo: ImageView = view.findViewById(R.id.imagen_fondo)
        private val circleAvatar: ImageView = view.findViewById(R.id.circle_avatar)
        private val nombreProfesional: TextView = view.findViewById(R.id.nombre_profesional)
        private val oficioProfesional: TextView = view.findViewById(R.id.oficio_profesional)
        private val favoritoIcon: ImageView = view.findViewById(R.id.favorito_icon)
        private val btnVerPerfil: ExtendedFloatingActionButton = view.findViewById(R.id.btn_ver_perfil)

        fun bind(profesional: ProfesionalEntity) {
            nombreProfesional.text = profesional.nombre
            oficioProfesional.text = profesional.oficio

            // --- Carga de Imágenes con Glide ---
            Glide.with(itemView.context)
                .load(profesional.imagenFondoUrl)
                .placeholder(R.drawable.gradient_scrim)
                .into(imagenFondo)

            Glide.with(itemView.context)
                .load(profesional.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(circleAvatar)

            // --- Lógica para el icono de Favorito (sigue aquí y es correcta) ---
            if (profesional.esFavorito) {
                favoritoIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_heart_filled))
            } else {
                favoritoIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_heart_outline))
            }

            // --- Asignación de los Clics (sigue aquí y es correcta) ---
            favoritoIcon.setOnClickListener {
                listeners.onFavoritoClick(profesional)
            }
            btnVerPerfil.setOnClickListener {
                listeners.onVerPerfilClick(profesional)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesionalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesional_card, parent, false) as ViewGroup
        return ProfesionalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesionalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ProfesionalDiffCallback : DiffUtil.ItemCallback<ProfesionalEntity>() {
    override fun areItemsTheSame(oldItem: ProfesionalEntity, newItem: ProfesionalEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProfesionalEntity, newItem: ProfesionalEntity): Boolean {
        return oldItem == newItem
    }
}
