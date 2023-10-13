package com.example.beevacuate


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class RnAdapter(fragmentManager: FragmentManager, lifecycle:Lifecycle) :FragmentStateAdapter
    (fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> rain_before()
            1 -> rain_usual()
            2 -> rain_ready()
            else -> rain_after()
        }
    }
}