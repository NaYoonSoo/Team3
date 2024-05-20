package com.example.moduroad.placeAPI

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.moduroad.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlaceSearchService(private val context: Context, private val adapter: PlacesAdapter) {

    var isLoading = false
    private var currentLocation: Location? = null

    fun searchPlaces(query: String, display: Int, start: Int, sort: String, latitude: Double, longitude: Double) {
        if (isLoading) return
        isLoading = true

        // Update the currentLocation object
        currentLocation = Location("").apply {
            setLatitude(latitude)
            setLongitude(longitude)
        }

        // Make the Retrofit call
        RetrofitClient.instance.searchPlacesByLocation(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, query, display, start, sort).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val places = response.body()?.items?.map {
                        Place(
                            title = it.title,
                            roadAddress = it.roadAddress,
                            address = it.address,
                            display = it.display,
                            sort = it.sort,
                            start = it.start,
                            lat = it.mapy / 1e6,  // Replace with the actual property name
                            lng = it.mapx / 1e6 // Replace with the actual property name
                        )
                    } ?: emptyList()

                    // Sort the places by distance if currentLocation is not null
                    val sortedPlaces = currentLocation?.let { location ->
                        places.sortedBy { place ->
                            val targetLocation = Location("").apply {
                                setLatitude(place.lat)
                                setLongitude(place.lng)
                            }
                            location.distanceTo(targetLocation)
                        }
                    } ?: places

                    if (start == 1) {
                        adapter.setData(sortedPlaces)
                    } else {
                        adapter.addData(sortedPlaces)
                    }
                } else {
                    Toast.makeText(context, "검색 결과를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// Extension function to calculate the distance between the current Location and a given latitude and longitude
fun Location.distanceTo(latitude: Double, longitude: Double): Float {
    // Create a Location object for the target location
    val targetLocation = Location("").apply {
        this.latitude = latitude
        this.longitude = longitude
    }

    // Use the built-in distanceTo method of the Location class to calculate the distance
    return this.distanceTo(targetLocation)
}
