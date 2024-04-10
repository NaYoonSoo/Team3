package com.example.moduroad

import MyMapFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // MyMapFragment instance 생성, 지정된 container에 추가
        val mapFragment = MyMapFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.map_fragment_container, mapFragment)
            commit()
        }
    }
}
