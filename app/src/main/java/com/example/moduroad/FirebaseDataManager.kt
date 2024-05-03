/* 주변 시설 표시 (엘레베이터, 에스컬레이터, 장애인용 주차장) */

package com.example.moduroad

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage

class FirebaseDataManager {
    private val db = Firebase.firestore

    // 현재 지도에 표시된 마커들을 저장할 리스트
    private val markersOnMap = mutableListOf<Marker>()

    // 마지막으로 조회한 시설 종류 저장
    private var lastQueriedType: String = ""

    fun fetchLocations(type: String, naverMap: NaverMap) {
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
                    addMarkerOnMap(LatLng(lat, lng), naverMap, type)
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
}
