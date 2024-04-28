package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moduroad.network.RetrofitClient
import com.example.moduroad.model.PathRequest
import com.example.moduroad.model.RouteResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_REQUEST_CODE_START = 1
        private const val LOCATION_REQUEST_CODE_END = 2
    }

    private lateinit var startLocationEditText: EditText
    private lateinit var endLocationEditText: EditText
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker
    private var currentPath: PolylineOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)
        endLocationEditText = findViewById(R.id.endLocation)
        mapView = findViewById(R.id.map_view)

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
        setupMap()
    }

    private fun setupMap() {
        naverMap?.let { map ->
            map.minZoom = 10.0
            map.maxZoom = 18.0
            val inchonBounds = LatLngBounds(LatLng(37.2830, 126.3920), LatLng(37.5580, 126.7780))
            map.extent = inchonBounds
            val initialPosition = LatLng(37.4563, 126.7052)
            map.moveCamera(CameraUpdate.scrollTo(initialPosition))
            map.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    private fun moveToLocationAndAddMarker(lat: Double, lng: Double, isStartLocation: Boolean) {
        naverMap?.let { map ->
            val location = LatLng(lat, lng)
            map.moveCamera(CameraUpdate.scrollTo(location))

            if (isStartLocation) {
                if (!this::startMarker.isInitialized) startMarker = Marker()
                startMarker.map = null
                startMarker.position = location
                startMarker.map = map
            } else {
                if (!this::endMarker.isInitialized) endMarker = Marker()
                endMarker.map = null
                endMarker.position = location
                endMarker.map = map
            }
        }
    }

    private fun checkBothLocationsSet() {
        if (startLocationEditText.text.isNotEmpty() && endLocationEditText.text.isNotEmpty()) {
            val startLatLng = startLocationEditText.text.toString().split(",").map { it.trim().toDouble() }
            val endLatLng = endLocationEditText.text.toString().split(",").map { it.trim().toDouble() }
            findPath(startLatLng[0], startLatLng[1], endLatLng[0], endLatLng[1])
        }
    }

    private fun findPath(latStart: Double, lngStart: Double, latEnd: Double, lngEnd: Double) {
        val apiService = RetrofitClient.yourApiInstance
        val request = PathRequest(latStart, lngStart, latEnd, lngEnd)

        apiService.findPath(request).enqueue(object : Callback<RouteResponse> {
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    Log.d("RouteSearchActivity", "Response: ${response.body()}")
                    response.body()?.let {
                        drawRoute(it.route)
                        updateRouteTime(it.time) // 소요 시간 업데이트 메소드를 호출합니다.
                    }
                } else {
                    Log.e("RouteSearchActivity", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                Log.e("RouteSearchActivity", "API call failed", t)
            }
        })
    }

    private fun updateRouteTime(time: String) {
        findViewById<TextView>(R.id.normal_route_time).text = time
        // 다른 경로 옵션들에 대한 시간도 이와 유사하게 설정할 수 있습니다.
    }

    private fun drawRoute(routePoints: List<List<Double>>) {
        // 기존 경로가 있으면 지도에서 제거
        currentPath?.map = null

        // 새로운 경로 생성
        val path = PolylineOverlay().apply {
            color = Color.BLUE
            width = 10
            coords = routePoints.map { LatLng(it[1], it[0]) }
        }

        // 지도에 새로운 경로 추가
        path.map = naverMap

        // 현재 경로 업데이트
        currentPath = path
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val location = data.getStringExtra("location") ?: ""
            val latLng = location.split(",").map { it.trim().toDouble() }
            when (requestCode) {
                LOCATION_REQUEST_CODE_START -> {
                    startLocationEditText.setText(location)
                    moveToLocationAndAddMarker(latLng[0], latLng[1], isStartLocation = true)
                    checkBothLocationsSet()
                }
                LOCATION_REQUEST_CODE_END -> {
                    endLocationEditText.setText(location)
                    moveToLocationAndAddMarker(latLng[0], latLng[1], isStartLocation = false)
                    checkBothLocationsSet()
                }
            }
        }
    }

// 생명주기 관련 메소드들은 이전에 주어진 것처럼 클래스 내부에 위치합니다.


    // 생명주기 관련 메소드들은 여기 위치합니다.
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
        startMarker.map = null
        endMarker.map = null
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
