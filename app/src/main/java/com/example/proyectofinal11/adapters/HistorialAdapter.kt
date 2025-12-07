package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.HistorialEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistorialAdapter : ListAdapter<HistorialEntity, HistorialAdapter.ViewHolder>(HistorialDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titulo: TextView = view.findViewById(R.id.txtTitulo)
        private val usuario: TextView = view.findViewById(R.id.txtUsuario)
        private val fecha: TextView = view.findViewById(R.id.txtFecha)
        private val precio: TextView = view.findViewById(R.id.txtPrecio)
        private val estado: TextView = view.findViewById(R.id.txtEstado)
        private val btnDetalles: Button = view.findViewById(R.id.btnDetalles)

        fun bind(item: HistorialEntity) {
            titulo.text = item.caso
            usuario.text = item.usuario
            fecha.text = item.fecha
            precio.text = item.precio
            estado.text = item.estado

            // Configurar colores según estado
            when (item.estado) {
                "Finalizado" -> {
                    estado.setBackgroundResource(R.drawable.bg_estado_finalizado)
                    estado.setTextColor("#22C55E".toColorInt())
                }
                "En proceso" -> {
                    estado.setBackgroundResource(R.drawable.bg_estado_proceso)
                    estado.setTextColor("#F59E0B".toColorInt())
                }
                "Cancelado" -> {
                    estado.setBackgroundResource(R.drawable.bg_estado_cancelado)
                    estado.setTextColor("#EF4444".toColorInt())
                }
            }

            // Lógica del botón "Ver Detalles"
            btnDetalles.setOnClickListener {
                MaterialAlertDialogBuilder(itemView.context)
                    .setTitle("Detalles del Acuerdo")
                    .setMessage(
                        "📌 Trabajo: ${item.titulo}\n" +
                        "📝 Descripción: ${item.caso}\n" +
                        "💰 Monto Acordado: ${item.precio}\n" +
                        "📅 Fecha: ${item.fecha}\n" +
                        "🔄 Estado Actual: ${item.estado}"
                    )
                    .setPositiveButton("Entendido") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
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
}

class HistorialDiffCallback : DiffUtil.ItemCallback<HistorialEntity>() {
    override fun areItemsTheSame(oldItem: HistorialEntity, newItem: HistorialEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistorialEntity, newItem: HistorialEntity): Boolean {
        return oldItem == newItem
    }
}
