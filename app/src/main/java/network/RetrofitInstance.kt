package network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:3000/" // Ensure this is correct for your backend

    // Create Retrofit instance
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provide ApiService instance
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
