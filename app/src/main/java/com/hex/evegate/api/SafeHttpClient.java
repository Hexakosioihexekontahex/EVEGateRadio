package com.hex.evegate.api;

import okhttp3.OkHttpClient;

public class SafeHttpClient {

    public static OkHttpClient safeOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }
}
