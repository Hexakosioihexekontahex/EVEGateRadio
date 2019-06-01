package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.AppEx
import com.hex.evegate.R
import com.hex.evegate.api.StationApi
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.radio.RadioManager
import com.hex.evegate.ui.mvp.model.NowPlaying
import com.hex.evegate.ui.mvp.view.MainView
import com.hex.evegate.util.calculateProgressPercent
import kotlinx.coroutines.*
import retrofit2.Retrofit

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private var job: Job? = null
    private var retrofit: Retrofit? = null
    private var stationApi: StationApi? = null

    private var freshNowPlaying: NowPlaying? = null
    private var freshness: Long? = null

    private val radioManager = RadioManager.with(AppEx.instance)
    private lateinit var streamURL: String

    private val showProgress =
            GlobalScope.launch(context = Dispatchers.Main ) {
                while (true) {
                    delay(1000)
                    refreshNowPlaying()
                    freshNowPlaying?.let { viewState.showProgress(calculateProgressPercent(it))}
                }
            }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        streamURL = if (AppEx.instance!!.shpHQ) {
            AppEx.instance!!.resources.getString(R.string.evegateradio_high)
        } else {
            AppEx.instance!!.resources.getString(R.string.evegateradio_low)
        }
        radioManager.bind()
        CoroutineScope(Dispatchers.IO).launch {
            getNowPlayingDto()
        }
    }

    fun configNet() {
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    private fun unConfigNet() {
        job?.cancelChildren()
    }

    private suspend fun getNowPlayingDto() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = stationApi?.nowPlaying()?.await()
            if (response?.isSuccessful == true) {
                withContext(Dispatchers.Main) {
                    response.body()?.let { nowPlayingDto ->
                        freshNowPlaying = nowPlayingDto.now_playing
                        freshness = System.currentTimeMillis() / 1000
                        viewState.setCount(nowPlayingDto.listeners.total)
                        viewState.setSongName(nowPlayingDto.now_playing.song.text)
                        viewState.setPlayList(nowPlayingDto.now_playing.playlist)
                        viewState.showLive(nowPlayingDto.live.is_live == "true")
                        viewState.showArt(nowPlayingDto.now_playing.song.art)
                        viewState.showProgress(calculateProgressPercent(nowPlayingDto.now_playing))
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    viewState.showMessage("Ашипко!")
                }
            }
        }
        job?.start()
    }

    private fun refreshNowPlaying() {
        if (freshNowPlaying == null || freshness == null) {
            CoroutineScope(Dispatchers.IO).launch {
                getNowPlayingDto()
            }
        } else {
            try {
                freshness?.let {
                    if (freshNowPlaying!!.played_at.toLong() + freshNowPlaying!!.duration.toLong()
                            < System.currentTimeMillis() / 1000 ||
                            it + 10 < System.currentTimeMillis() / 1000) {
                        CoroutineScope(Dispatchers.IO).launch {
                            getNowPlayingDto()
                        }
                    }
                }
            } catch (e: NumberFormatException) {}
        }
    }

    fun playOrPause() {
        radioManager.playOrPause(streamURL)
    }

    fun isPlaying() = radioManager.isPlaying

    override fun onDestroy() {
        super.onDestroy()

        radioManager.unbind()
        unConfigNet()
    }

    fun onCheckBoxHqChanged(isChecked: Boolean) {
        AppEx.instance!!.shpHQ = isChecked
        streamURL = if (isChecked) { AppEx.instance!!.resources.getString(R.string.evegateradio_high)
        } else { AppEx.instance!!.resources.getString(R.string.evegateradio_low) }
    }

    fun isHQ() = AppEx.instance!!.shpHQ

    override fun attachView(view: MainView?) {
        super.attachView(view)
        CoroutineScope(Dispatchers.IO).launch {
            getNowPlayingDto()
        }
        showProgress.start()
    }

    override fun detachView(view: MainView?) {
        super.detachView(view)
        job?.cancelChildren()
        showProgress.cancelChildren()
    }
}