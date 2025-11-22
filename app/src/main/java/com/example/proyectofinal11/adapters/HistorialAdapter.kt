package com.example.proyectofinal11.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.models.Historial

class HistorialAdapter (

    private val lista: List<Historial>
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo = view.findViewById<TextView>(R.id.txtTitulo)
        val usuario = view.findViewById<TextView>(R.id.txtUsuario)
        val fecha = view.findViewById<TextView>(R.id.txtFecha)
        val precio = view.findViewById<TextView>(R.id.txtPrecio)
        val estado = view.findViewById<TextView>(R.id.txtEstado)
        val btnDetalles = view.findViewById<Button>(R.id.btnDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.titulo.text = item.titulo
        holder.usuario.text = item.usuario
        holder.fecha.text = item.fecha
        holder.precio.text = item.precio
        holder.estado.text = item.estado

        when (item.estado) {
            "Finalizado" -> {
                holder.estado.setBackgroundResource(R.drawable.bg_estado_finalizado)
                holder.estado.setTextColor(Color.parseColor("#22C55E"))
            }
            "En Proceso" -> {
                holder.estado.setBackgroundResource(R.drawable.bg_estado_proceso)
                holder.estado.setTextColor(Color.parseColor("#F59E0B"))
            }
            "Cancelado" -> {
                holder.estado.setBackgroundResource(R.drawable.bg_estado_cancelado)
                holder.estado.setTextColor(Color.parseColor("#EF4444"))
            }
        }
    }

    override fun getItemCount() = lista.size
}