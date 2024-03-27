package activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainBinding
import fragments.MainAccountFragment
import fragments.MainFavorFragment
import fragments.MainHomeFragment
import fragments.MainMapFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        supportFragmentManager.beginTransaction().add(R.id.container_fragment,MainMapFragment()).commit()

        binding.bnv.setOnItemSelectedListener {
            when (it.itemId){
                R.id.menu_btm_home -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,MainHomeFragment()).commit()
                R.id.menu_btm_map -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,MainMapFragment()).commit()
                R.id.menu_btm_favor -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,MainFavorFragment()).commit()
                R.id.menu_btm_account -> supportFragmentManager.beginTransaction().replace(R.id.container_fragment,MainAccountFragment()).commit()
            }
            true
        }


    }
}