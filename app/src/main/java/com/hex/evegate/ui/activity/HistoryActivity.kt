package com.hex.evegate.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hex.evegate.R
import com.hex.evegate.api.StationApi
import com.hex.evegate.api.dto.NowPlayingDto
import com.hex.evegate.api.dto.Song
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.ui.adapter.ItemsRVAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit

class HistoryActivity: AppCompatActivity() {
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var rvSongs: RecyclerView
    private lateinit var li: LayoutInflater

    private var compositeDisposable: CompositeDisposable? = null
    private var retrofit: Retrofit? = null
    private var stationApi: StationApi? = null

    private var songList = listOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configNet()
        configViews()
    }

    private fun configNet() {
        compositeDisposable = CompositeDisposable()
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    private fun configViews() {
        setContentView(R.layout.history_activity)

        coordinator = findViewById(R.id.content)

        li = LayoutInflater.from(this@HistoryActivity)

        configRv()
    }

    private fun configRv() {
        rvSongs = findViewById(R.id.rvSongs)
        rvSongs.setHasFixedSize(false)
        rvSongs.layoutManager = LinearLayoutManager(this@HistoryActivity)
        compositeDisposable!!.add(stationApi!!.nowPlaying()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this@HistoryActivity::handleNowPlayingResponse,
                        this@HistoryActivity::handleNowPlayingError)
        )
    }

    private fun handleNowPlayingResponse(result: Response<NowPlayingDto>) {
        if (result.isSuccessful) {
            result.body()?.let {
                songList = it.song_history.map { songHistory -> songHistory.song.apply { lyrics = songHistory.playlist } }
            }
            val adapter = ItemsRVAdapter(this@HistoryActivity)
            adapter.setData(songList)
            rvSongs.adapter = adapter
        } else {}
    }

    private fun handleNowPlayingError(error: Throwable) {

    }

}