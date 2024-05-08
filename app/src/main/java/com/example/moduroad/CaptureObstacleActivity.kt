package com.example.moduroad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class CaptureObstacleActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_obstacle)

        val captureButton: Button = findViewById(R.id.button_capture_photo)
        captureButton.setOnClickListener {
            capturePhoto()
        }
    }

    private fun capturePhoto() {
        val photoFile: File = createImageFile()
        imageUri = Uri.fromFile(photoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        // 이미지 파일 생성 로직
        val imageFileName = "JPEG_" + System.currentTimeMillis() + "_"
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            displayPhoto()
        }
    }

    private fun displayPhoto() {
        val intent = Intent(this, PhotoConfirmationActivity::class.java).apply {
            putExtra("imageUri", imageUri.toString())
        }
        startActivity(intent)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}
