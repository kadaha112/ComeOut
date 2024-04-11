package fragments

import activities.MainActivityFood
import activities.MainActivityMovie
import activities.MainActivityRecommend
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daehankang.comeout.databinding.FragmentMainHomeBinding

class MainHomeFragment : Fragment(){

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

        binding.ivbtnFood.setOnClickListener { activity?.let {
            val intent = Intent(context, MainActivityFood::class.java)
            startActivity(intent)
        } }
        binding.ivbtnMovie.setOnClickListener { activity?.let {
            val intent = Intent(context, MainActivityMovie::class.java)
            startActivity(intent)
        } }
        binding.ivbtnRecommend.setOnClickListener { activity?.let {
            val intent = Intent(context, MainActivityRecommend::class.java)
            startActivity(intent)
        } }





    }


}