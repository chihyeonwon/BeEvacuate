package com.example.beevacuate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.opencsv.CSVReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.*
import kotlin.properties.Delegates


class ShelterCheckActivity : AppCompatActivity(), OnMapReadyCallback {
    var TAG: String = "로그"


    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private var locationManager: LocationManager? = null
    private lateinit var currentLocation: Location

    var count = 0

    private var MarkerList = arrayListOf<Marker>() // 마커 리스트
    private var addList = arrayListOf<String>() // 도로명 주소 리스트
    private var bpnList = arrayListOf<String>() // 사업장명 리스트
    private var areaList = arrayListOf<String>() // 소재지 면적 리스트
    private var latList = arrayListOf<String>() // 위도 리스트
    private var lngList = arrayListOf<String>() // 경도 리스트
    private var distanceList = arrayListOf<Float>() // 거리 리스트
    private var distanceIndex by Delegates.notNull<Int>() // 최소거리 인덱스

    private lateinit var userLocAddress: String // 사용자의 현재 위치 주소


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shelter_main)


        var shelterButton = findViewById<Button>(R.id.ShelterButton)
        var researchButton = findViewById<Button>(R.id.ReSearchButton)

        // 뷰 역할을 하는 프래그먼트 객체 얻기
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // 인터페이스 역할을 하는 NaverMap 객체 얻기
        // 프래그먼트(MapFragment)의 getMapAsync() 메서드로 OnMapReadyCallback 을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다고 한다.
        // NaverMap 객체가 준비되면 OnMapReady() 콜백 메서드 호출
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)


//        // 가까운 대피소 조회


        shelterButton.setOnClickListener {

            getMarkerDistance()



            val intent = Intent(this, NearShelterActivity::class.java)

            val disTag = bpnList[distanceIndex] + "*" + addList[distanceIndex] + "*" + areaList[distanceIndex] + "*" + latList[distanceIndex] + "*" + lngList[distanceIndex] + "*" + distanceList[distanceIndex]
            intent.putExtra("disTag", disTag)

            startActivity(intent)
        }

        researchButton.setOnClickListener{
            for(i in 0 until count){
                MarkerList[i].map = null
            }
            count = 0
            MarkerList.clear()
            addList.clear()
            bpnList.clear()
            areaList.clear()
            latList.clear()
            lngList.clear()

            Toast.makeText(this, "대피소를 재검색합니다.", Toast.LENGTH_SHORT).show()
            getCurrentLoc()
            getUserLocation()
            openCSV()

            DisplayMarker()
        }


    }


    override fun onMapReady(naverMap: NaverMap) {
        Log.d(TAG, "MainActivity - onMapReady")
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Face
        naverMap.uiSettings.isCompassEnabled = true
        naverMap.uiSettings.isLocationButtonEnabled = true


        //현재 위치 찾기
        getCurrentLoc()
        getUserLocation()

        openCSV()
        DisplayMarker()

        // 시작 시점 화면을 현재 위치로 고정
        val cameraUpdate =
            CameraUpdate.scrollTo(LatLng(currentLocation.latitude, currentLocation.longitude))
                .animate(CameraAnimation.Fly, 1000)
        naverMap.moveCamera(cameraUpdate)




//        var i = 0
//
//
//        // 마커를 클릭했을 시 띄우는 정보 창
//        val infoWindow = InfoWindow()
//
//        while (i < count) {
//            var marker = Marker()
//            marker.icon = OverlayImage.fromResource(R.drawable.shelter_icon)
//            marker.position = LatLng(latList[i].toDouble(), lngList[i].toDouble())
//
//            MarkerList.add(marker)
//
//
//
//            marker.tag =
//                bpnList[i] + "*" + addList[i] + "*" + areaList[i] + "*" + latList[i] + "*" + lngList[i]
//
//            // 마커를 클릭했을 시
//            val listener = Overlay.OnClickListener { overlay ->
//                val marker = overlay as Marker
//
//
//                val intent = Intent(this, MapInfoActivity::class.java)
//                intent.putExtra("Tag", marker.tag.toString())
//
//
//                startActivity(intent)
//                false
//
//
//            }
//
//
//            MarkerList[i].map = naverMap
//            MarkerList[i].onClickListener = listener
//
//
//
//            i++
//        }
//
//
//
//
//
//        // 지도를 클릭했을 시 정보창 닫음
//        naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
//            infoWindow.close()
//        }


    }


    private fun getMin(): Int {
        val min = Collections.min(distanceList)
        for (i in 0..distanceList.size) {
            if (min == distanceList[i]) {
                return i
                break
            }
        }
        return 0
    }


    private fun getMarkerDistance(){
        var i = 0

        while(i < count){
            val distance = calculateDistance(
                currentLocation.latitude,
                currentLocation.longitude,
                latList[i].toDouble(),
                lngList[i].toDouble(),
            )
            distanceList.add(distance.toFloat())

            i++

        }

        distanceIndex = getMin()
    }




    // 현재 위치 찾기
    private fun getCurrentLoc() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        currentLocation = getLatLng()
        if (currentLocation != null) {
            var latitude = currentLocation.latitude
            var longitude = currentLocation.longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: $latitude, $longitude")
        }
    }

    // 현재 위도 경도 찾기
    private fun getLatLng() : Location {
        var currentLatLng: Location? = null
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            getLatLng()
        } else {
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager!!.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER)
        }
        return currentLatLng!!
    }


    // 사용자의 현재 위치를 주소로 변환
    private fun getUserLocation(){
        val geocoder = Geocoder(this)
        var gList: List<Address>? = null
        // var addrList : List<String>? = null
        try {
            gList = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 8)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "setMaskLocation() - 서버에서 주소변환시 에러발생")
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size == 0) {
                Toast.makeText(this, " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show()
            } else {
                val address: Address = gList[0]
                var abc = address.toString().split(" ")
                userLocAddress = abc[2] + " " + abc[3]


            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.d(TAG, "MainActivity - onRequestPermissionsResult")
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                Log.d(TAG, "MainActivity - onRequestPermissionsResult 권 한 거부됨")
                naverMap.locationTrackingMode = LocationTrackingMode.None
            } else {
                Log.d(TAG, "MainActivity - onRequestPermissionsResult 권한 승인됨")
                naverMap.locationTrackingMode = LocationTrackingMode.Follow // 현위치 버튼 컨트롤 활성
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun DisplayMarker(){
        var i = 0


        // 마커를 클릭했을 시 띄우는 정보 창
        val infoWindow = InfoWindow()

        while (i < count) {
            var marker = Marker()
            marker.icon = OverlayImage.fromResource(R.drawable.shelter_icon)
            marker.position = LatLng(latList[i].toDouble(), lngList[i].toDouble())

            MarkerList.add(marker)



            marker.tag =
                bpnList[i] + "*" + addList[i] + "*" + areaList[i] + "*" + latList[i] + "*" + lngList[i]

            // 마커를 클릭했을 시
            val listener = Overlay.OnClickListener { overlay ->
                val marker = overlay as Marker


                val intent = Intent(this, MapInfoActivity::class.java)
                intent.putExtra("Tag", marker.tag.toString())


                startActivity(intent)
                false


            }


            MarkerList[i].map = naverMap
            MarkerList[i].onClickListener = listener



            i++
        }


        // 지도를 클릭했을 시 정보창 닫음
        naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
            infoWindow.close()
        }
    }

    private fun openCSV() {
        val assetManager = this.assets
        val inputStream = assetManager.open("shelter.csv")

        val reader = CSVReader(InputStreamReader(inputStream))


        var nextLine: Array<String>? = arrayOf()
        count = 0

        while (nextLine.apply {
                nextLine = reader.readNext()
            } != null) {
            var ox = nextLine?.get(10)?.toList().toString() // 사용중 유무
            var a = nextLine?.get(18)?.toList().toString() // 도시 비교
            a = a.replace("[", "")
            a = a.replace("]", "")
            a = a.replace(", ", "")

            if(ox == "[사, 용, 중]") {
                if(a.contains(userLocAddress)) {
                    var add = nextLine?.get(19)?.toList().toString()
                    add = add.replace("[", "")
                    add = add.replace("]", "")
                    add = add.replace(", ", "")
                    addList.add(add) //도로명 주소 리스트 추가
                    var bpn = nextLine?.get(21)?.toList().toString()
                    bpn = bpn.replace("[", "")
                    bpn = bpn.replace("]", "")
                    bpn = bpn.replace(", ", "")
                    bpnList.add(bpn) // 사업자명 리스트 추가
                    var area = nextLine?.get(16)?.toList().toString()
                    area = area.replace("[", "")
                    area = area.replace("]", "")
                    area = area.replace(", ", "")
                    areaList.add(area) // 소재지 면적 리스트 추가
                    var lat = nextLine?.get(26)?.toList().toString()
                    lat = lat.replace("[", "")
                    lat = lat.replace("]", "")
                    lat = lat.replace(", ", "")
                    latList.add(lat) // 위도 리스트 추가
                    var lng = nextLine?.get(27)?.toList().toString()
                    lng = lng.replace("[", "")
                    lng = lng.replace("]", "")
                    lng = lng.replace(", ", "")
                    lngList.add(lng) // 경도 리스트 추가

                    count++
                }
            }
        }

        // 해당 위치에 대피소가 존재하지 않을 경우
        if(count == 0) {
            Toast.makeText(this, userLocAddress + "에 대피소가 존재 하지 않습니다", Toast.LENGTH_SHORT).show()
            var ab = userLocAddress.split(" ")
            userLocAddress = ab[0]
            Toast.makeText(this, userLocAddress + "로 검색합니다", Toast.LENGTH_SHORT).show()
            openCSV()
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val LOCATION_REQUEST_CODE = 2

    }

    fun calculateDistance(
        lat1: Double, lon1: Double,  // 현재 좌표의 위도와 경도
        lat2: Double, lon2: Double   // 특정 좌표의 위도와 경도
    ): Double {
        val radius = 6371.0  // 지구의 반경 (단위: km)

        val latDiff = Math.toRadians(lat2 - lat1)
        val lonDiff = Math.toRadians(lon2 - lon1)

        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDiff / 2) * sin(lonDiff / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }



}


