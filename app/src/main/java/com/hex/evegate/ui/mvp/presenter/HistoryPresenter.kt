package com.hex.evegate.ui.mvp.presenter

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.api.StationApi
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.ui.mvp.model.Song
import com.hex.evegate.ui.mvp.model.SongHistory
import com.hex.evegate.ui.mvp.view.HistoryView
import kotlinx.coroutines.*

@InjectViewState
class HistoryPresenter : MvpPresenter<HistoryView>(), CoroutineScope by MainScope() {

    override fun attachView(view: HistoryView?) {
        super.attachView(view)

        CoroutineScope(Dispatchers.IO).launch {
            getHistory()
        }
    }

    private suspend fun getHistory() {
        CoroutineScope(Dispatchers.IO + errorHandler).launch {
            try {
                val response = RetrofitClient.getInstance().create(StationApi::class.java).nowPlaying().await()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        var history: List<Song>? = null
                        response.body()?.let { nowPlayingDto -> run {
                            history = nowPlayingDto.song_history.map(SongHistory::song)
                            }
                            viewState.showHistory(history)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        viewState.showMessage("Ошибка! ${response.errorBody() ?: "Сервер не отвечает"}")
                    }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    viewState.showMessage("Ошибка! ${e.message ?: "Проверьте интернет соединение"}")
                }
            }
        }
    }

    private val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(this@HistoryPresenter::class.java.canonicalName, "$coroutineContext ${throwable.message}")
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}