package com.example.moduroad

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLngBounds
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.moduroad.model.Place
import com.example.moduroad.model.PlacesResponse
import com.example.moduroad.network.RetrofitClient
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var naverMap: NaverMap
    private var currentMarker: Marker? = null
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrofit을 통한 네이버 주변시설 검색
        searchPlaces("카페")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
        }

        findViewById<ImageButton>(R.id.button_current_location).setOnClickListener {
            updateCurrentLocationAndMoveCamera()
        }

        findViewById<AppCompatButton>(R.id.search_road_button).setOnClickListener {
            val intent = Intent(this, RouteSearchActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
        setupMap()
        startLocationUpdates()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
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
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    if (currentMarker == null) {
                        currentMarker = Marker().apply {
                            position = newLatLng
                            map = naverMap
                        }
                    } else {
                        currentMarker!!.position = newLatLng
                    }
                }
            }
        }
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.Builder(5000L).build()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
            return
        }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    private fun searchPlaces(query: String) {
        val call = RetrofitClient.instance.searchPlaces(
            clientId = "bP2f2VGdQIzl8E6KLXp4",
            clientSecret = "LNpJPXF0XJ",
            query = query
        )

        call.enqueue(object : Callback<PlacesResponse> {
            override fun onResponse(
                call: Call<PlacesResponse>,
                response: Response<PlacesResponse>
            ) {
                if (response.isSuccessful) {
                    val placesResponse = response.body()
                    placesResponse?.items?.let { places ->
                        // 검색 결과가 있는 경우 RecyclerView에 표시
                        showPlaces(places)
                    }
                } else {
                    // 서버에서 오류 응답을 받은 경우
                    Toast.makeText(
                        this@MainActivity,
                        "서버 오류: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                // 네트워크 오류 등으로 검색에 실패한 경우
                Toast.makeText(this@MainActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun showPlaces(places: List<Place>) {
        // RecyclerView 어댑터 설정
        placeAdapter = PlaceAdapter(places)
        recyclerView.adapter = placeAdapter
    }

    class PlaceAdapter(private val places: List<Place>) :
        RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

        class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val placeNameTextView: TextView = itemView.findViewById(R.id.place_name_text_view)
            // 다른 뷰도 필요한 경우 추가
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
            return PlaceViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
            val place = places[position]
            holder.placeNameTextView.text = place.name
            // 다른 데이터도 필요한 경우 추가
        }

        override fun getItemCount(): Int {
            return places.size
        }
    }

}


