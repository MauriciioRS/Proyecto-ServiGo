package com.example.proyectofinal11

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// CAMBIO: El adaptador ahora acepta el UID del usuario.
class ProfilePagerAdapter(activity: FragmentActivity, private val usuarioUid: String?) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Creamos un 'bundle' para pasar el UID a cada fragmento hijo.
        val bundle = Bundle().apply {
            putString("USUARIO_UID", usuarioUid)
        }

        val fragment = when (position) {
            0 -> TabInformacionFragment()
            1 -> TabPortafolioFragment()
            2 -> TabResenasFragment()
            else -> TabInformacionFragment()
        }

        // Adjuntamos el 'bundle' con el UID al fragmento antes de devolverlo.
        fragment.arguments = bundle
        return fragment
    }
}
