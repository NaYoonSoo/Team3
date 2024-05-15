package com.example.moduroad

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CaptureObstacleActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_obstacle)

        imageView = findViewById(R.id.imageView_captured)
        captureButton = findViewById(R.id.button_action)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            initCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initCamera() {
        captureButton.setOnClickListener {
            capturePhoto()
        }
    }

    private fun capturePhoto() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.e("CaptureActivity", "File creation failed", ex)
            null
        }
        photoFile?.let {
            val photoURI: Uri? = try {
                FileProvider.getUriForFile(
                    this,
                    "com.example.moduroad.fileprovider",
                    it
                )
            } catch (e: IllegalArgumentException) {
                Log.e("CaptureActivity", "File Uri failed", e)
                null
            }
            photoURI?.let { uri ->
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: Exception) {
                    Log.e("CaptureActivity", "Cannot start camera", e)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).also {
            imageUri = Uri.fromFile(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            displayPhoto()
        }
    }

    private fun displayPhoto() {
        imageView.visibility = View.VISIBLE
        imageView.setImageURI(imageUri)
        captureButton.text = "장애물 등록하기"
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
