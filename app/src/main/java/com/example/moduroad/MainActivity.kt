package com.example.moduroad

import MyMapFragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.moduroad.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // MyMapFragment instance 생성, 지정된 container에 추가
// MyMapFragment instance 생성, 지정된 container에 추가
        val mapFragment = MyMapFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.map_fragment, mapFragment)  // 여기의 ID를 map_fragment_container에서 map_fragment로 변경
            commit()
        }


        // search_road_button 찾기
        val searchRoadButton: Button = findViewById(R.id.search_road_button)

        // search_road_button 클릭 리스너 설정
        searchRoadButton.setOnClickListener(View.OnClickListener {
            // RouteSearchActivity로 이동
            val intent = Intent(this@MainActivity, RouteSearchActivity::class.java)
            startActivity(intent)
        })
    }
}
