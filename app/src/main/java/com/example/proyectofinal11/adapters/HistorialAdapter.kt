package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.toColorInt // Importante añadir este import
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.HistorialEntity

// 1. Cambiamos a ListAdapter para usar DiffUtil automáticamente
class HistorialAdapter : ListAdapter<HistorialEntity, HistorialAdapter.ViewHolder>(HistorialDiffCallback()) {

    // El ViewHolder no cambia
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titulo: TextView = view.findViewById(R.id.txtTitulo)
        private val usuario: TextView = view.findViewById(R.id.txtUsuario)
        private val fecha: TextView = view.findViewById(R.id.txtFecha)
        private val precio: TextView = view.findViewById(R.id.txtPrecio)
        private val estado: TextView = view.findViewById(R.id.txtEstado)
        private val btnDetalles: Button = view.findViewById(R.id.btnDetalles) // Dejamos esto para uso futuro

        fun bind(item: HistorialEntity) {
            titulo.text = item.caso
            usuario.text = item.usuario
            fecha.text = item.fecha
            precio.text = item.precio
            estado.text = item.estado

            // 2. Usamos .toColorInt() que es más moderno
            when (item.estado) {
                "Finalizado" -> {
                    estado.setBackgroundResource(R.drawable.bg_estado_finalizado)
                    estado.setTextColor("#22C55E".toColorInt())
                }
                "En proceso" -> { // Asegúrate que este String coincide con el de la base de datos
                    estado.setBackgroundResource(R.drawable.bg_estado_proceso)
                    estado.setTextColor("#F59E0B".toColorInt())
                }
                "Cancelado" -> {
                    estado.setBackgroundResource(R.drawable.bg_estado_cancelado)
                    estado.setTextColor("#EF4444".toColorInt())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 3. Ya no necesitamos getItemCount() ni actualizarLista(). ListAdapter lo hace por nosotros.
}

// 4. Esta clase calcula la diferencia entre la lista vieja y la nueva
class HistorialDiffCallback : DiffUtil.ItemCallback<HistorialEntity>() {
    override fun areItemsTheSame(oldItem: HistorialEntity, newItem: HistorialEntity): Boolean {
        // Los ítems son los mismos si tienen el mismo ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistorialEntity, newItem: HistorialEntity): Boolean {
        // El contenido es el mismo si el objeto entero no ha cambiado
        return oldItem == newItem
    }
}
