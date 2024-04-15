package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.OnMapReadyCallback
import android.widget.Button
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true

        findViewById<Button>(R.id.button_confirm).setOnClickListener {
            val centerPosition = naverMap.cameraPosition.target
            val intent = Intent().apply {
                putExtra("latitude", centerPosition.latitude)
                putExtra("longitude", centerPosition.longitude)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        setupMap()
        requestLocation()
    }

    private fun setupMap() {
        naverMap.minZoom = 10.0
        naverMap.maxZoom = 18.0
        val inchonBounds = LatLngBounds(LatLng(37.2830, 126.3920), LatLng(37.5580, 126.7780))
        naverMap.extent = inchonBounds
        val initialPosition = LatLng(37.4563, 126.7052)
        naverMap.moveCamera(CameraUpdate.scrollTo(initialPosition))
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
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
