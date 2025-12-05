package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.ResenaEntity

class ResenaAdapter : ListAdapter<ResenaEntity, ResenaAdapter.ResenaViewHolder>(ResenaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenaViewHolder {
        val view = LayoutInflater.from(parent.context)
            // Reutilizaremos un layout similar al que tienes
            .inflate(R.layout.item_resena, parent, false)
        return ResenaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResenaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ResenaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val autorNombre: TextView = itemView.findViewById(R.id.author_name_text)
        private val comentario: TextView = itemView.findViewById(R.id.comment_text)
        private val autorFoto: ImageView = itemView.findViewById(R.id.author_avatar)

        fun bind(resena: ResenaEntity) {
            autorNombre.text = resena.autorNombre
            comentario.text = resena.comentario
            Glide.with(itemView.context)
                .load(resena.autorFotoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_user)
                .into(autorFoto)
        }
    }

    class ResenaDiffCallback : DiffUtil.ItemCallback<ResenaEntity>() {
        override fun areItemsTheSame(oldItem: ResenaEntity, newItem: ResenaEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ResenaEntity, newItem: ResenaEntity) = oldItem == newItem
    }
}
