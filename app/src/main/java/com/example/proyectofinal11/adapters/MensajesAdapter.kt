package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView // <-- Importa ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.MensajeEntity

// Fíjate que el constructor SOLO pide la función onClick. No pide una lista.
class MensajesAdapter(
    private val onClick: (MensajeEntity) -> Unit
) : ListAdapter<MensajeEntity, MensajesAdapter.ViewHolder>(MensajeDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        private val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        private val tvHora: TextView = view.findViewById(R.id.tvHora)
        private val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        private val tvUnreadCount: TextView = view.findViewById(R.id.tvUnreadCount)
        private val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        fun bind(item: MensajeEntity) {
            val displayName = if (item.titulo.isNotBlank() && item.titulo != "Trabajo ${item.id}") {
                item.titulo
            } else {
                item.nombreProfesional // Este es el nombre del campo en MensajeEntity
            }

            tvNombre.text = "$displayName - ${item.oficioProfesional}"
            tvMensaje.text = item.ultimoMensaje
            tvHora.text = item.hora
            tvStatus.text = item.estado

            tvUnreadCount.isVisible = item.noLeidos > 0
            tvUnreadCount.text = item.noLeidos.toString()

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MensajeDiffCallback : DiffUtil.ItemCallback<MensajeEntity>() {
    override fun areItemsTheSame(oldItem: MensajeEntity, newItem: MensajeEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MensajeEntity, newItem: MensajeEntity): Boolean {
        return oldItem == newItem
    }
}
