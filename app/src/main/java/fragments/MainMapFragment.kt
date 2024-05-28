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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.kakao.vectormap.MapLifeCycleCallback
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
import java.lang.Exception


class MainMapFragment : Fragment() {


    private val binding: FragmentMainMapBinding by lazy { FragmentMainMapBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 카카오 지도 start
        binding.mapView.start(mapReadyCallback)
    }
    private val mapReadyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback(){
        override fun onMapReady(kakaoMap: KakaoMap) {
            val latitude: Double = (activity as MainActivity).myLocation?.latitude ?: 37.5666
            val longitude: Double = (activity as MainActivity).myLocation?.longitude ?: 126.9782
            val myPos: LatLng = LatLng.from(latitude, longitude)

            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(myPos, 16)
            kakaoMap.moveCamera(cameraUpdate)

            val labelOptions: LabelOptions = LabelOptions.from(myPos).setStyles(R.drawable.ic_mypin)
            val labelLayer: LabelLayer = kakaoMap.labelManager!!.layer!!
            labelLayer.addLabel(labelOptions)

            (activity as MainActivity).searchPlaceResponse?.documents?.forEach {
                val pos = LatLng.from(it.y.toDouble(), it.x.toDouble())
                val options = LabelOptions.from(pos).setStyles(R.drawable.ic_pin)
                    .setTexts(it.place_name, "${it.distance}m").setTag(it)
                kakaoMap.labelManager!!.layer!!.addLabel(options)
            }

            kakaoMap.setOnLabelClickListener { _, _, label ->
                val layout = GuiLayout(Orientation.Vertical).apply {
                    setPadding(16, 16, 16, 16)
                    setBackground(R.drawable.base_msg, true)
                    label.texts.forEach {
                        val guiText = GuiText(it)
                        guiText.setTextSize(30)
                        guiText.setTextColor(Color.WHITE)
                        addView(guiText)
                    }
                }

                val options = InfoWindowOptions.from(label.position).apply {
                    body = layout
                    setBodyOffset(0f, -10f)
                    setTag(label.tag)
                }

                kakaoMap.mapWidgetManager!!.infoWindowLayer.removeAll()
                kakaoMap.mapWidgetManager!!.infoWindowLayer.addInfoWindow(options)
            }

            kakaoMap.setOnInfoWindowClickListener { _, infoWindow, _ ->
                val place: Place = infoWindow.tag as Place
                val json: String = Gson().toJson(place)
                val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                    putExtra("place", json)
                }
                startActivity(intent)
            }
        }
    }


}




