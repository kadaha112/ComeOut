package adapter

import activities.PlaceDetailActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daehankang.comeout.databinding.RecyclerItemListFragmentBinding
import com.google.gson.Gson
import data.Place

class PlaceListRecyclerAdapter(private val context: Context, private var documents: List<Place>) : RecyclerView.Adapter<PlaceListRecyclerAdapter.VH>() {

    inner class VH(val binding: RecyclerItemListFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            binding.tvPlaceName.text = place.place_name
            binding.tvAddress.text = if (place.road_address_name.isEmpty()) place.address_name else place.road_address_name
            binding.tvDistance.text = "${place.distance}m"

            binding.root.setOnClickListener {
                val intent = Intent(context, PlaceDetailActivity::class.java)
                try {
                    val gson = Gson()
                    val placeJson = gson.toJson(place)
                    intent.putExtra("place", placeJson)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(documents[position])
    }

    fun updatePlaces(newPlaces: List<Place>) {
        val diffResult = DiffUtil.calculateDiff(PlaceDiffCallback(documents, newPlaces))
        documents = newPlaces
        diffResult.dispatchUpdatesTo(this)
    }

    class PlaceDiffCallback(
        private val oldList: List<Place>,
        private val newList: List<Place>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
