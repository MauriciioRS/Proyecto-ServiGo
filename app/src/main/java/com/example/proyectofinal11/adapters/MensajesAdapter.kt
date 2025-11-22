package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.models.Mensaje

class MensajesAdapter(
    private var lista: MutableList<Mensaje>,
    private val onClick: (Mensaje) -> Unit = {}
) : RecyclerView.Adapter<MensajesAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgAvatar: ImageView = v.findViewById(R.id.imgAvatar)
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvHora: TextView = v.findViewById(R.id.tvHora)
        val tvMensaje: TextView = v.findViewById(R.id.tvMensaje)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val tvUnread: TextView = v.findViewById(R.id.tvUnreadCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.displayName
        holder.tvHora.text = item.hora
        holder.tvMensaje.text = item.ultimoMensaje
        holder.tvStatus.text = item.estado

        // Mostrar/u ocultar contador
        if (item.unreadCount > 0) {
            holder.tvUnread.visibility = View.VISIBLE
            holder.tvUnread.text = item.unreadCount.toString()
        } else {
            holder.tvUnread.visibility = View.GONE
        }

        // Avatar: por defecto usamos circle_avatar; si quieres cargar desde URL usa Coil/Glide
        holder.imgAvatar.setImageResource(R.drawable.circle_avatar)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(newList: List<Mensaje>) {
        lista.clear()
        lista.addAll(newList)
        notifyDataSetChanged()
    }
}
