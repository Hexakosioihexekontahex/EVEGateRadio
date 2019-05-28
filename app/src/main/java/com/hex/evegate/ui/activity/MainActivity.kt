package com.hex.evegate.ui.activity

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.hex.evegate.AppEx
import com.hex.evegate.R
import com.hex.evegate.api.StationApi
import com.hex.evegate.api.dto.NowPlaying
import com.hex.evegate.api.dto.NowPlayingDto
import com.hex.evegate.net.RetrofitClient
import com.hex.evegate.radio.PlaybackStatus
import com.hex.evegate.radio.RadioManager
import com.hex.evegate.ui.visualizer.BarVisualizer
import com.hex.evegate.util.calculateProgressPercent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var tvCount: TextView
    lateinit var tvSongName: TextView
    lateinit var tvPlaylist: TextView
    lateinit var ivImage: ImageView
    lateinit var ivBackground: ImageView
    lateinit var chbHQ: CheckBox
    lateinit var ibPlayPause: ImageButton
    private lateinit var dlDrawer: DrawerLayout
    private lateinit var nvMenu: NavigationView

    private lateinit var radioManager: RadioManager

    private lateinit var streamURL: String
    private var lastBackPressTime: Long = 0

    private var compositeDisposable: CompositeDisposable? = null
    private var retrofit: Retrofit? = null
    private var stationApi: StationApi? = null
    var bvVisualizer: BarVisualizer? = null

    private var freshNowPlaying: NowPlaying? = null
    private var freshness: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        configNet()
        getNowPlayingDto()
    }

    private fun initialize() {
        setContentView(R.layout.activity_main)

        radioManager = RadioManager.with(this)
        tvCount = findViewById(R.id.tvCount)
        tvSongName = findViewById(R.id.tvSongName)
        tvPlaylist = findViewById(R.id.tvPlaylist)
        ivImage = findViewById(R.id.ivImage)
        ivBackground = findViewById(R.id.ivBackground)
        dlDrawer = findViewById(R.id.dlDrawer)
        nvMenu = findViewById(R.id.nvMenu)
        val toggle = ActionBarDrawerToggle(
                this, dlDrawer, findViewById(R.id.toolbar), R.string.on, R.string.off)
        dlDrawer.addDrawerListener(toggle)
        toggle.syncState()
        nvMenu.setNavigationItemSelectedListener(this)

        chbHQ = findViewById(R.id.chbHQ)
        chbHQ.isChecked = AppEx.instance!!.shpHQ
        streamURL = if (AppEx.instance!!.shpHQ) { resources.getString(R.string.evegateradio_high)
        } else { resources.getString(R.string.evegateradio_low) }
        chbHQ.setOnCheckedChangeListener { buttonView, isChecked ->
            AppEx.instance!!.shpHQ = isChecked
            streamURL = if (isChecked) { resources.getString(R.string.evegateradio_high)
            } else { resources.getString(R.string.evegateradio_low) }
        }

        ibPlayPause = findViewById(R.id.ibPlayPause)
        ibPlayPause.setOnClickListener { v ->
            if (!TextUtils.isEmpty(streamURL)) {
                radioManager.playOrPause(streamURL)
            }
        }


    }

    private fun getNowPlayingDto() {
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
                tvCount.text = result.body()!!.listeners.total
                tvSongName.text = result.body()!!.now_playing.song.text
                tvPlaylist.text = result.body()!!.now_playing.playlist
                if (result.body()!!.live.is_live == "true") {
                    findViewById<ImageView>(R.id.ivLive).visibility = ImageView.VISIBLE
                }
                try {
                    Glide.with(this).load(result.body()!!.now_playing.song.art)
                            .into(ivBackground)
                } catch (e: Exception) {
                    /*ignored*/
                }
                showProgress(result.body()!!.now_playing)
            }
        } else { Toast.makeText(this, "Ашипко!", Toast.LENGTH_SHORT).show() }
    }

    private fun handleNowPlayingError(error: Throwable) {
        Toast.makeText(this, "Ашипко! ${error.message}", Toast.LENGTH_SHORT).show()
    }

    private fun configNet() {
        compositeDisposable = CompositeDisposable()
        retrofit = RetrofitClient.getInstance()
        stationApi = retrofit!!.create(StationApi::class.java)
    }

    public override fun onStart() {

        super.onStart()

        EventBus.getDefault().register(this)
    }

    private fun startVisualizer() {
        if (RadioManager.with(this).isPlaying) {
            bvVisualizer?.visualizer?.release()
            volumeControlStream = AudioManager.STREAM_MUSIC
            val id = RadioManager.getService().exoPlayer.audioSessionId

            bvVisualizer = findViewById<BarVisualizer>(R.id.bvVisualizer).apply {
                setColor(ContextCompat.getColor(this@MainActivity, R.color.barVisualizer))
                setDensity(70F)
                setPlayer(id)
                visibility = View.VISIBLE
            }
        }
    }

    private fun stopVisualizer() {
        bvVisualizer?.visibility = View.INVISIBLE
        bvVisualizer?.release()
        bvVisualizer?.visualizer?.enabled = false
    }

    public override fun onStop() {

        EventBus.getDefault().unregister(this)

        super.onStop()
    }

    override fun onDestroy() {

        radioManager.unbind()
        compositeDisposable!!.clear()

        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        initialize()
        configNet()
        getNowPlayingDto()

        radioManager.bind()
        startVisualizer()

        showProgress().start()
    }

    override fun onPause() {
        super.onPause()
        showProgress().cancel()
    }

    override fun onBackPressed() {
        if (dlDrawer.isDrawerOpen(GravityCompat.START)) {
            dlDrawer.closeDrawer(GravityCompat.START)
        } else {
            val now = System.currentTimeMillis()
            if (System.currentTimeMillis() - lastBackPressTime < 1000) {
                finish()
            } else {
                lastBackPressTime = now
                val toast = Toast.makeText(this@MainActivity, "Нажмите \"Назад\" еще раз для выхода.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

    }

    @Subscribe
    fun onEvent(status: String) {
        if (status == PlaybackStatus.PLAYING) {
            startVisualizer()
            ibPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            try {stopVisualizer()} catch (e: Exception) {}
            ibPlayPause.setImageResource(android.R.drawable.ic_media_play)
        }
        when (status) {
            PlaybackStatus.LOADING -> {
                ibPlayPause.setImageResource(R.drawable.ic_cloud_download_white_24dp)
            }
            PlaybackStatus.ERROR ->
                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
            R.id.community -> startActivity(Intent(this, CommsActivity::class.java))
        }
        dlDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showProgress(nowPlaying: NowPlaying) {
        val percent = calculateProgressPercent(nowPlaying)

        val llProgressStart = findViewById<LinearLayout>(R.id.llProgressStart)
        val llProgressEnd = findViewById<LinearLayout>(R.id.llProgressEnd)

        llProgressStart.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, percent)
        llProgressEnd.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Math.abs((100 - percent)))
    }

    private fun showProgress() =
        GlobalScope.launch(context = Dispatchers.Main ) {
            while (true) {
                delay(1000)
                refreshNowPlaying()
                freshNowPlaying?.let { showProgress(it)}
            }
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
                    }) {
                        Toast.makeText(this@MainActivity, "Ашипко! ${it.message}",
                                Toast.LENGTH_SHORT).show()
                    }
            )
        } else if (freshNowPlaying!!.played_at.toLong() + freshNowPlaying!!.duration.toLong() < now) {
            getNowPlayingDto()
        }
    }
}
