package com.example.moduroad

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moduroad.placeAPI.PlaceSearchService
import com.example.moduroad.placeAPI.PlacesAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.appcompat.widget.SearchView

class LocationInputActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocationButton: Button
    private lateinit var endLocationInput: SearchView
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var adapter: PlacesAdapter

    private val CURRENT_LOCATION_REQUEST_CODE = 2
    private val LOCATION_REQUEST_CODE = 1

    private lateinit var placeSearchService: PlaceSearchService
    private var destinationLat: Double? = null
    private var destinationLng: Double? = null
    private var destinationTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_input)

        // Initialize views
        endLocationInput = findViewById(R.id.search_view)
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        currentLocationButton = findViewById(R.id.button_current_location)

        // Set up RecyclerView
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlacesAdapter { place ->
            returnPlaceLocation(place.lat, place.lng, place.title)
        }
        resultsRecyclerView.adapter = adapter

        // Initialize PlaceSearchService
        placeSearchService = PlaceSearchService(this, adapter)

        // Get destination coordinates from intent
        destinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        destinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        destinationTitle = intent.getStringExtra("destinationTitle")

        if (destinationLat != 0.0 && destinationLng != 0.0) {
            Toast.makeText(this, "Destination set to: $destinationLat, $destinationLng", Toast.LENGTH_SHORT).show()
            returnPlaceLocation(destinationLat!!, destinationLng!!, destinationTitle!!)
        }

        // SearchView query text listener
        endLocationInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    searchPlaces(query)
                } else {
                    Toast.makeText(this@LocationInputActivity, "Please enter a search query", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // No action needed
                return true
            }
        })

        // Current location button click listener
        currentLocationButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CURRENT_LOCATION_REQUEST_CODE)
            } else {
                fetchCurrentLocation()
            }
        }

        // Adjust insets for view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Select from map button click listener
        val selectFromMapButton = findViewById<Button>(R.id.button_select_from_map)
        selectFromMapButton.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }
    }

    private fun searchPlaces(query: String) {
        // Use the PlaceSearchService to search for places
        placeSearchService.searchPlaces(query, display = 5, start = 1, sort = "random", latitude = 37.5666102, longitude = 126.9783881)
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CURRENT_LOCATION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
            if (location != null) {
                val locationResult = "${location.latitude}, ${location.longitude}"
                val returnIntent = Intent().apply {
                    putExtra("location", locationResult)
                }
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                Toast.makeText(this, "Cannot find current location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun returnPlaceLocation(latitude: Double, longitude: Double, title: String) {
        val locationResult = "${latitude / 10}, ${longitude / 10}"
        val returnIntent = Intent().apply {
            putExtra("location", locationResult)
            putExtra("title", title)

        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val latitude = it.getDoubleExtra("latitude", 0.0)
                val longitude = it.getDoubleExtra("longitude", 0.0)
                val location = "$latitude, $longitude"
                val returnIntent = Intent().putExtra("location", location)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CURRENT_LOCATION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show()
        }
    }
}