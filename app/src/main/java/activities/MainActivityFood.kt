package activities

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainFoodBinding

class MainActivityFood : AppCompatActivity() {

    private val binding by lazy { ActivityMainFoodBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Intent에서 위치 정보와 검색어 추출
        val location = intent.getParcelableExtra<Location>("location")
        val searchQuery = intent.getStringExtra("searchQuery")

        if (location != null && searchQuery != null) {
            // 위치 정보와 검색어를 사용하여 음식점 목록 등을 로드
        } else {
            Toast.makeText(this, "위치 정보 또는 검색어가 제공되지 않았습니다.", Toast.LENGTH_LONG).show()
        }

        Log.d("mainActivityFood","location: $location, searchQuery: $searchQuery")

    }

    private fun setupRecyclerView(location: Location, searchQuery: String) {
        // 리사이클러뷰 설정 로직
        // 예: API를 호출하여 위치와 검색어에 맞는 데이터를 불러와서 표시
    }
}