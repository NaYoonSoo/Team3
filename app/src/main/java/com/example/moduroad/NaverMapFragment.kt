package com.example.moduroad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate

class NaverMapFragment : MapFragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var currentMarker: LocationOverlay? = null
    lateinit var naverMap: NaverMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    var firebaseDataManager: FirebaseDataManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Asynchronously receive the map object
        getMapAsync(this)

        // Initialize the location source
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        return rootView
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationOverlay.isVisible = true
        naverMap.uiSettings.isLocationButtonEnabled = true  // 현재 위치 버튼 활성화
        naverMap.locationSource = locationSource

        setupMap()
        setupLocationRequest()
        setupLocationCallback()
        updateCurrentLocationAndMoveCamera()  // Move camera to current location at start
        startLocationUpdates()

        // 현재 위치 버튼 기능을 프로그래밍적으로 수행
        triggerCurrentLocation()
    }

    private fun setupMap() {
        naverMap.minZoom = 10.0
        naverMap.maxZoom = 18.0
        val inchonBounds = LatLngBounds(LatLng(37.2830, 126.3920), LatLng(37.5580, 126.7780))
        naverMap.extent = inchonBounds
        val initialPosition = LatLng(37.4563, 126.7052)
        naverMap.moveCamera(CameraUpdate.scrollTo(initialPosition))
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000  // Update location every 5 seconds
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    // 위치 오버레이 업데이트
                    naverMap.locationOverlay.run {
                        isVisible = true
                        position = newLatLng
                        bearing = location.bearing
                    }
                }
            }
        }
    }

    private fun updateCurrentLocationAndMoveCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                // 위치 오버레이 초기화
                naverMap.locationOverlay.run {
                    isVisible = true
                    position = currentLatLng
                    bearing = location.bearing
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        locationRequest?.let { req ->
            locationCallback?.let { callback ->
                fusedLocationClient.requestLocationUpdates(req, callback, null)
            } ?: run {
                // locationCallback이 null일 경우의 처리 로직
                Toast.makeText(
                    requireContext(),
                    "Location callback is not initialized",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun triggerCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                naverMap.locationOverlay.run {
                    isVisible = true
                    position = currentLatLng
                    bearing = location.bearing
                }
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
            Toast.makeText(requireContext(), "Location permission is required.", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}
