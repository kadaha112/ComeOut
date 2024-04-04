package activities

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainBinding
import data.KakaoSearchPlaceResponse
import fragments.FragmentMainHome
import fragments.FragmentMainAccount
import fragments.FragmentMainFavor
import fragments.FragmentMainMap

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var myLocation: Location?= null
    var searchPlaceResponse: KakaoSearchPlaceResponse?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        supportFragmentManager.beginTransaction().add(R.id.container_fragment,FragmentMainHome()).commit()

        binding.bnv.setOnItemSelectedListener {
            when (it.itemId){
                R.id.menu_btm_home -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,FragmentMainHome()).commit()
                R.id.menu_btm_map -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,FragmentMainMap()).commit()
                R.id.menu_btm_favor -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,FragmentMainFavor()).commit()
                R.id.menu_btm_account -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,FragmentMainAccount()).commit()
            }
            true
        }


    }
}