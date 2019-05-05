package com.hex.evegate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hex.evegate.R
import com.hex.evegate.ui.model.allComms

class CommsActivity: AppCompatActivity() {

    private lateinit var rvComms: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.comms_activity)

        configRv()
    }

    private fun configRv() {
        rvComms = findViewById(R.id.rvSongs)
        rvComms.setHasFixedSize(false)
        rvComms.layoutManager = LinearLayoutManager(this@CommsActivity)
        val adapter = CommsRVAdapter(this@CommsActivity)
        adapter.setData(allComms)
        rvComms.adapter = adapter
    }
}