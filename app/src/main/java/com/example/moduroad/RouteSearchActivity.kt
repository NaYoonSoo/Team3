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
        private const val LOCATION_REQUEST_CODE_START = 1
        private const val LOCATION_REQUEST_CODE_END = 2
    }

    private lateinit var startLocationEditText: EditText
    private lateinit var endLocationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_search)

        startLocationEditText = findViewById(R.id.startLocation)
        endLocationEditText = findViewById(R.id.endLocation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_START)
        }

        endLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE_END)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val location = it.getStringExtra("location")
                if (requestCode == LOCATION_REQUEST_CODE_START) {
                    startLocationEditText.setText(location)
                } else if (requestCode == LOCATION_REQUEST_CODE_END) {
                    endLocationEditText.setText(location)
                }
            }
        }
    }
}

