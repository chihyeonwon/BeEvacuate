package com.example.beevacuate


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beevacuate.R

class typhoon_before : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        val rootView = inflater.inflate(R.layout.typhoon_before_test, container, false)

        return rootView
    }
}
