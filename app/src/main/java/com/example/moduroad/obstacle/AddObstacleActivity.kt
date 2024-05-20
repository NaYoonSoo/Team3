package com.example.moduroad.obstacle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.moduroad.R

class AddObstacleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_obstacle)

        // "장애물 추가" 버튼을 설정합니다. 클릭하면 CaptureObstacleActivity로 이동합니다.
        findViewById<Button>(R.id.button_add_obstacle).setOnClickListener {
            // CaptureObstacleActivity로 이동하는 인텐트를 생성합니다.
            val intent = Intent(this, CaptureObstacleActivity::class.java)
            startActivity(intent)
        }
    }
}
