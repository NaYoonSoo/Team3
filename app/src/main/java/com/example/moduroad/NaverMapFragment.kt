import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MyMapFragment : MapFragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)

        // 지도 객체를 비동기로 받아옵니다.
        getMapAsync(this)

        // 위치 정보 소스를 초기화합니다.
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        return rootView
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.locationOverlay.isVisible = false

        // 현위치 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true

        // 위치 정보 소스를 지도에 설정
        naverMap.locationSource = locationSource

        // 현위치 마커
        val marker = Marker()
        naverMap.addOnLocationChangeListener { location ->
            marker.position = LatLng(location.latitude, location.longitude)
            marker.map = naverMap
        }
    }

    // 위치 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
