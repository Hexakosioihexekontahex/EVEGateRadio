package com.hex.evegate.ui.activity

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.hex.evegate.R
import com.hex.evegate.radio.PlaybackStatus
import com.hex.evegate.radio.RadioManager
import com.hex.evegate.ui.mvp.presenter.MainPresenter
import com.hex.evegate.ui.mvp.view.MainView
import com.hex.evegate.ui.visualizer.BarVisualizer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : MvpAppCompatActivity(), MainView, NavigationView.OnNavigationItemSelectedListener {
    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    lateinit var tvCount: TextView
    lateinit var tvSongName: TextView
    lateinit var tvPlaylist: TextView
    lateinit var ivImage: ImageView
    lateinit var ivBackground: ImageView
    lateinit var chbHQ: CheckBox
    lateinit var ibPlayPause: ImageButton
    private lateinit var dlDrawer: DrawerLayout
    private lateinit var nvMenu: NavigationView

    private var lastBackPressTime: Long = 0

    var bvVisualizer: BarVisualizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainPresenter.configNet()
        initialize()
    }

    private fun initialize() {
        setContentView(R.layout.activity_main)

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
        chbHQ.isChecked = mainPresenter.isHQ()
        chbHQ.setOnCheckedChangeListener { _, isChecked ->
            mainPresenter.onCheckBoxHqChanged(isChecked)
        }

        ibPlayPause = findViewById(R.id.ibPlayPause)
        ibPlayPause.setOnClickListener { v ->
            mainPresenter.playOrPause()
        }
        ibPlayPause.setImageResource(if (mainPresenter.isPlaying()) {
            android.R.drawable.ic_media_pause } else { android.R.drawable.ic_media_play }
        )
    }

    public override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)
    }

    override fun startVisualizer() {
        if (mainPresenter.isPlaying()) {
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

    override fun stopVisualizer() {
        bvVisualizer?.let {
            it.visibility = View.INVISIBLE
            it.release()
            it.visualizer?.enabled = false
        }
    }

    public override fun onStop() {

        EventBus.getDefault().unregister(this)

        super.onStop()
    }

    override fun onResume() {
        super.onResume()

        initialize()
        mainPresenter.getNowPlayingDto()

        if (!mainPresenter.isPlaying()) {
            mainPresenter.bind()
        }
        startVisualizer()

        mainPresenter.startShowingProgress()
    }

    override fun onPause() {
        super.onPause()
        mainPresenter.stopShowingProgress()
        try {stopVisualizer()} catch (e: Exception) {}
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
                showShortToast("Нажмите \"Назад\" еще раз для выхода.")
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
                showShortToast(R.string.no_stream)
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

    override fun showProgress(percent: Float) {
        val llProgressStart = findViewById<LinearLayout>(R.id.llProgressStart)
        val llProgressEnd = findViewById<LinearLayout>(R.id.llProgressEnd)

        llProgressStart.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, percent)
        llProgressEnd.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Math.abs((100 - percent)))
    }
    override fun showShortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showShortToast(res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }

    override fun showArt(artUrl: String) {
        try {
            Glide.with(this).load(artUrl).into(ivBackground)
        } catch (e: Exception) { /*ignored*/ }
    }

    override fun showLive(isLive: Boolean) {
        findViewById<ImageView>(R.id.ivLive).visibility =
                if (isLive) { ImageView.VISIBLE } else { ImageView.GONE }
    }

    override fun setTextViewPlayList(playlist: String) { tvPlaylist.text = playlist }
    override fun setTextViewSongName(songName: String) { tvSongName.text = songName }
    override fun setTextViewCountText(count: String) { tvCount.text = count }
}
