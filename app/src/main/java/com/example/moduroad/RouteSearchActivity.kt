package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moduroad.placeAPI.RetrofitClient
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
    private var currentType: String = "normal" // 기본으로 'normal' 타입 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)
        endLocationEditText = findViewById(R.id.endLocation)
        mapView = findViewById(R.id.map_view)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        findViewById<RadioGroup>(R.id.transportOptions).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_normal_route -> currentType = "normal"
                R.id.radio_elderly_route -> currentType = "elderly"
                R.id.radio_special_route -> currentType = "wheelchair"
            }
            Log.d("RouteSearchActivity", "Type changed to: $currentType")
            checkBothLocationsSet() // Type 변경 시 경로 검색을 트리거합니다.
        }

        startLocationEditText.setOnClickListener {
            val intent = Intent(this, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_START)
        }

        endLocationEditText.setOnClickListener {
            val intent = Intent(this, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_END)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        startMarker = Marker()
        endMarker = Marker()
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

            val marker = if (isStartLocation) {
                if (!this::startMarker.isInitialized) startMarker = Marker()
                startMarker
            } else {
                if (!this::endMarker.isInitialized) endMarker = Marker()
                endMarker
            }

            marker.position = location
            marker.map = map
        }
    }


    private fun checkBothLocationsSet() {
        if (startLocationEditText.text.isNotEmpty() && endLocationEditText.text.isNotEmpty()) {
            val startLatLng = startLocationEditText.text.toString().split(",").map { it.trim().toDouble() }
            val endLatLng = endLocationEditText.text.toString().split(",").map { it.trim().toDouble() }
            findPath(startLatLng[0], startLatLng[1], endLatLng[0], endLatLng[1], currentType)
        }
    }


    private fun findPath(latStart: Double, lngStart: Double, latEnd: Double, lngEnd: Double, type: String) {
        val apiService = RetrofitClient.yourApiInstance
        val request = PathRequest(latStart, lngStart, latEnd, lngEnd, type)

        Log.d("RouteSearchActivity", "Sending path request with type: $currentType")

        apiService.findPath(request).enqueue(object : Callback<RouteResponse> {
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        drawRoute(it.route)
                        updateRouteTime(it.time, it.distance)
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

    private fun updateRouteTime(time: String, distance: String) {
        val textToShow = "$time ($distance)"
        findViewById<TextView>(R.id.route_info).text = textToShow
    }

    private fun drawRoute(routePoints: List<List<Double>>) {
        currentPath?.map = null

        val path = PolylineOverlay().apply {
            color = Color.BLUE
            width = 10
            coords = routePoints.map { LatLng(it[1], it[0]) }
        }

        path.map = naverMap
        currentPath = path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val location = data.getStringExtra("location")
            Log.d("RouteSearchActivity", "Received location: $location")  // 로그 추가
            if (location != null) {
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
        } else {
            Log.d("RouteSearchActivity", "Failed to receive location or bad resultCode: $resultCode") // 실패 로그 추가
        }
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