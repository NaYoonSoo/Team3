package com.example.moduroad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import android.widget.Button
import com.naver.maps.map.CameraUpdate

class AddObstacleActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_obstacle)

        mapView = findViewById(R.id.map_view_add_obstacle)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        findViewById<Button>(R.id.button_add_obstacle).setOnClickListener {
            // CaptureObstacleActivity로 이동하는 인텐트를 생성합니다.
            val intent = Intent(this, CaptureObstacleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        setupMapLimits()
    }

    private fun setupMapLimits() {
        val inchonBounds = LatLngBounds(
            LatLng(37.2830, 126.3920), // 남서쪽 좌표
            LatLng(37.5580, 126.7780)  // 북동쪽 좌표
        )
        naverMap.extent = inchonBounds
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(37.4563, 126.7052)))
        naverMap.minZoom = 10.0
        naverMap.maxZoom = 18.0
    }

    // 생명주기 관련 메소드들은 이전에 주어진 것처럼 클래스 내부에 위치합니다.


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
