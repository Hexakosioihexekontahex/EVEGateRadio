package com.hex.evegate.ui.mvp.presenter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.hex.evegate.AppEx
import com.hex.evegate.R
import com.hex.evegate.api.StationApi
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.radio.RadioManager
import com.hex.evegate.ui.mvp.model.NowPlaying
import com.hex.evegate.ui.mvp.model.NowPlayingDto
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

    private val radioManager = RadioManager.getInstance()
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
            freshness = System.currentTimeMillis() / 1000
            try {
                val response = stationApi?.nowPlaying()?.await()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        response.body()?.let { nowPlayingDto ->
                            freshNowPlaying = nowPlayingDto.now_playing
                            viewState.setCount(nowPlayingDto.listeners.total)
                            viewState.setSongName(nowPlayingDto.now_playing.song.text)
                            viewState.showLive(nowPlayingDto.live.is_live == "true")
                            viewState.showArt(nowPlayingDto.now_playing.song.art)
                            viewState.showProgress(calculateProgressPercent(nowPlayingDto.now_playing))
                            if (radioManager.isPlaying) { setNotificationData(nowPlayingDto) }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        viewState.showMessage("Ошибка! ${response?.errorBody() ?: "Сервер не отвечает"}")
                    }
                }
            } catch (e: Throwable) {
                freshness = System.currentTimeMillis() / 1000
                withContext(Dispatchers.Main) {
                    viewState.showMessage("Ошибка! ${e.message ?: "Проверьте интернет соединение"}")
                }
            }
        }
        job?.start()
    }

    private fun setNotificationData(nowPlayingDto: NowPlayingDto) {
        radioManager.getService()?.onTrackUpdated(nowPlayingDto.now_playing.song.title,
                nowPlayingDto.now_playing.song.artist)
        val largeIconSize = Math.round(64 * AppEx.instance!!.resources.displayMetrics.density)
        try {
            Glide.with(AppEx.instance!!)
                    .asBitmap()
                    .load(nowPlayingDto.now_playing.song.art)
                    .override(largeIconSize, largeIconSize)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(resource: Bitmap,
                                                     transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                            radioManager.getService()?.onIconLoaded(resource)
                        }
                    })
        } catch (e: Exception) { }
    }

    private fun refreshNowPlaying() {
        if (freshNowPlaying == null && freshness == null) {
            CoroutineScope(Dispatchers.IO).launch {
                getNowPlayingDto()
            }
        } else {
            try {
                if (freshness == null) {
                    freshness = System.currentTimeMillis() / 1000
                }
                freshness?.let {
                    val now = System.currentTimeMillis() / 1000
                    if ((freshNowPlaying?.played_at?.toLong() ?: now) + (freshNowPlaying?.duration?.toLong() ?: 10L) < now ||
                            it + 10 < now) {
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