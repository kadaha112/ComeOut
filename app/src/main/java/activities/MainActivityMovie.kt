package activities

import adapter.MovieAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadMoviesFromApi()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) // 초기에는 비어 있는 데이터 리스트로 어댑터 생성
        binding.movieRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.movieRecyclerView.adapter = movieAdapter
    }

    private fun loadMoviesFromApi() {
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        RetrofitInstance.api.getDailyBoxOffice("7eccaf19b2266ca008ae9525686c8de5", yesterday).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val movies = response.body()!!.boxOfficeResult.dailyBoxOfficeList
                    movieAdapter.updateMovies(movies)
                } else {
                    Toast.makeText(this@MainActivityMovie, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@MainActivityMovie, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}