package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.PortafolioEntity

class PortafolioAdapter : ListAdapter<PortafolioEntity, PortafolioAdapter.PortafolioViewHolder>(PortafolioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortafolioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_portafolio_foto, parent, false)
        return PortafolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortafolioViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class PortafolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.portfolio_image)

        fun bind(item: PortafolioEntity) {
            Glide.with(itemView.context)
                .load(item.imageUrl) // Cargamos la URL de la imagen
                .placeholder(R.drawable.ic_profile_placeholder) // Un placeholder mientras carga
                .error(R.drawable.ic_user) // Una imagen de error si falla
                .into(imageView)
        }
    }

    class PortafolioDiffCallback : DiffUtil.ItemCallback<PortafolioEntity>() {
        override fun areItemsTheSame(oldItem: PortafolioEntity, newItem: PortafolioEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PortafolioEntity, newItem: PortafolioEntity): Boolean {
            return oldItem == newItem
        }
    }
}
