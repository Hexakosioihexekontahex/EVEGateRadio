package com.hex.evegate.api

import com.hex.evegate.ui.mvp.model.NowPlayingDto

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface StationApi {

    @GET("nowplaying/1")
    fun nowPlaying(): Observable<Response<NowPlayingDto>>
}
