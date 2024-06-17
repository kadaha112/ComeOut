package activities

import adapter.RMPostAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.daehankang.comeout.R
import com.daehankang.comeout.databinding.ActivityMainRecommendBinding
import data.PageItem

class MainActivityRecommend : AppCompatActivity() {

    private val binding by lazy { ActivityMainRecommendBinding.inflate(layoutInflater) }

    private lateinit var viewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: RMPostAdapter
    private lateinit var posts: List<PageItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recommendToolbar.setNavigationOnClickListener { finish() }

        posts = listOf(
            PageItem(listOf(R.drawable.p1, R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5),"N서울타워"),
            PageItem(listOf(R.drawable.a1, R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5),"경복궁"),
            PageItem(listOf(R.drawable.b1, R.drawable.b2,R.drawable.b3,R.drawable.b4,R.drawable.b5),"북촌 한옥마을")
        )


        recyclerView = findViewById(R.id.recyclerView)
        postAdapter = RMPostAdapter(this,posts)
        recyclerView.adapter = postAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}