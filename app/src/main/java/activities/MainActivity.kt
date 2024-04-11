package activities

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainBinding
import data.KakaoSearchPlaceResponse
import fragments.MainHomeFragment
import fragments.MainAccountFragment
import fragments.MainFavorFragment
import fragments.MainMapFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var myLocation: Location?= null
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

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, fragment)
            .commit()
    }
}