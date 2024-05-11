package com.example.moduroad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var naverMapFragment: NaverMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치 서비스 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // NaverMapFragment 인스턴스 생성 및 초기화
        naverMapFragment = NaverMapFragment().apply {
            // firebaseDataManager 할당
            this.firebaseDataManager = FirebaseDataManager()
        }

        // NaverMapFragment 인스턴스를 지정된 container에 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.map_fragment, naverMapFragment)
            commit()
        }

        getCurrentLocation() // 사용자의 현재 위치 획득
        setupMenuButton()    // 메뉴 버튼 리스너 설정
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = LatLng(location.latitude, location.longitude)
                setupButtonListeners()
            }
        }
    }

    private fun setupButtonListeners() {
        findViewById<Button>(R.id.elevator_button).setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("elevator", naverMapFragment.naverMap, currentLocation)
        }
        findViewById<Button>(R.id.escalator_button).setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("escalator", naverMapFragment.naverMap, currentLocation)
        }
        findViewById<Button>(R.id.parking_button).setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("parking", naverMapFragment.naverMap, currentLocation)
        }
        findViewById<Button>(R.id.search_road_button).setOnClickListener {
            // RouteSearchActivity로 이동
            val intent = Intent(this@MainActivity, RouteSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMenuButton() {
        findViewById<ImageButton>(R.id.button_menu).setOnClickListener { view ->
            showMenu(view)
        }
    }

    private fun showMenu(v: View) {
        PopupMenu(this, v).apply {
            menuInflater.inflate(R.menu.menu_main, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_add_obstacle -> {
                        // AddObstacleActivity로 이동
                        val intent = Intent(this@MainActivity, AddObstacleActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            // 권한 거부됨
        }
    }

}
