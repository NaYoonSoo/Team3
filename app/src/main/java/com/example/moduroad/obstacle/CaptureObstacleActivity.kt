package com.example.moduroad.obstacle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.moduroad.R
import com.example.moduroad.network.ObstacleRequest
import com.example.moduroad.network.ObstacleResponse
import com.example.moduroad.network.ObstacleRetrofitClient
import com.example.moduroad.network.RegisterResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CaptureObstacleActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var registerButton: Button
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_REQUEST_CODE = 100
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_obstacle)

        imageView = findViewById(R.id.imageView_captured)
        captureButton = findViewById(R.id.button_action)
        registerButton = findViewById(R.id.button_register)
        loadingOverlay = findViewById(R.id.loading_overlay)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            initCamera()
        }

        registerButton.setOnClickListener {
            if (currentLatitude != null && currentLongitude != null) {
                uploadObstacle(imageUri, currentLatitude!!, currentLongitude!!)
            } else {
                Toast.makeText(this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                }
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
        captureButton.text = "다시 촬영하기"
        registerButton.visibility = View.VISIBLE
        getLastLocation()
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLatitude = it.latitude
                        currentLongitude = it.longitude
                    }
                }
        }
    }

    private fun showLoadingOverlay() {
        loadingOverlay.visibility = View.VISIBLE
    }

    private fun hideLoadingOverlay() {
        loadingOverlay.visibility = View.GONE
    }

    private fun uploadObstacle(imageUri: Uri, latitude: Double, longitude: Double) {
        showLoadingOverlay()

        val file = File(imageUri.path!!)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val call = ObstacleRetrofitClient.instance.detectObstacle(body)
        call.enqueue(object : Callback<ObstacleResponse> {
            override fun onResponse(call: Call<ObstacleResponse>, response: Response<ObstacleResponse>) {
                hideLoadingOverlay()
                if (response.isSuccessful) {
                    val result = response.body()
                    var success = false
                    var obstacleType = " "

                    if (result != null) {
                        success = result.success
                        obstacleType = result.obstacleType
                    }

                    if (success) {
                        showConfirmationDialog(obstacleType, latitude, longitude)
                    } else {
                        Toast.makeText(this@CaptureObstacleActivity, "장애물로 인식되지 않았습니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@CaptureObstacleActivity, "서버 오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ObstacleResponse>, t: Throwable) {
                hideLoadingOverlay()
                val errorMessage = t.message ?: "알 수 없는 오류가 발생했습니다."
                Toast.makeText(this@CaptureObstacleActivity, "네트워크 오류: $errorMessage", Toast.LENGTH_LONG).show()

            }
        })
    }


    private fun showConfirmationDialog(obstacleType: String, latitude: Double, longitude: Double) {
        val obstacleName = when (obstacleType) {
            "slope" -> "경사로"
            "stair_steep" -> "계단"
            "bollard" -> "볼라드"
            "crosswalk_curb" -> "연석"
            "sidewalk_curb" -> "연석"
            else -> "장애물"}
        val message = if (obstacleName in listOf("경사로", "볼라드")) {
            "$obstacleName 를 등록하시겠습니까?"
        } else {
            "$obstacleName 을 등록하시겠습니까?"
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("예") { dialog, id ->
                registerObstacle(obstacleType, latitude, longitude)
            }
            .setNegativeButton("아니오") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun registerObstacle(obstacleType: String, latitude: Double, longitude: Double) {
        showLoadingOverlay()

        val obstacleRequest = ObstacleRequest(obstacleType, latitude, longitude)
        Log.d("CaptureObstacleActivity", "Obstacle Request: $obstacleRequest")
        val call = ObstacleRetrofitClient.instance.registerObstacle(obstacleRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                hideLoadingOverlay()
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.success) {
                        val obstacleName = when (obstacleType) {
                            "slope" -> "경사로"
                            "stair_steep" -> "계단"
                            "bollard" -> "볼라드"
                            "crosswalk_curb" -> "연석"
                            "sidewalk_curb" -> "연석"
                            else -> "장애물"}
                        Toast.makeText(this@CaptureObstacleActivity, "${obstacleName}이(가) 등록되었습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@CaptureObstacleActivity, "장애물 등록에 실패하였습니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@CaptureObstacleActivity, "서버 오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                hideLoadingOverlay()
                Toast.makeText(this@CaptureObstacleActivity, "네트워크 오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }
}
