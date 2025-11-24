package com.example.proyectofinal11

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabInformacionFragment()
            1 -> TabPortafolioFragment()
            2 -> TabResenasFragment()
            else -> TabInformacionFragment()
        }
    }
}
