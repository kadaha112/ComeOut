package activities

import adapter.PlaceListRecyclerAdapter
import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityFoodListBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import data.KakaoSearchPlaceResponse
import data.Place
import data.PlaceMeta
import network.RetrofitApiService
import network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFoodListBinding.inflate(layoutInflater) }

    val searchQuery : String = "내 주변 맛집"
    var myLocation : Location? = null
    val locationProviderClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    var searchPlaceResponse : KakaoSearchPlaceResponse?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var permissionState : Int = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if(permissionState==PackageManager.PERMISSION_DENIED){
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            requestMyLocation()
        }


    }
    val permissionResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        if(it) requestMyLocation()
        else Toast.makeText(this, "내 위치정보를 제공하지 않아 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }
    private fun requestMyLocation(){


        val request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()


        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
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
    private fun searchPlaces(){

        val retrofit= RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService= retrofit.create(RetrofitApiService::class.java)
        val call= retrofitApiService.searchPlace(searchQuery, myLocation?.longitude.toString(), myLocation?.latitude.toString())
        call.enqueue(object : Callback<KakaoSearchPlaceResponse> {
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse= response.body()


            }
            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@FoodListActivity, "서버에 오류가 있습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }



}