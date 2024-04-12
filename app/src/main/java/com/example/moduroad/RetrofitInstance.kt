package com.example.moduroad

import android.content.Context
import android.content.pm.PackageManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://openapi.naver.com/"

    // Context를 이용해서 메타데이터를 불러오는 함수
    private fun getMetaData(context: Context, name: String): String {
        return context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData.getString(name) ?: throw IllegalStateException("No meta-data named $name")
    }

    // Retrofit 인스턴스를 lazy로 초기화하여 필요할 때만 생성되도록 함
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 네이버 검색 API 인터페이스의 인스턴스를 lazy로 초기화
    val api: NaverSearchAPI by lazy {
        retrofit.create(NaverSearchAPI::class.java)
    }

    // 클라이언트 ID와 시크릿을 불러오는 함수, Context를 넘겨받아야 함
    fun getClientId(context: Context): String = getMetaData(context, "com.naver.maps.map.CLIENT_ID")

    fun getClientSecret(context: Context): String = getMetaData(context, "com.naver.maps.map.CLIENT_SECRET")
}
