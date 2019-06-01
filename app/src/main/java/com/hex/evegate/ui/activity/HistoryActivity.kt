package com.hex.evegate.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.hex.evegate.R
import com.hex.evegate.ui.adapter.ItemsRVAdapter
import com.hex.evegate.ui.mvp.model.Song
import com.hex.evegate.ui.mvp.presenter.HistoryPresenter
import com.hex.evegate.ui.mvp.view.HistoryView


class HistoryActivity : MvpAppCompatActivity(), HistoryView {

    @InjectPresenter
    lateinit var historyPresenter: HistoryPresenter

    private lateinit var coordinator: CoordinatorLayout
    private lateinit var rvSongs: RecyclerView
    private lateinit var li: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configViews()
    }

    override fun configViews() {
        setContentView(R.layout.history_activity)

        coordinator = findViewById(R.id.content)

        li = LayoutInflater.from(this@HistoryActivity)
        rvSongs = findViewById(R.id.rvSongs)
        rvSongs.setHasFixedSize(false)
        rvSongs.layoutManager = LinearLayoutManager(this@HistoryActivity)
    }

    override fun showHistory(songList: List<Song>?) {
        val adapter = ItemsRVAdapter(this@HistoryActivity)
        adapter.setData(songList)
        rvSongs.adapter = adapter
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
    }
}