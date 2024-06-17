// MainFavorFragment.kt

package fragments

import adapter.PlaceListRecyclerAdapter
import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daehankang.comeout.databinding.FragmentMainFavorBinding
import data.Place

class MainFavorFragment : Fragment() {

    private val binding by lazy { FragmentMainFavorBinding.inflate(layoutInflater) }
    private lateinit var db: SQLiteDatabase

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
        binding.recyclerviewFavor.layoutManager = LinearLayoutManager(requireContext())
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        // SQLite DB... [place.db] 파일 안에... "favor" 테이블에 저장된 장소 정보들을 읽어오기
        db = requireContext().openOrCreateDatabase("place", Activity.MODE_PRIVATE, null)

        val cursor = db.rawQuery("SELECT * FROM favor", null)
        // 레코드의 개수만큼 반복하면서 값들을 읽어오기
        cursor?.apply {
            moveToFirst()

            val placeList: MutableList<Place> = mutableListOf()
            for (i in 0 until count) {
                val id: String = getString(0)
                val place_name = getString(1)
                val category_name = getString(2)
                val phone = getString(3)
                val address_name = getString(4)
                val road_address_name = getString(5)
                val x = getString(6)
                val y = getString(7)
                val place_url = getString(8)
                val distance = getString(9)

                val place = Place(id, place_name, category_name, phone, address_name, road_address_name, x, y, place_url, distance)
                placeList.add(place)

                moveToNext()
            }

            // 리스트 데이터를 리사이클러뷰에 아이템뷰로 보이도록 아답터 설정!
            binding.recyclerviewFavor.adapter = PlaceListRecyclerAdapter(requireContext(), placeList)
        }
    }
}
