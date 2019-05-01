package com.hex.evegate.net

import okhttp3.OkHttpClient

object SafeHttpClient {

    fun safeOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
