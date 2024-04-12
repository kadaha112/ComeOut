package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daehankang.comeout.databinding.MovieItemBinding
import data.Movie

class MovieAdapter(private var movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        with(holder.binding) {
            val movie = movies[position]
            tvRank.text = movie.rank +"위"
            tvTitle.text = movie.movieNm
            tvReleaseDate.text = "개봉일 : "+movie.openDt
            tvAudience.text = "누적 관객수 : "+movie.audiAcc
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>){
        movies = newMovies
        notifyDataSetChanged()
    }
}