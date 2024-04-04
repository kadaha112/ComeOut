package fragments


import activities.MainActivity
import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.daehankang.comeout.R

import com.daehankang.comeout.databinding.ActivityFragmentMainMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation
import data.KakaoSearchPlaceResponse
import data.Place
import data.PlaceMeta
import network.RetrofitApiService
import network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentMainMap : Fragment() {

    private val binding by lazy { ActivityFragmentMainMapBinding.inflate(layoutInflater) }

    var searchQuery:String= "카페"
    var myLocation: Location?= null
    val locationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(mainActivity) }
    lateinit var mainActivity: MainActivity
    var searchPlaceResponse: KakaoSearchPlaceResponse?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.start(mapReadyCallback)




        val permissionState: Int= ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionState==PackageManager.PERMISSION_DENIED){
            //퍼미션을 요청하는 다이얼로그 보이고 그 결과를 받아오는 작업을 대신해주는 대행사 이용
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //위치정보수집이 허가되어 있다면.. 곧바로 위치정보 얻어오는 작업 시작
            requestMyLocation()
        }
    }
    // 퍼미션요청 및 결과를 받아오는 작업을 대신하는 대행사 등록
    val permissionResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        if(it) requestMyLocation()
        else Toast.makeText(mainActivity, "내 위치정보를 제공하지 않아 검색기능 사용이 제한됩니다 ", Toast.LENGTH_SHORT).show()
    }
    
    private val mapReadyCallback : KakaoMapReadyCallback = object :KakaoMapReadyCallback(){
        override fun onMapReady(kakaoMap: KakaoMap) {
            // 현재 내 위치를 지도의 중심위치로 설정
            val latitude : Double = (activity as MainActivity).myLocation?.latitude ?: 37.566
            val longitude : Double = (activity as MainActivity).myLocation?.longitude ?: 126.9782
            val myPos : LatLng = LatLng.from(latitude,longitude)

            // 내 위치로 지도 카메라 이동
            val cameraUpdate : CameraUpdate = CameraUpdateFactory.newCenterPosition(myPos, 16)
            kakaoMap.moveCamera(cameraUpdate)

            // 내 위치 마커 추가하기
            val labelOptions : LabelOptions = LabelOptions.from(myPos).setStyles(R.drawable.ic_mypin) // 벡터그래픽 이미지는 안됨
            // 라벨이 그려질 레이어 객체 소환
            val labelLayer : LabelLayer = kakaoMap.labelManager!!.layer!!
            // 라벨 레이어에 라벨 추가
            labelLayer.addLabel(labelOptions)
            //-----------------------------------------------------------------------------------

            // 주변 검색 장소들에 마커 추가하기
            val placeList : List<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
            placeList?.forEach {
                // 마커(라벨) 옵션 객체 생성
                val pos = LatLng.from(it.y.toDouble(),it.x.toDouble())
                val options = LabelOptions.from(pos).setStyles(R.drawable.ic_pin).setTexts(it.place_name, "${it.distance}m").setTag(it)
                kakaoMap.labelManager!!.layer!!.addLabel(options)
            }// forEach..

            // 라벨 클릭
            kakaoMap.setOnLabelClickListener{ kakaoMap, layer, label ->

                label.apply {
                    // 정보창 [ infoWindow ] 보여주기
                    val layout = GuiLayout(Orientation.Vertical)
                    layout.setPadding(16,16,16,16)
                    layout.setBackground(R.drawable.base_msg, true)

                    texts.forEach {
                        val guiText = GuiText(it)
                        guiText.setTextSize(30)
                        guiText.setTextColor(Color.WHITE)
                        layout.addView(guiText)
                    }

                    // [정보창 info window] 객체 만들기
                    val options : InfoWindowOptions = InfoWindowOptions.from(label.position)
                    options.body = layout
                    options.setBodyOffset(0f, -10f)
                    options.setTag(tag)

                    kakaoMap.mapWidgetManager!!.infoWindowLayer.removeAll()
                    kakaoMap.mapWidgetManager!!.infoWindowLayer.addInfoWindow(options)
                }// apply..
            }// label click..

        }

    }

    var choiceID= R.id.choice01
    private fun clickChoice(view:View){

        //기존에 선택되었던 ImageView를 찾아서 배경이미지를 선택되지 않은 하얀색 원그림으로 변경
        view.findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)

        //현재 클릭한 ImageView의 배경을 선택된 회색 원그림으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_selected)

        //클릭한 뷰의 id를 저장
        choiceID= view.id

        when(choiceID){
            R.id.choice01->searchQuery="카페"
            R.id.choice02->searchQuery="편의점"
            R.id.choice03->searchQuery="주차장"
            R.id.choice04->searchQuery="주유소"
            R.id.choice05->searchQuery="약국"
            R.id.choice06->searchQuery="꽃집"
            R.id.choice07->searchQuery="화장실"

        }

        //바뀐 검색장소명으로 검색 요청
        searchPlaces()

        //검색창에 글씨가 있다면 지우기..
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()
    }
    private fun searchPlaces(){

        // 레트로핏을 이용한 REST API 작업 수행 - GET방식
        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService= retrofit.create(RetrofitApiService::class.java)
        val call= retrofitApiService.searchPlace(searchQuery, myLocation?.longitude.toString(), myLocation?.latitude.toString())
        call.enqueue(object : Callback<KakaoSearchPlaceResponse> {
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                //응답받은 json을 파싱한 객체를 참조하기..
                searchPlaceResponse= response.body()

                //먼저 데이터가 온전히 잘 왔는지 파악해보기..
                val meta: PlaceMeta? = searchPlaceResponse?.meta
                val documents: List<Place>? = searchPlaceResponse?.documents
                //AlertDialog.Builder(this@MainActivity).setMessage("${meta?.total_count}\n${documents?.get(0)?.place_name}").create().show()

            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(mainActivity, "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }
    
    private fun requestMyLocation(){

        //요청 객체 생성
        val request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        //실시간 위치정보 갱신 요청 - 퍼미션 체크코드가 있어야만 함.
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationProviderClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())
    }
    private val locationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation= p0.lastLocation

            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만...
            locationProviderClient.removeLocationUpdates(this) //this: locationCallback 객체

            //위치정보를 얻었으니.. 키워드 장소검색 작업 시작!
            searchPlaces()
        }
    }

}




