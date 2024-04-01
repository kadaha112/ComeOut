package network

import data.KakaoSearchPlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {

    @Headers("Authorization: KakaoAK 69d11c31deffdd031c0ae711e20be194")
    @GET("/v2/local/search/keyword,json")
    fun searchPlaceToString(@Query("query") query : String, @Query("x") longitude : String, @Query("y") latitude:String) : Call<String>


    @Headers("Authorization: KakaoAK 69d11c31deffdd031c0ae711e20be194")
    @GET("/v2/local/search/keyword.json?sort=distance")
    fun searchPlace(@Query("query") query:String, @Query("x") longitude:String, @Query("y") latitude:String) : Call<KakaoSearchPlaceResponse>


    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization : String ) : Call<String>

}