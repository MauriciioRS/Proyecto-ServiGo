package com.example.proyectofinal11.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectofinal11.TabInformacionFragment
import com.example.proyectofinal11.TabPortafolioFragment
import com.example.proyectofinal11.TabResenasFragment
import java.lang.IllegalStateException

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var profesionalUid: String? = null

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Si el UID aún no se ha asignado (por seguridad), devolvemos un fragmento vacío para evitar un crash.
        val uid = profesionalUid ?: return Fragment()

        return when (position) {
            0 -> TabInformacionFragment.newInstance(uid)
            1 -> TabPortafolioFragment.newInstance(uid)
            2 -> TabResenasFragment.newInstance(uid)
            else -> throw IllegalStateException("Posición de pestaña inválida: $position")
        }
    }
}
