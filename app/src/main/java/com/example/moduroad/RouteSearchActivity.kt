package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.MapView
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap

class RouteSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_REQUEST_CODE_START = 1
        private const val LOCATION_REQUEST_CODE_END = 2
    }

    private lateinit var startLocationEditText: EditText
    private lateinit var endLocationEditText: EditText
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)
        endLocationEditText = findViewById(R.id.endLocation)
        mapView = findViewById(R.id.map_view)

        // 지도 설정
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        startLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_START)
        }

        endLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_END)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        showMapIfLocationSet()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            data?.getStringExtra("location")?.let { location ->
                when (requestCode) {
                    LOCATION_REQUEST_CODE_START -> startLocationEditText.setText(location)
                    LOCATION_REQUEST_CODE_END -> endLocationEditText.setText(location)
                }
                showMapIfLocationSet()
            }
        }
    }

    private fun showMapIfLocationSet() {
        if (startLocationEditText.text.isNotEmpty() && endLocationEditText.text.isNotEmpty()) {
            mapView.visibility = View.VISIBLE
            // TODO: 지도에 도보 경로를 표시하는 추가 로직을 여기에 구현합니다.
        } else {
            mapView.visibility = View.GONE
        }
    }

    // 생명주기 관련 오버라이드 메서드들
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
