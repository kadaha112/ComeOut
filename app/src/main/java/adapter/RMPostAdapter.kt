package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.daehankang.comeout.R
import data.PageItem

class RMPostAdapter(private val context: Context, private val posts: List<PageItem>) : RecyclerView.Adapter<RMPostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommend_item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(context, post)
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
        private val textUserName: TextView = itemView.findViewById(R.id.title)

        fun bind(context: Context, post:PageItem) {
            textUserName.text = post.title
            viewPager.adapter = RMImageAdapter(context, post.imgResId)
        }
    }
}