package com.hex.evegate.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.hex.evegate.R
import com.hex.evegate.ui.adapter.CommsRVAdapter
import com.hex.evegate.ui.mvp.model.Comms
import com.hex.evegate.ui.mvp.presenter.CommsPresenter
import com.hex.evegate.ui.mvp.view.CommsView

class CommsActivity : MvpAppCompatActivity(), CommsView {
    @InjectPresenter
    lateinit var commsPresenter: CommsPresenter

    private lateinit var rvComms: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.comms_activity)
        rvComms = findViewById(R.id.rvSongs)
    }

    override fun showComms(list: List<Comms>) {
        rvComms.setHasFixedSize(false)
        rvComms.layoutManager = LinearLayoutManager(this@CommsActivity)
        val adapter = CommsRVAdapter(this@CommsActivity)
        adapter.setData(list)
        rvComms.adapter = adapter
    }
}