package com.example.moduroad

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage
import kotlin.math.*

class FirebaseDataManager {
    private val db = Firebase.firestore
    private val markersOnMap = mutableListOf<Marker>()
    private var lastQueriedType: String = ""

    fun fetchLocations(type: String, naverMap: NaverMap, currentLocation: LatLng) {
        if (lastQueriedType == type) {
            clearMarkers()
            lastQueriedType = ""
            return
        }

        clearMarkers()
        lastQueriedType = type

        val collectionPath = when(type) {
            "elevator" -> "elevator"
            "escalator" -> "escalator_Wheelchairlift"
            "parking" -> "parking"
            else -> throw IllegalArgumentException("Invalid location type")
        }

        db.collection(collectionPath).get().addOnSuccessListener { result ->
            for (document in result) {
                val lat = document.getDouble("latitude")
                val lng = document.getDouble("longitude")
                if (lat != null && lng != null) {
                    val distance = calculateDistance(currentLocation.latitude, currentLocation.longitude, lat, lng)
                    if (distance <= 5) { // 5km 이내의 거리에 있는 경우만 마커 추가
                        addMarkerOnMap(LatLng(lat, lng), naverMap, type)
                    }
                }
            }
        }
    }

    private fun addMarkerOnMap(location: LatLng, naverMap: NaverMap, type: String) {
        val marker = Marker()
        marker.position = location
        marker.map = naverMap
        when(type) {
            "elevator" -> marker.icon = OverlayImage.fromResource(R.drawable.elevator_button_click)
            "escalator" -> marker.icon = OverlayImage.fromResource(R.drawable.escalator_button_click)
            "parking" -> marker.icon = OverlayImage.fromResource(R.drawable.parking_button_click)
        }
        markersOnMap.add(marker)
    }

    private fun clearMarkers() {
        for (marker in markersOnMap) {
            marker.map = null
        }
        markersOnMap.clear()
    }

    // 두 위치 사이의 거리를 계산 (단위: km)
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371 // 지구 반경 (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
