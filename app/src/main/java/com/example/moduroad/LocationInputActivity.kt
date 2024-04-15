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
    private val LOCATION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_input)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val selectFromMapButton = findViewById<Button>(R.id.button_select_from_map)
        searchInput = findViewById<EditText>(R.id.search_input)

        selectFromMapButton.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }
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
}
