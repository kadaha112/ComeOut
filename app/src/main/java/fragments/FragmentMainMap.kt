package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daehankang.comeout.databinding.ActivityFragmentMainMapBinding
import net.daum.mf.map.api.MapView

class FragmentMainMap : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ActivityFragmentMainMapBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val mapView = MapView(context)
        binding.containerFragment.addView(mapView)

        return binding.root
    }

}