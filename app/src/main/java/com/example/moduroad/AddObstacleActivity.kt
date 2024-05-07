package com.example.moduroad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.MapView
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import android.widget.Button
import com.naver.maps.geometry.LatLng

class AddObstacleActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_obstacle)

        // 지도 뷰를 초기화하고, 맵이 준비되면 콜백을 받습니다.
        mapView = findViewById(R.id.map_view_add_obstacle)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // "장애물 추가" 버튼을 설정합니다. 클릭하면 CaptureObstacleActivity로 이동합니다.
        findViewById<Button>(R.id.button_add_obstacle).setOnClickListener {
            // CaptureObstacleActivity로 이동하는 인텐트를 생성합니다.
            val intent = Intent(this, CaptureObstacleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 추가적으로 맵 설정을 여기에서 진행할 수 있습니다.
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
