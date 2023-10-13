package com.example.beevacuate


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Action_Rain_Activity  : AppCompatActivity() {
    private lateinit var tablayout : TabLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var adapter: RnAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_rain)

//        supportActionBar!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.white)))

        tablayout = findViewById(R.id.tabs2)
        viewpager = findViewById(R.id.tabContent)
        adapter = RnAdapter(supportFragmentManager, lifecycle)
        viewpager.adapter=adapter

        TabLayoutMediator(tablayout, viewpager) {
                tab,position ->
            when(position) {
                0-> {
                    tab.text="호우예보시"
                }
                1-> {
                    tab.text="호우특보중"
                }
                2-> {
                    tab.text="평상시"
                }
                3 -> {
                    tab.text ="호우특보후"
                }
            }
        }.attach()
    }

}