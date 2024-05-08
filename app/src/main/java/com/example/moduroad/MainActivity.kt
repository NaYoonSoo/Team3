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
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moduroad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var naverMapFragment: NaverMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private lateinit var adapter: PlacesAdapter
    private lateinit var placeSearchService: PlaceSearchService
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PlacesAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // PlaceSearchService 인스턴스 생성
        placeSearchService = PlaceSearchService(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // 검색 서비스를 통해 장소 검색
                    placeSearchService.searchPlaces(it, adapter)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // 위치 서비스 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // NaverMapFragment 인스턴스 생성 및 초기화
        naverMapFragment = NaverMapFragment().apply {
            // firebaseDataManager 할당
            this.firebaseDataManager = FirebaseDataManager()
        }

        // NaverMapFragment 인스턴스를 지정된 container에 추가함
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
        binding.elevatorButton.setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("elevator", naverMapFragment.naverMap, currentLocation)
        }
        binding.escalatorButton.setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("escalator", naverMapFragment.naverMap, currentLocation)
        }
        binding.parkingButton.setOnClickListener {
            naverMapFragment.firebaseDataManager?.fetchLocations("parking", naverMapFragment.naverMap, currentLocation)
        }
        binding.searchRoadButton.setOnClickListener {
            // RouteSearchActivity로 이동
            val intent = Intent(this@MainActivity, RouteSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMenuButton() {
        binding.buttonMenu.setOnClickListener { view ->
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
}
