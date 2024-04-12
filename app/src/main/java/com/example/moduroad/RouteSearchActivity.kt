package com.example.moduroad

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

        // 화면을 가득 채우기 위한 설정입니다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // '출발지' EditText를 클릭할 때 새 액티비티를 시작하는 리스너를 설정합니다.
        startLocationEditText.setOnClickListener {
            val intent = Intent(this@RouteSearchActivity, LocationInputActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }
    }

    // LocationInputActivity에서 선택된 위치를 받기 위한 메소드입니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            val location = data?.getStringExtra("location")
            startLocationEditText.setText(location)
        }
    }
}
