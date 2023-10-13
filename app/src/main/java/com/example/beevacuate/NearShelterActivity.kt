package com.example.beevacuate

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.round
import kotlin.properties.Delegates


class NearShelterActivity : AppCompatActivity() {

    private lateinit var Back_Button : Button
    private lateinit var Route_Button : Button

    private var width by Delegates.notNull<Int>()
    private var height by Delegates.notNull<Int>()

    private lateinit var latlng : String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_shelter)

        Back_Button = findViewById(R.id.map_back_button)
        Route_Button = findViewById(R.id.map_route_button)

        Back_Button.setOnClickListener(){
            finish()
        }


        Route_Button.setOnClickListener(){
            val intent = Intent(this, RouteActivity::class.java)

            intent.putExtra("LatLng", latlng)
            Log.i("blog", latlng)
            startActivity(intent)


        }
    }

    override fun onResume() {
        super.onResume()

        var getMapInfoName = findViewById<TextView>(R.id.map_info_name)
        var getMapInfoAddress = findViewById<TextView>(R.id.map_info_addr)
        var getMapInfoArea = findViewById<TextView>(R.id.map_info_area)
        var getMapInfoDistance = findViewById<TextView>(R.id.map_info_distance)

        val intent = intent
        var tag = intent.getStringExtra("disTag")

        var tx = tag!!.split("*")

        latlng = tx[3] + "," + tx[4]

        getMapInfoName.setText(tx[0])
        getMapInfoAddress.setText(tx[1])
        getMapInfoArea.setText("면적 : " + tx[2] + "㎡")
        getMapInfoDistance.setText("거리 : " + tx[5] + "km")



        initLayout()
    }

    private fun initLayout(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        }
        else {
            val display: Display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }
        getWindow().setLayout(round(width * 0.9).toInt(), round(height * 0.22).toInt())
        getWindow().setGravity(Gravity.BOTTOM)
        getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }




}