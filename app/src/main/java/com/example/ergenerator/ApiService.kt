package com.example.ergenerator

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // This method will send data as JSON
    @POST("api/connect")
    fun connectToDatabase(
        @Body requestBody: RequestBody
    ): Call<ResponseBody>
}
