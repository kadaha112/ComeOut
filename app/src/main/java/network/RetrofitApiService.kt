package network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitApiService {
    @GET("/v1/nid/me")
    fun getNidUserInfo(@Header("Authorization") authorization : String ) : Call<String>
}