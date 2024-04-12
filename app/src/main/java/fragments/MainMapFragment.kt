package fragments


import activities.MainActivity
import activities.PlaceDetailActivity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.daehankang.comeout.R

import com.daehankang.comeout.databinding.FragmentMainMapBinding
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


class MainMapFragment : Fragment() {

    private var _binding: FragmentMainMapBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String = "카페"
    var myLocation: Location? = null
    private val locationProviderClient: FusedLocationProviderClient? by lazy {
        context?.let { LocationServices.getFusedLocationProviderClient(it) }
    }
    var searchPlaceResponse: KakaoSearchPlaceResponse? = null

    private val permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) requestMyLocation()
        else Toast.makeText(context, "내 위치정보를 제공하지 않아 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.start(mapReadyCallback)

        checkPermissions()


        binding.etSearch.setOnEditorActionListener{V, actionId, evert ->
            searchQuery = binding.etSearch.text.toString()
            searchPlaces()
            false
        }

        setChoiceButtonsListener()


    }
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestMyLocation()
        }

    }
    private fun requestMyLocation() {
        val request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        //실시간 위치정보 갱신 요청 - 퍼미션 체크코드가 있어야만 함.
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationProviderClient?.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())
    }
    private val locationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation= p0.lastLocation

            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만...
            locationProviderClient?.removeLocationUpdates(this) //this: locationCallback 객체

            //위치정보를 얻었으니.. 키워드 장소검색 작업 시작!
            searchPlaces()
        }
    }
    private fun setChoiceButtonsListener(){
        binding.layoutChoice.choice01.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice02.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice03.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice04.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice05.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice06.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choice07.setOnClickListener { clickChoice(it) }
    }
    var choiceID= R.id.choice01

    private fun clickChoice(view:View){

        //기존에 선택되었던 ImageView를 찾아서 배경이미지를 선택되지 않은 하얀색 원그림으로 변경
        val oldChoice = binding.root.findViewById<ImageView>(choiceID)
        oldChoice.setBackgroundResource(R.drawable.bg_choice)

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
    private fun searchPlaces() {
        Toast.makeText(requireContext(), "$searchQuery\n${myLocation?.latitude},${myLocation?.longitude}", Toast.LENGTH_SHORT).show()

        // 레트로핏을 이용한 REST API 작업 수행 - GET방식
        val retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService = retrofit.create(RetrofitApiService::class.java)
        val call = retrofitApiService.searchPlace(
            searchQuery,
            myLocation?.longitude.toString(),
            myLocation?.latitude.toString()
        )
        call.enqueue(object : Callback<KakaoSearchPlaceResponse> {
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                //응답받은 json을 파싱한 객체를 참조하기..
                searchPlaceResponse = response.body()

                //먼저 데이터가 온전히 잘 왔는지 파악해보기..
                val meta: PlaceMeta? = searchPlaceResponse?.meta
                val documents: List<Place>? = searchPlaceResponse?.documents
                val context = context
                if (context != null) {
                    AlertDialog.Builder(context)
                        .setMessage("${meta?.total_count}\n${documents?.get(0)?.place_name}").create()
                        .show()
                }


            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버 오류가 있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private val mapReadyCallback : KakaoMapReadyCallback = object : KakaoMapReadyCallback(){
        override fun onMapReady(kakaoMap: KakaoMap) {
            val latitude : Double = (activity as MainActivity).myLocation?.latitude ?: 37.566
            val longitude : Double = (activity as MainActivity).myLocation?.longitude ?: 126.9782
            val myPos : LatLng = LatLng.from(latitude,longitude)

            val cameraUpdate : CameraUpdate = CameraUpdateFactory.newCenterPosition(myPos, 16)
            kakaoMap.moveCamera(cameraUpdate)

            val labelOptions : LabelOptions = LabelOptions.from(myPos).setStyles(R.drawable.ic_mypin) // 벡터그래픽 이미지는 안됨

            val labelLayer : LabelLayer = kakaoMap.labelManager!!.layer!!

            labelLayer.addLabel(labelOptions)

            val placeList : List<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
            placeList?.forEach {
                val pos = LatLng.from(it.y.toDouble(),it.x.toDouble())
                val options = LabelOptions.from(pos).setStyles(R.drawable.ic_pin).setTexts(it.place_name, "${it.distance}m").setTag(it)
                kakaoMap.labelManager!!.layer!!.addLabel(options)
            }

            kakaoMap.setOnLabelClickListener{ kakaoMap, layer, label ->

                label.apply {
                    val layout = GuiLayout(Orientation.Vertical)
                    layout.setPadding(16,16,16,16)
                    layout.setBackground(R.drawable.base_msg, true)

                    texts.forEach {
                        val guiText = GuiText(it)
                        guiText.setTextSize(30)
                        guiText.setTextColor(Color.WHITE)
                        layout.addView(guiText)
                    }

                    val options : InfoWindowOptions = InfoWindowOptions.from(label.position)
                    options.body = layout
                    options.setBodyOffset(0f, -10f)
                    options.setTag(tag)

                    kakaoMap.mapWidgetManager!!.infoWindowLayer.removeAll()
                    kakaoMap.mapWidgetManager!!.infoWindowLayer.addInfoWindow(options)
                }// apply..
            }// label click..
            kakaoMap.setOnInfoWindowClickListener { kakaoMap, infoWindow, guiId ->
                // 장소에 대한 상세 소개 웹페이지를 보여주는 화면으로 이동
                val intent = Intent(requireContext(), PlaceDetailActivity::class.java)

                // 클릭한 장소에 대한 정보를 json문자열로 변환하여 전달해주기
                val place: Place = infoWindow.tag as Place
                val json:String = Gson().toJson(place)
                intent.putExtra("place",json)

                startActivity(intent)
            }

        }
    }
}




