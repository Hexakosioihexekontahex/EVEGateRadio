package com.hex.evegate.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hex.evegate.R

class CommsActivity: AppCompatActivity() {

    lateinit var llFirst: LinearLayout
    lateinit var tvFirst: TextView
    lateinit var ivFirst: ImageView
    lateinit var llSecond: LinearLayout
    lateinit var tvSecond: TextView
    lateinit var ivSecond: ImageView

    private var firstClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://t.me/evegateradio")))
    }

    private var secondClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://t.me/EVE_ONLINE_RUS")))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.comms_activity)

        llFirst = findViewById(R.id.llFirst)
        tvFirst = findViewById(R.id.tvFirst)
        ivFirst = findViewById(R.id.ivFirst)
        llSecond = findViewById(R.id.llSecond)
        tvSecond = findViewById(R.id.tvSecond)
        ivSecond = findViewById(R.id.ivSecond)

        llFirst.setOnClickListener(firstClickListener)
        tvFirst.setOnClickListener(firstClickListener)
        ivFirst.setOnClickListener(firstClickListener)

        llSecond.setOnClickListener(secondClickListener)
        tvSecond.setOnClickListener(secondClickListener)
        ivSecond.setOnClickListener(secondClickListener)
    }
}