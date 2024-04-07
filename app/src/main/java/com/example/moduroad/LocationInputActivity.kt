package com.example.moduroad

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationInputActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_input)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currentLocationButton = findViewById<Button>(R.id.button_current_location)
        searchInput = findViewById<EditText>(R.id.search_input)

        currentLocationButton.setOnClickListener {
            getCurrentLocation()
        }

        searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val searchText = v.text.toString()
                returnSelectedLocation(searchText)
                true
            } else {
                false
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        val task: Task<Location> = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                val result = "${location.latitude}, ${location.longitude}"
                searchInput.setText(result) // 현재 위치를 EditText에 설정합니다.
                returnSelectedLocation(result) // 현재 위치를 RouteSearchActivity로 반환합니다.
                // 주석 처리된 부분은 실제 앱의 동작에 따라 필요할 경우 사용하세요.
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun returnSelectedLocation(location: String) {
        val returnIntent = Intent()
        returnIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
