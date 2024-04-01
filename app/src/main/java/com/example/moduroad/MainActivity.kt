package com.example.moduroad

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton // ImageButton import 추가
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.CameraUpdate
import com.naver.maps.geometry.LatLngBounds

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var naverMap: NaverMap
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
        }

        // ImageButton으로 변경
        findViewById<ImageButton>(R.id.button_current_location).setOnClickListener {
            updateCurrentLocationAndMoveCamera()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        setupLocationCallback()
        setupLocationRequest()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 지도 설정 (줌 레벨, 지도 영역 제한 등)
        setupMap()

        // 현재 위치 업데이트 시작
        startLocationUpdates()

        // 앱 시작시 현재 위치로 화면 이동
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                if (currentMarker == null) {
                    currentMarker = Marker().apply {
                        position = currentLatLng
                        map = naverMap
                    }
                } else {
                    currentMarker!!.position = currentLatLng
                }
            } else {
                Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // 새 위치로 마커 위치 업데이트
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    if (currentMarker == null) {
                        currentMarker = Marker().apply {
                            position = newLatLng
                            map = naverMap
                        }
                    } else {
                        currentMarker!!.position = newLatLng
                    }
                    // 위치 갱신만 수행하고 화면 이동은 하지 않도록 수정
                }
            }
        }
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.Builder(5000L).build() // 5초에 한번 다시 위치 찾기
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 사용자에게 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun setupMap() {
        naverMap.minZoom = 10.0
        naverMap.maxZoom = 18.0
        val inchonBounds = LatLngBounds(LatLng(37.2830, 126.3920), LatLng(37.5580, 126.7780))
        naverMap.extent = inchonBounds
        val initialPosition = LatLng(37.4563, 126.7052)
        naverMap.moveCamera(CameraUpdate.scrollTo(initialPosition))
    }

    private fun updateCurrentLocationAndMoveCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 체크 후 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // null 체크
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                if (currentMarker == null) {
                    currentMarker = Marker().apply {
                        position = currentLatLng
                        map = naverMap
                    }
                } else {
                    currentMarker!!.position = currentLatLng
                }
            } else {
                Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
