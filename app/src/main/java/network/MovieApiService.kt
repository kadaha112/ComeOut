package network

import data.MovieResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("boxoffice/searchDailyBoxOfficeList.json")
    fun getDailyBoxOffice(@Query("key") apiKey: String, @Query("targetDt") targetDate: String): Call<MovieResponse>
}
object RetrofitInstance {
    private const val BASE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/"

    val api: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}
