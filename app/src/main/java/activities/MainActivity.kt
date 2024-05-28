package activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import data.KakaoSearchPlaceResponse
import fragments.MainHomeFragment
import fragments.MainAccountFragment
import fragments.MainFavorFragment
import fragments.MainMapFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //2. 현재 내위치 정보 객체 (위도,경도 정보를 멤버로 보유)
    var myLocation:Location?= null

    // [ Google Fused Location API 사용 : play-services-location ]
    val locationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // kakao search API 응답결과 객체 참조변수
    var searchPlaceResponse: KakaoSearchPlaceResponse?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        loadFragment(MainHomeFragment())
        binding.bnv.selectedItemId = R.id.menu_btm_home

        binding.bnv.setOnNavigationItemSelectedListener { menuItem ->
            var fragment : Fragment? = null
            when (menuItem.itemId){
                R.id.menu_btm_home -> fragment = MainHomeFragment()
                R.id.menu_btm_map -> fragment = MainMapFragment()
                R.id.menu_btm_favor -> fragment = MainFavorFragment()
                R.id.menu_btm_account -> fragment = MainAccountFragment()
            }
            fragment?.let { loadFragment(it) }
            true

        }
        Log.d("MainActivity", "Current location: Lat ${myLocation?.latitude}, Long ${myLocation?.longitude}")

        binding.fabWrite.setOnClickListener { startActivity(Intent(this,WriteActivity::class.java)) }

        // 위치정보 제공에 대한 퍼미션 체크
        val permissionState: Int= checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionState== PackageManager.PERMISSION_DENIED){
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
        else Toast.makeText(this, "내 위치정보를 제공하지 않아 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }

    // 현재 위치를 얻어오는 작업요청 코드가 있는 기능메소드
    private fun requestMyLocation(){

        //요청 객체 생성
        val request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        //실시간 위치정보 갱신 요청 - 퍼미션 체크코드가 있어야만 함.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Location permission not granted")
            return
        }

        locationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper()).addOnFailureListener {
            Log.e("MainActivity", "Location request failed: ${it.message}")
        }
    }
    private val locationCallback= object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation= p0.lastLocation
            Log.d("MainActivity", "Current location: Lat ${myLocation?.latitude}, Long ${myLocation?.longitude}")


            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만...
            locationProviderClient.removeLocationUpdates(this) //this: locationCallback 객체


        }
    }
    private fun loadFragment(fragment: Fragment) {
        // 위치 정보가 필요한 프래그먼트에만 Bundle을 통해 위치 정보를 전달
        if (fragment is MainHomeFragment || fragment is MainMapFragment) {
            myLocation?.let { location ->
                val bundle = Bundle()
                bundle.putParcelable("location", location)
                fragment.arguments = bundle
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, fragment)
            .commit()
    }
}