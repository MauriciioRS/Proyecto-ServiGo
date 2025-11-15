package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class PerfilFragment : Fragment() {

    private lateinit var logoutButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        
        logoutButton = view.findViewById(R.id.logout_button)
        
        // aca para poner estrellas 4.5
        val starRating = view.findViewById<android.widget.TextView>(R.id.star_rating)
        starRating?.text = "★★★★☆"
        
        // Botón cerrar sesión
        logoutButton.setOnClickListener {

            val intent = Intent(requireContext(), InicioSesionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        
        return view
    }
}
