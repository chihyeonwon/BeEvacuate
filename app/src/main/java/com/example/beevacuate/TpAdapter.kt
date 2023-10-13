package com.example.beevacuate


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TpAdapter(fragmentManager: FragmentManager, lifecycle:Lifecycle) :FragmentStateAdapter
    (fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> typhoon_before()
            1 -> typhoon_usual()
            else -> typhoon_after()
        }
    }
}