/* Firestore에서 데이터를 가져와 주변 시설 표시(엘레베이터, 에스컬레이터, 장애인용 주차장) */

package com.example.moduroad

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage

class FirebaseDataManager {

    private val db = Firebase.firestore

    fun fetchElevatorLocations(naverMap: NaverMap) {
        db.collection("elevator").get().addOnSuccessListener { result ->
            for (document in result) {
                val lat = document.getDouble("latitude")
                val lng = document.getDouble("longitude")
                if (lat != null && lng != null) {
                    addMarkerOnMap(LatLng(lat, lng), naverMap)
                }
            }
        }
    }

    private fun addMarkerOnMap(location: LatLng, naverMap: NaverMap) {
        val marker = Marker()
        marker.position = location
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.elevator_button_click)
    }
}