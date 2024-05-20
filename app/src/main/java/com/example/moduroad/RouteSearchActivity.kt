package com.example.moduroad

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.graphics.Color
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moduroad.model.Obstacle
import com.example.moduroad.model.PathRequest
import com.example.moduroad.model.RouteResponse
import com.example.moduroad.placeAPI.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.naver.maps.map.overlay.OverlayImage

class RouteSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_REQUEST_CODE_START = 1
        private const val LOCATION_REQUEST_CODE_END = 2
        private const val DISTANCE_THRESHOLD = 50.0 // 50미터 이내의 장애물만 표시
    }

    private lateinit var startLocationEditText: EditText
    private lateinit var endLocationEditText: EditText
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private lateinit var startMarker: Marker
    private lateinit var endMarker: Marker
    private var currentPath: PolylineOverlay? = null
    private var currentType: String = "normal" // 기본으로 'normal' 타입 설정
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)
        endLocationEditText = findViewById(R.id.endLocation)
        mapView = findViewById(R.id.map_view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        startMarker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.location_start)
        }
        endMarker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.location_end)
        }
        setupMap()

        // Get destination coordinates from intent
        val destinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        val destinationLng = intent.getDoubleExtra("destination_lng", 0.0)

        if (destinationLat != 0.0 && destinationLng != 0.0) {
            Log.d("RouteSearchActivity", "Destination set to: $destinationLat, $destinationLng")
            endLocationEditText.setText("$destinationLat, $destinationLng")
            moveToLocationAndAddMarker(destinationLat, destinationLng, isStartLocation = false)
            setStartLocationToCurrentLocation()
        } else {
            Log.d("RouteSearchActivity", "No destination coordinates provided")
        }
    }

    private fun setStartLocationToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE_START)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLat = it.latitude
                val currentLng = it.longitude
                startLocationEditText.setText("$currentLat, $currentLng")
                moveToLocationAndAddMarker(currentLat, currentLng, isStartLocation = true)
                checkBothLocationsSet()
            } ?: run {
                Toast.makeText(this, "Cannot fetch current location.", Toast.LENGTH_SHORT).show()
            }
        }
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
        Log.d("RouteSearchActivity", "moveToLocationAndAddMarker: lat=$lat, lng=$lng, isStartLocation=$isStartLocation")
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

            Log.d("RouteSearchActivity", "Marker added at: ${marker.position}")
        } ?: run {
            Log.e("RouteSearchActivity", "NaverMap is null")
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

        Log.d("RouteSearchActivity", "Sending path request with type: $currentType and request: $request")

        apiService.findPath(request).enqueue(object : Callback<RouteResponse> {
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("RouteSearchActivity", "API response: ${it}")
                        drawRoute(it.route)
                        updateRouteTime(it.time, it.distance)
                        val obstacles = it.obstacles ?: emptyList() // Null 체크
                        Log.d("RouteSearchActivity", "Obstacles received: $obstacles") // 장애물 로그 추가
                        displayObstacles(obstacles, it.route) // 경로와 함께 장애물 표시
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
        val textView: TextView = findViewById(R.id.route_info)
        val background = ContextCompat.getDrawable(this, R.drawable.border)
        textView.background = background
        val textToShow = "⏰소요시간: $time (\uD83C\uDFF4 $distance)"
        findViewById<TextView>(R.id.route_info).text = textToShow
    }

    private fun drawRoute(routePoints: List<List<Double>>) {
        currentPath?.map = null

        val pathColor = when (currentType) {
            "normal" -> Color.BLUE          // 기본 경로는 파란색
            "elderly" -> Color.GRAY         // 노약자 경로는 회색
            "wheelchair" -> Color.RED       // 휠체어 경로는 빨간색
            else -> Color.BLUE              // 기본값
        }

        val path = PolylineOverlay().apply {
            color = pathColor
            width = 10
            coords = routePoints.map { LatLng(it[1], it[0]) }
        }

        path.map = naverMap
        currentPath = path
    }

    private fun displayObstacles(obstacles: List<Obstacle>, routePoints: List<List<Double>>) {
        val icon = OverlayImage.fromResource(R.drawable.ic_marker) // 아이콘을 한 번만 로드
        val distanceThreshold = 50.0 // 50미터 이내의 장애물만 표시

        Log.d("RouteSearchActivity", "Displaying obstacles. Total obstacles: ${obstacles.size}")

        obstacles.forEach { obstacle ->
            Log.d("RouteSearchActivity", "Obstacle type: ${obstacle.type}, points: ${obstacle.points}")
            val points = obstacle.points ?: emptyList()
            points.forEach { point ->
                Log.d("RouteSearchActivity", "Checking obstacle point: $point")
                val lat = point[1]
                val lon = point[0]
                val isNearRoute = routePoints.any { routePoint ->
                    val distance = haversine(lat, lon, routePoint[1], routePoint[0])
                    Log.d("RouteSearchActivity", "Distance to route point $routePoint: $distance meters")
                    distance <= distanceThreshold
                }
                if (isNearRoute) {
                    Log.d("RouteSearchActivity", "Obstacle near route. Adding marker at: ($lat, $lon)")
                    if (naverMap != null) {
                        Marker().apply {
                            position = LatLng(lat, lon)
                            this.icon = icon
                            map = naverMap
                            Log.d("RouteSearchActivity", "Marker added at: ($lat, $lon)")
                        }
                    } else {
                        Log.e("RouteSearchActivity", "NaverMap is null, cannot add marker")
                    }
                }
            }
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // 지구의 반경 (미터)
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon1 - lon2)

        val a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c // 두 지점 간의 거리 (미터)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val location = data.getStringExtra("location")
            Log.d("RouteSearchActivity", "Received location: $location")
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
            Log.d("RouteSearchActivity", "Failed to receive location or bad resultCode: $resultCode")
        }
    }

    // 생명주기 관련 메소드들
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
