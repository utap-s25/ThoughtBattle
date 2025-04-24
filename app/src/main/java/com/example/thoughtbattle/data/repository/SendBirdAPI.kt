package com.example.thoughtbattle.data.repository

import com.example.thoughtbattle.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface SendbirdApi {
    @Headers("Content-Type: application/json","Accept: application/json")
    @POST("/v3/bots/{user_id}/channels")
    suspend fun addBotToChannel(@Path("user_id") userId: String, @Body requestBody: SendbirdRequestBody)

data class SendbirdRequestBody(
    @SerializedName("channel_urls")
    val channelUrls: List<String>
)



companion object{
    var url = HttpUrl.Builder().scheme("https").host(BuildConfig.SENDBIRD_URL).build()
    fun create():SendbirdApi = create(url)
    private fun create(httpUrl: HttpUrl):SendbirdApi{
        val client = OkHttpClient.Builder().addInterceptor( Interceptor(){
            chain ->
            val newRequest = chain.request().newBuilder().addHeader("Api-Token",BuildConfig.SENDBIRD_API_KEY).build()
            chain.proceed(newRequest)
        }).build()
    return Retrofit.Builder().baseUrl(httpUrl).client(client).addConverterFactory(
        GsonConverterFactory.create()).build().create(SendbirdApi::class.java)
        }
    }
}