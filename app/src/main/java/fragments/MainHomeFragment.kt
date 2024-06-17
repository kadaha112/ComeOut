package fragments

import data.LocationUpdateListener
import activities.MainActivity
import activities.MainActivityFood
import activities.MainActivityMovie
import activities.MainActivityRecommend
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daehankang.comeout.databinding.FragmentMainHomeBinding

class MainHomeFragment : Fragment(), LocationUpdateListener {

    private val binding by lazy { FragmentMainHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val location = arguments?.getParcelable<Location>("location")
        Log.d("MainHomeFragmentLocation", "Received location: $location")

        binding.ivbtnFood.setOnClickListener {
            if (location != null) {
                startMainActivityFood(location)
            } else {
                (activity as MainActivity).locationUpdateListener = this
                (activity as MainActivity).requestMyLocation()
            }
        }

        binding.ivbtnMovie.setOnClickListener { activity?.let {
            val intent = Intent(context, MainActivityMovie::class.java)
            startActivity(intent)
        } }
        binding.ivbtnRecommend.setOnClickListener { activity?.let {
            val intent = Intent(context, MainActivityRecommend::class.java)
            startActivity(intent)
        } }
    }

    private fun startMainActivityFood(location: Location) {
        val intent = Intent(context, MainActivityFood::class.java).apply {
            putExtra("location", location)
            putExtra("searchQuery", "내주변맛집")
        }
        startActivity(intent)
    }

    override fun onLocationUpdated(location: Location) {
        startMainActivityFood(location)
    }
}