package com.hex.evegate.api;

import com.hex.evegate.api.dto.NowPlayingDto;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface StationApi {

    @GET("nowplaying/1")
    public Observable<Response<NowPlayingDto>> nowPlaying();
}
