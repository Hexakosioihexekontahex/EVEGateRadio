package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.api.StationApi
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.ui.mvp.model.Song
import com.hex.evegate.ui.mvp.view.HistoryView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

@InjectViewState
class HistoryPresenter : MvpPresenter<HistoryView>() {

    override fun attachView(view: HistoryView?) {
        super.attachView(view)

        CoroutineScope(Dispatchers.IO).launch {
            getHistory()
        }
    }

    private suspend fun getHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.getInstance().create(StationApi::class.java).nowPlaying().await()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    var history: List<Song>? = null
                    response.body()?.let { nowPlayingDto -> run {
                        history = nowPlayingDto.song_history.map { songHistory ->
                                songHistory.song.apply { lyrics = songHistory.playlist }
                            }
                        }
                        viewState.showHistory(history)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    viewState.showMessage("Ашипко!")
                }
            }
        }
    }
}