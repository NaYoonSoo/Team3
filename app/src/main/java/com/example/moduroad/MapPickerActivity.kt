package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.OnMapReadyCallback
import android.widget.Button
import com.naver.maps.map.overlay.OverlayImage

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var centerMarker: Marker // 중앙 마커를 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        // 지도 객체 초기화
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true

        // 지도의 중앙에 마커를 고정합니다.
        centerMarker = Marker().apply {
            position = naverMap.cameraPosition.target
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_marker) // 마커 이미지 설정
            width = Marker.SIZE_AUTO
            height = Marker.SIZE_AUTO
            anchor = PointF(0.5f, 0.5f) // 마커의 위치를 중앙에 고정
        }

        // 지도가 움직일 때마다 중앙에 마커가 유지되도록 설정합니다.
        naverMap.addOnCameraIdleListener {
            centerMarker.position = naverMap.cameraPosition.target
        }

        // '확인' 버튼을 누를 때의 액션
        findViewById<Button>(R.id.button_confirm).setOnClickListener {
            val centerPosition = naverMap.cameraPosition.target
            val intent = Intent().apply {
                putExtra("latitude", centerPosition.latitude)
                putExtra("longitude", centerPosition.longitude)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
