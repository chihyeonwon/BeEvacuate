package com.example.beevacuate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ActionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_main)

        val earthquake_btn: ImageButton = findViewById(R.id.earthquake_btn) //버튼
        val rain_Btn:ImageButton = findViewById(R.id.rain_btn)
        val wave_btn:ImageButton = findViewById(R.id.wave_btn)
        val typhoon_btn:ImageButton = findViewById(R.id.typhoon_btn)

        earthquake_btn.setOnClickListener {
            startActivity(Intent(this@ActionActivity, Action_Earthquake_Activity::class.java))
        }

        rain_Btn.setOnClickListener {
            startActivity(Intent(this@ActionActivity, Action_Rain_Activity::class.java))
        }

        wave_btn.setOnClickListener {
            startActivity(Intent(this@ActionActivity, Action_Wave_Activity::class.java))
        }

        typhoon_btn.setOnClickListener {
            startActivity(Intent(this@ActionActivity, Action_Typhoon_Activity::class.java))
        }
    }
}
