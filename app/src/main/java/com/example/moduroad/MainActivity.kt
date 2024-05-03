package com.example.moduroad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var naverMapFragment: NaverMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // firebaseDataManager 초기화
        val firebaseDataManager = FirebaseDataManager()

        // NaverMapFragment 인스턴스 생성 및 초기화
        naverMapFragment = NaverMapFragment().apply {
            // firebaseDataManager 할당
            this.firebaseDataManager = firebaseDataManager
        }

        // NaverMapFragment 인스턴스를 지정된 container에 추가
        supportFragmentManager.beginTransaction().apply {replace(R.id.map_fragment, naverMapFragment)
            commit()
        }

        // elevator_button 클릭 리스너 설정
        findViewById<Button>(R.id.elevator_button).setOnClickListener {naverMapFragment.firebaseDataManager?.fetchLocations(
            "elevator", naverMapFragment.naverMap)
        }

        // escalator_button 클릭 리스너 설정
        findViewById<Button>(R.id.escalator_button).setOnClickListener {naverMapFragment.firebaseDataManager?.fetchLocations(
                "escalator", naverMapFragment.naverMap)
        }

        // parking_button 클릭 리스너 설정
        findViewById<Button>(R.id.parking_button).setOnClickListener {naverMapFragment.firebaseDataManager?.fetchLocations(
                "parking", naverMapFragment.naverMap)
        }

        // search_road_button 클릭 리스너 설정
        findViewById<Button>(R.id.search_road_button).setOnClickListener {
            // RouteSearchActivity로 이동
            val intent = Intent(this@MainActivity, RouteSearchActivity::class.java)
            startActivity(intent)
        }
    }
}
