// CÓDIGO FINAL Y COMPLETO PARA ProfileFragment.kt
package com.example.proyectofinal11

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.example.proyectofinal11.ui.chat.ChatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var db: ServiGoDatabase
    private var usuarioUid: String? = null
    private var usuarioDelPerfil: UsuarioEntity? = null

    // ⭐⭐ AÑADIDO: Bloque Companion Object con la función newInstance ⭐⭐
    companion object {
        private const val ARG_USUARIO_UID = "USUARIO_UID"

        fun newInstance(usuarioUid: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USUARIO_UID, usuarioUid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())
        arguments?.let {
            // Usamos la constante para evitar errores de tipeo
            usuarioUid = it.getString(ARG_USUARIO_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (usuarioUid != null) {
            cargarDatosDelUsuario(view)
        } else {
            Toast.makeText(requireContext(), "Error: No se pudo encontrar al usuario.", Toast.LENGTH_SHORT).show()
        }
        setupTabs(view)
    }

    private fun cargarDatosDelUsuario(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuario = db.usuarioDao().obtenerUsuarioPorFirebaseUid(usuarioUid!!)
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    usuarioDelPerfil = usuario
                    mostrarDatosEnUI(view, usuario)
                    setupButtons(view)
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupButtons(view: View) {
        val contactarButton = view.findViewById<MaterialButton>(R.id.btnContactar)
        contactarButton.setOnClickListener {
            val profesional = usuarioDelPerfil
            if (profesional == null) {
                Toast.makeText(requireContext(), "Error, no se puede contactar al usuario.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("RECEPTOR_UID", profesional.firebaseUid)
                putExtra("RECEPTOR_NOMBRE", "${profesional.nombre} ${profesional.apellido}")
                putExtra("CHAT_TITLE", "Trabajo de ${profesional.oficio}")
            }
            startActivity(intent)
        }
    }

    private fun mostrarDatosEnUI(view: View, usuario: UsuarioEntity) {
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val userName = view.findViewById<TextView>(R.id.userName)
        val userProfession = view.findViewById<TextView>(R.id.userProfession)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val ratingText = view.findViewById<TextView>(R.id.ratingText)

        userName.text = "${usuario.nombre} ${usuario.apellido}"
        userProfession.text = usuario.oficio ?: "No especificado"
        ratingBar.rating = (usuario.rating ?: 0.0).toFloat()
        ratingText.text = String.format("%.1f", usuario.rating ?: 0.0)

        if (usuario.fotoPerfilBase64 != null) {
            try {
                val imageBytes = Base64.decode(usuario.fotoPerfilBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(this).load(decodedImage).circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder).error(R.drawable.ic_user)
                    .into(profileImage)
            } catch (e: Exception) {
                profileImage.setImageResource(R.drawable.ic_user)
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_user)
        }
    }

    private fun setupTabs(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.tabContent)
        val pagerAdapter = ProfilePagerAdapter(requireActivity(), usuarioUid)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Información"
                1 -> "Portafolio"
                2 -> "Reseña"
                else -> ""
            }
        }.attach()
    }
}
