package com.example.proyectofinal11.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal11.R
import com.example.proyectofinal11.data.local.entity.ResenaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResenaAdapter : ListAdapter<ResenaEntity, ResenaAdapter.ResenaViewHolder>(ResenaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenaViewHolder {
        // Asegúrate de tener un layout llamado 'item_resena.xml'
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resena, parent, false)
        return ResenaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResenaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ResenaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ⭐ Asegúrate de que estos IDs coincidan con tu 'item_resena.xml'
        private val clienteImagen: ImageView = itemView.findViewById(R.id.cliente_image)
        private val clienteNombre: TextView = itemView.findViewById(R.id.cliente_name)
        private val resenaRating: RatingBar = itemView.findViewById(R.id.resena_rating)
        private val resenaComentario: TextView = itemView.findViewById(R.id.resena_comment)
        private val resenaFecha: TextView = itemView.findViewById(R.id.resena_date)

        fun bind(resena: ResenaEntity) {
            clienteNombre.text = resena.clienteNombre
            resenaRating.rating = resena.calificacion.toFloat()
            resenaComentario.text = resena.comentario
            resenaFecha.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(resena.fecha))

            // Cargar la foto del cliente que dejó la reseña
            if (resena.clienteFotoUrl != null) {
                try {
                    val imageBytes = Base64.decode(resena.clienteFotoUrl, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Glide.with(itemView.context).load(decodedImage).circleCrop().into(clienteImagen)
                } catch (e: Exception) {
                    clienteImagen.setImageResource(R.drawable.ic_user)
                }
            } else {
                clienteImagen.setImageResource(R.drawable.ic_user)
            }
        }
    }
}

class ResenaDiffCallback : DiffUtil.ItemCallback<ResenaEntity>() {
    override fun areItemsTheSame(oldItem: ResenaEntity, newItem: ResenaEntity) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ResenaEntity, newItem: ResenaEntity) = oldItem == newItem
}
