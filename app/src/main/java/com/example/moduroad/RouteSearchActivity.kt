package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RouteSearchActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private lateinit var startLocationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let {
                val location = it.getStringExtra("location")
                startLocationEditText.setText(location)
            }
        }
    }
}
