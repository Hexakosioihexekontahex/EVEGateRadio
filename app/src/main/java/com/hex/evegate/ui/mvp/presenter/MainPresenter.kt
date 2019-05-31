package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.AppEx
import com.hex.evegate.R
import com.hex.evegate.api.StationApi
import com.hex.evegate.api.dto.NowPlaying
import com.hex.evegate.api.dto.NowPlayingDto
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.radio.RadioManager
import com.hex.evegate.ui.mvp.view.MainView
import com.hex.evegate.util.calculateProgressPercent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private var compositeDisposable: CompositeDisposable? = null
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
        getNowPlayingDto()
    }

    fun configNet() {
        compositeDisposable = CompositeDisposable()
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    private fun unConfigNet() {
        compositeDisposable?.clear()
    }

    fun getNowPlayingDto() {
        compositeDisposable!!.add(stationApi!!.nowPlaying()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleNowPlayingResponse, this::handleNowPlayingError)
        )
    }

    private fun handleNowPlayingResponse(result: Response<NowPlayingDto>) {
        if (result.isSuccessful) {
            if (result.body() != null) {
                freshNowPlaying = result.body()!!.now_playing
                freshness = System.currentTimeMillis() / 1000
                viewState.setTextViewCountText(result.body()!!.listeners.total)
                viewState.setTextViewSongName(result.body()!!.now_playing.song.text)
                viewState.setTextViewPlayList(result.body()!!.now_playing.playlist)
                viewState.showLive(result.body()!!.live.is_live == "true")
                viewState.showArt(result.body()!!.now_playing.song.art)
                viewState.showProgress(calculateProgressPercent(result.body()!!.now_playing))
            }
        } else {
            viewState.showShortToast("Ашипко!")
        }
    }

    private fun handleNowPlayingError(error: Throwable) {
        viewState.showShortToast("Ашипко! ${error.message}")
    }

    private fun refreshNowPlaying() {
        val now = System.currentTimeMillis() / 1000
        if (freshNowPlaying == null) {
            compositeDisposable!!.add(stationApi!!.nowPlaying()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.isSuccessful && it.body() != null) {
                            freshNowPlaying = it.body()!!.now_playing
                            freshness = System.currentTimeMillis() / 1000
                        }
                    }) { viewState.showShortToast("Ашипко! ${it.message}") }
            )
        } else {
            try {
                if (freshNowPlaying!!.played_at.toLong() + freshNowPlaying!!.duration.toLong() < now)
                    getNowPlayingDto()
            } catch (e: NumberFormatException) {}
        }
    }

    fun playOrPause() {
        radioManager.playOrPause(streamURL)
    }

    fun isPlaying() = radioManager.isPlaying

    fun bind() {
        radioManager.bind()
    }

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
        showProgress.start()
    }

    override fun detachView(view: MainView?) {
        super.detachView(view)
        showProgress.cancelChildren()
    }
}