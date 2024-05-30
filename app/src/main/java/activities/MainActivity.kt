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
import data.LocationUpdateListener
import fragments.MainHomeFragment
import fragments.MainAccountFragment
import fragments.MainFavorFragment
import fragments.MainMapFragment





class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var myLocation: Location? = null

    val locationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    var searchPlaceResponse: KakaoSearchPlaceResponse? = null

    var locationUpdateListener: LocationUpdateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadFragment(MainHomeFragment())
        binding.bnv.selectedItemId = R.id.menu_btm_home

        binding.bnv.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment? = when (menuItem.itemId) {
                R.id.menu_btm_home -> MainHomeFragment()
                R.id.menu_btm_map -> MainMapFragment()
                R.id.menu_btm_favor -> MainFavorFragment()
                R.id.menu_btm_account -> MainAccountFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) }
            true
        }

        binding.fabWrite.setOnClickListener { startActivity(Intent(this, WriteActivity::class.java)) }
        checkLocationPermissionAndRequestLocation()
    }

    private fun checkLocationPermissionAndRequestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            requestMyLocation()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestMyLocation()
        } else {
            Toast.makeText(this, "내 위치정보를 제공하지 않아 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestMyLocation() {
        val request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "Location permission not granted")
            return
        }

        locationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper()).addOnFailureListener {
            Log.e("MainActivity", "Location request failed: ${it.message}")
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation = p0.lastLocation
            Log.d("MainActivity", "Current location: Lat ${myLocation?.latitude}, Long ${myLocation?.longitude}")

            locationProviderClient.removeLocationUpdates(this)

            locationUpdateListener?.onLocationUpdated(myLocation!!)
        }
    }

    private fun loadFragment(fragment: Fragment) {
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
