package activities

import adapter.PlaceListRecyclerAdapter
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.daehankang.comeout.databinding.ActivityMainFoodBinding
import data.KakaoSearchPlaceResponse
import data.Place
import network.RetrofitApiService
import network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityFood : AppCompatActivity() {

    private val binding by lazy { ActivityMainFoodBinding.inflate(layoutInflater) }
    private lateinit var adapter: PlaceListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.foodToolbar.setNavigationOnClickListener { finish() }

        // Intent에서 위치 정보와 검색어 추출
        val location = intent.getParcelableExtra<Location>("location")
        val searchQuery = intent.getStringExtra("searchQuery") ?: "내주변맛집" // 기본값 설정

        if (location != null) {
            setupRecyclerView()
            loadPlaces(location, searchQuery)
        } else {
            Toast.makeText(this, "위치 정보가 제공되지 않았습니다.", Toast.LENGTH_LONG).show()
        }

        Log.d("MainActivityFoodLocation", "location: $location, searchQuery: $searchQuery")
    }

    private fun setupRecyclerView() {
        adapter = PlaceListRecyclerAdapter(this@MainActivityFood, mutableListOf())
        binding.rvFoodList.layoutManager = LinearLayoutManager(this)
        binding.rvFoodList.adapter = adapter
    }

    private fun loadPlaces(location: Location, searchQuery: String) {
        val retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val apiService = retrofit.create(RetrofitApiService::class.java)
        apiService.searchPlace(searchQuery, location.longitude.toString(), location.latitude.toString()).enqueue(object : Callback<KakaoSearchPlaceResponse> {
            override fun onResponse(call: Call<KakaoSearchPlaceResponse>, response: Response<KakaoSearchPlaceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val places = response.body()!!.documents
                    adapter.updatePlaces(places) // Changed method name to `updatePlaces`
                    Log.d("MainActivityFood", "Places loaded successfully.")
                } else {
                    Log.e("MainActivityFood", "Failed to retrieve data, response code: ${response.code()}")
                    Toast.makeText(this@MainActivityFood, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Log.e("MainActivityFood", "API call failed: ${t.message}")
                Toast.makeText(this@MainActivityFood, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
