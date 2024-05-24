package com.example.moduroad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PhotoConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_confirmation)

        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        val imageView = findViewById<ImageView>(R.id.imageView_photo)
        Glide.with(this).load(imageUri).into(imageView)

        findViewById<Button>(R.id.button_confirm).setOnClickListener {
            // 사진 저장 로직 또는 추가 행동
            finish()
        }
    }
}
