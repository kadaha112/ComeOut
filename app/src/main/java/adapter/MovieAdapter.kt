package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daehankang.comeout.databinding.MovieItemBinding
import data.Movie

class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        with(holder.binding) {
            val movie = movies[position]
            tvRank.text = movie.rank
            tvTitle.text = movie.movieNm
            tvReleaseDate.text = movie.openDt
            tvAudience.text = movie.audiAcc
        }
    }

    override fun getItemCount(): Int = movies.size
}