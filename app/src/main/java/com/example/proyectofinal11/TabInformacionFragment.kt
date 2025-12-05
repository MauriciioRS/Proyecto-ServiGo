package com.example.proyectofinal11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TabInformacionFragment : Fragment() {

    private lateinit var db: ServiGoDatabase
    private var usuarioUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())
        // Recibimos el UID que ProfilePagerAdapter nos pasa
        arguments?.let {
            usuarioUid = it.getString("USUARIO_UID")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tab_informacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (usuarioUid != null) {
            cargarInformacionUsuario(view)
        }
    }

    private fun cargarInformacionUsuario(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Buscamos al usuario en la base de datos
            val usuario = db.usuarioDao().obtenerUsuarioPorFirebaseUid(usuarioUid!!)

            // Volvemos al hilo principal para actualizar la UI
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    val aboutMeText = view.findViewById<TextView>(R.id.aboutMeText)
                    val experienceText = view.findViewById<TextView>(R.id.experienceText)
                    val zonesText = view.findViewById<TextView>(R.id.zonesText)

                    // Llenamos las vistas con los datos REALES del usuario
                    aboutMeText.text = "Profesional del rubro de ${usuario.oficio ?: "servicios"} con dedicación y compromiso."
                    experienceText.text = "Experiencia: 5 años" // Puedes añadir este campo a UsuarioEntity
                    zonesText.text = "Zonas: ${usuario.distrito ?: "Varias"}"
                }
            }
        }
    }
}
