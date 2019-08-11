package com.hex.evegate.ui.mvp.presenter

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import kotlin.math.roundToInt


@InjectViewState
class MainPresenter : MvpPresenter<MainView>(), CoroutineScope by MainScope() {

    private var retrofit: Retrofit? = null
    private var stationApi: StationApi? = null

    private var freshNowPlaying: NowPlaying? = null
    private var freshness: Long? = null

    private val radioManager = RadioManager.getInstance()
    private lateinit var streamURL: String

    private val showProgress =
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    delay(1000)
                    refreshNowPlaying()
                    freshNowPlaying?.let {
                        val percent = calculateProgressPercent(it)
                        withContext(Dispatchers.Main) {
                            viewState.showProgress(percent)
                        }
                    }
                }
            }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        streamURL = if (AppEx.instance!!.shpHQ) {
            AppEx.instance!!.resources.getString(R.string.evegateradio_high)
        } else {
            AppEx.instance!!.resources.getString(R.string.evegateradio_low)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            configPermissions()
        }
        radioManager.bind()
        CoroutineScope(Dispatchers.IO).launch {
            getNowPlayingDto()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun configPermissions() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsForCheckingList = mutableListOf(
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.WAKE_LOCK,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.RECORD_AUDIO
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissionsForCheckingList.add(android.Manifest.permission.FOREGROUND_SERVICE)
            }

            if (permissionsForCheckingList.isNotEmpty()) {
                val permissionsForChecking = permissionsForCheckingList.toTypedArray()

                val permissionsForRequesting = mutableListOf<String>()

                for (permission in permissionsForChecking) {
                    if (AppEx.instance!!.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "$permission PERMISSION_GRANTED")
                    } else {
                        Log.d("Permissions", "$permission PERMISSION_DENIED")
                        permissionsForRequesting.add(permission)
                    }
                }
                return if (permissionsForRequesting.isNotEmpty()) {
                    viewState.requestPermissions(permissionsForRequesting.toTypedArray())
                    false
                } else {
                    true
                }
            }
        }
        return true
    }


    fun configNet() {
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    private suspend fun getNowPlayingDto() {
        CoroutineScope(Dispatchers.IO + errorHandler).launch {
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
    }

    private fun setNotificationData(nowPlayingDto: NowPlayingDto) {
        radioManager.getService()?.onTrackUpdated(nowPlayingDto.now_playing.song.title,
                nowPlayingDto.now_playing.song.artist)
        val largeIconSize = (64 * AppEx.instance!!.resources.displayMetrics.density).roundToInt()
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
            CoroutineScope(Dispatchers.IO + errorHandler).launch {
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

        cancel()
    }

    fun onCheckBoxHqChanged(isChecked: Boolean) {
        AppEx.instance!!.shpHQ = isChecked
        streamURL = if (isChecked) { AppEx.instance!!.resources.getString(R.string.evegateradio_high)
        } else { AppEx.instance!!.resources.getString(R.string.evegateradio_low) }
    }

    fun isHQ() = AppEx.instance!!.shpHQ

    fun onRequestPermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        var anyDenied = false

        for (i in 0 until permissions.size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                anyDenied = true
            }
        }

        if (anyDenied) {
            viewState.showMessage("Без необходимых разрешений приложение может работать некорректно или не сможет работать вообще")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            }
        }
    }

    override fun attachView(view: MainView?) {
        super.attachView(view)
        CoroutineScope(Dispatchers.IO).launch {
            getNowPlayingDto()
        }
        showProgress.start()
    }

    override fun detachView(view: MainView?) {
        super.detachView(view)
        showProgress.cancelChildren()
    }

    private val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(this@MainPresenter::class.java.canonicalName, "$coroutineContext ${throwable.message}")
    }
}