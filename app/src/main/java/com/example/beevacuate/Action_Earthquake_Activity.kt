package com.example.beevacuate

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Action_Earthquake_Activity  : AppCompatActivity() {
    private lateinit var tablayout : TabLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var adapter: CustomPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_earthquake)

//        supportActionBar!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.white)))

        tablayout = findViewById(R.id.tabs)
        viewpager = findViewById(R.id.tabContent)
        adapter = CustomPagerAdapter(supportFragmentManager, lifecycle)
        viewpager.adapter=adapter

        TabLayoutMediator(tablayout, viewpager) {
            tab,position ->
            when(position) {
                0-> {
                    tab.text="평상시"
                }
                1-> {
                    tab.text="지진발생전"
                }
                2-> {
                    tab.text="지진발생후"
                }
            }
        }.attach()
    }

}