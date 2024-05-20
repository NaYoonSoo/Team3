package com.example.moduroad.placeAPI

import com.example.moduroad.network.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://openapi.naver.com/v1/"
    //private const val BASE_URL_YOUR_API = "http://10.0.2.2:5000/"
    private const val BASE_URL_IPV4 = "http://172.20.10.3:5000/"
    private const val BASE_URL_IPV6 = "http://[2001:e60:8018:1d73:d42f:fbab:f3b5:bd6a]:5000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun isIPv6AddressAvailable(): Boolean {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (addr is Inet6Address && !addr.isLoopbackAddress) {
                        return true
                    }
                }
            }
            false
        } catch (e: SocketException) {
            e.printStackTrace()
            false
        }
    }

    private fun getBaseUrlYourApi(): String {
        return if (isIPv6AddressAvailable()) BASE_URL_IPV6 else BASE_URL_IPV4
    }

    val instance: NaverAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NaverAPI::class.java)
    }

    val yourApiInstance: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(getBaseUrlYourApi())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}
