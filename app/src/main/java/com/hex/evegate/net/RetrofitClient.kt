package com.hex.evegate.net

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://azure.evegateradio.ru/api/"

    private var instance: Retrofit? = null

    @JvmStatic
    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(SafeHttpClient.safeOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
        }
        return instance!!
    }


}
