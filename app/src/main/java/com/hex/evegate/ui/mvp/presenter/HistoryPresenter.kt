package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.api.StationApi
import com.hex.evegate.ui.mvp.model.NowPlayingDto
import com.hex.evegate.ui.mvp.model.Song
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.ui.mvp.view.HistoryView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit

@InjectViewState
class HistoryPresenter : MvpPresenter<HistoryView>() {
    private var compositeDisposable: CompositeDisposable? = null
    private var retrofit: Retrofit? = null
    private var stationApi: StationApi? = null

    private var songList = listOf<Song>()

    override fun attachView(view: HistoryView?) {
        super.attachView(view)

        configNet()
        getHistory()
    }

    override fun detachView(view: HistoryView?) {
        super.detachView(view)
        unconfigNet()
    }

    private fun configNet() {
        compositeDisposable = CompositeDisposable()
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    private fun unconfigNet() {
        compositeDisposable?.clear()
    }


    private fun getHistory() {
        compositeDisposable!!.add(stationApi!!.nowPlaying()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleNowPlayingResponse,
                        this::handleNowPlayingError)
        )
    }

    private fun handleNowPlayingResponse(result: Response<NowPlayingDto>) {
        if (result.isSuccessful) {
            result.body()?.let {
                songList = it.song_history.map { songHistory -> songHistory.song.apply { lyrics = songHistory.playlist } }
                viewState.showHistory(songList)
            }
        } else {
            viewState.showMessage("Ашипко!")
        }
    }

    private fun handleNowPlayingError(error: Throwable) {
        viewState.showMessage("Ашипко! ${error.message}")
    }
}