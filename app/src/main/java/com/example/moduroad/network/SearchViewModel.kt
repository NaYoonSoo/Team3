package com.example.moduroad.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moduroad.SearchApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 데이터 모델 클래스
data class SearchItem(
    val title: String,
    val description: String,
    val link: String = "", // 웹사이트 링크나 상세 페이지에 대한 링크 (옵셔널)
    val thumbnail: String = "" // 이미지 URL 등 (옵셔널)
)

// ViewModel 클래스
class SearchViewModel : ViewModel() {
    val searchResults = MutableLiveData<List<SearchItem>>()
    val errorMessage = MutableLiveData<String>()

    fun searchPlaces(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // RetrofitInstance에서 클라이언트 ID와 클라이언트 시크릿을 가져옵니다.
                val clientId = RetrofitInstance.getClientId()
                val clientSecret = RetrofitInstance.getClientSecret()

                // API 호출
                val response: Response<SearchResponse> = RetrofitInstance.api.searchPlaces(clientId, clientSecret, query)

                if (response.isSuccessful) {
                    // 성공적으로 결과를 받으면 searchResults LiveData를 업데이트합니다.
                    searchResults.postValue(response.body()?.items)
                } else {
                    // 요청이 실패하면 오류 메시지를 포스팅합니다.
                    errorMessage.postValue("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // 네트워크 요청 중 예외가 발생하면 오류 메시지를 포스팅합니다.
                errorMessage.postValue("Exception: ${e.message}")
            }
        }
    }
}

// Retrofit API 인터페이스
interface SearchApiService {
    @GET("search")
    suspend fun searchPlaces(
        @Header("X-Client-ID") clientId: String,
        @Header("X-Client-Secret") clientSecret: String,
        @Query("query") query: String
    ): Response<SearchResponse>
}

// Retrofit 인스턴스 설정
object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://openapi.naver.com") // API의 Base URL을 설정하세요.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SearchApiService by lazy {
        retrofit.create(SearchApiService::class.java)
    }

    fun getClientId(): String {
        // 클라이언트 ID를 가져오는 로직
        return "watt6cqojf"
    }

    fun getClientSecret(): String {
        // 클라이언트 시크릿을 가져오는 로직
        return "TjYQdEwcyZ12KDaqMTpqrGQHncCxD1jQamICZG0J"
    }
}

// API 응답을 위한 데이터 클래스
data class SearchResponse(
    val items: List<SearchItem>
)
