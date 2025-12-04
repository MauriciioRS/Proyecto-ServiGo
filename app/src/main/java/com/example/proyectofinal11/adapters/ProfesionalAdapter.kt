package com.example.proyectofinal11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.R
import com.example.proyectofinal11.database.entities.ProfesionalEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ProfesionalAdapter(
    private var profesionales: List<ProfesionalEntity>,
    private val onFavoritoClick: (ProfesionalEntity) -> Unit = {},
    private val onVerPerfilClick: (ProfesionalEntity) -> Unit = {}
) : RecyclerView.Adapter<ProfesionalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.card_profesional)
        val avatar: ImageView = view.findViewById(R.id.img_avatar)
        val nombre: TextView = view.findViewById(R.id.tv_nombre)
        val oficio: TextView = view.findViewById(R.id.tv_oficio)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val calificacion: TextView = view.findViewById(R.id.tv_calificacion)
        val precio: TextView = view.findViewById(R.id.tv_precio)
        val ubicacion: TextView = view.findViewById(R.id.tv_ubicacion)
        val btnFavorito: ImageView = view.findViewById(R.id.btn_favorito)
        val btnVerPerfil: MaterialButton = view.findViewById(R.id.btn_ver_perfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesional, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profesional = profesionales[position]

        holder.nombre.text = profesional.nombre
        holder.oficio.text = profesional.oficio
        holder.ratingBar.rating = profesional.calificacion
        holder.calificacion.text = profesional.calificacionFormateada
        holder.precio.text = profesional.precioFormateado
        holder.ubicacion.text = "${profesional.distrito} - ${profesional.ubicacion}"

        // Avatar por defecto
        holder.avatar.setImageResource(R.drawable.circle_avatar)

        holder.btnFavorito.setOnClickListener {
            onFavoritoClick(profesional)
        }

        holder.btnVerPerfil.setOnClickListener {
            onVerPerfilClick(profesional)
        }
    }

    override fun getItemCount() = profesionales.size

    fun updateList(newList: List<ProfesionalEntity>) {
        profesionales = newList
        notifyDataSetChanged()
    }
}




