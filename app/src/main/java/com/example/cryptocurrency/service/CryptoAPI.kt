package com.example.cryptocurrency.service

import com.example.cryptocurrency.model.CryptoModel
import retrofit2.Response
import retrofit2.http.GET

interface CryptoAPI {
    @GET("erkindil/Json/main/cryptolist.json")
    suspend fun getData(): Response<List<CryptoModel>>

}
