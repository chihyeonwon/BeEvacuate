package com.example.beevacuate


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CustomPagerAdapter(fragmentManager: FragmentManager, lifecycle:Lifecycle) :FragmentStateAdapter
    (fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> earthquake_usual()
            1 -> earthquake_before()
            else -> earthquake_after()
        }
    }
}