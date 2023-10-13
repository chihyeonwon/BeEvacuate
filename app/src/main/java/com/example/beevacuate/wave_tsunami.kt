package com.example.beevacuate


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beevacuate.R


class wave_tsunami: Fragment() {
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("띄워짐"," a")

        return inflater.inflate(R.layout.wave_tsunami, container, true)
    }



}