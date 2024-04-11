package activities

import adapter.MovieAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainMovieBinding
import data.MovieResponse
import network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivityMovie : AppCompatActivity() {
    private lateinit var binding: ActivityMainMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        RetrofitInstance.api.getDailyBoxOffice("7eccaf19b2266ca008ae9525686c8de5", yesterday)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            binding.movieRecyclerView.adapter =
                                MovieAdapter(it.boxOfficeResult.dailyBoxOfficeList)
                        }
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    // 에러 처리
                }
            })
    }
}