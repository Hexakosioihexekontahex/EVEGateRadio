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

    private lateinit var llFirst: LinearLayout
    private lateinit var tvFirst: TextView
    private lateinit var ivFirst: ImageView
    private lateinit var llSecond: LinearLayout
    private lateinit var tvSecond: TextView
    private lateinit var ivSecond: ImageView
    private lateinit var llDiscord: LinearLayout
    private lateinit var tvDiscord: TextView
    private lateinit var ivDiscord: ImageView
    private lateinit var llVk: LinearLayout
    private lateinit var tvVk: TextView
    private lateinit var ivVk: ImageView

    private var firstClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://t.me/evegateradio")))
    }

    private var secondClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://t.me/EVE_ONLINE_RUS")))
    }

    private var discordClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://discord.gg/7DpE7wF")))
    }

    private var vkClickListener = { _: View ->
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://vk.com/evegateradio")))
    }
    //https://www.youtube.com/channel/UCAif8AY9riM1ADWWrytHYZw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.comms_activity)

        llFirst = findViewById(R.id.llFirst)
        tvFirst = findViewById(R.id.tvFirst)
        ivFirst = findViewById(R.id.ivFirst)
        llSecond = findViewById(R.id.llSecond)
        tvSecond = findViewById(R.id.tvSecond)
        ivSecond = findViewById(R.id.ivSecond)
        llDiscord = findViewById(R.id.llDiscord)
        tvDiscord = findViewById(R.id.tvDiscord)
        ivDiscord = findViewById(R.id.ivDiscord)
        llVk = findViewById(R.id.llVk)
        tvVk = findViewById(R.id.tvVk)
        ivVk = findViewById(R.id.ivVk)

        llFirst.setOnClickListener(firstClickListener)
        tvFirst.setOnClickListener(firstClickListener)
        ivFirst.setOnClickListener(firstClickListener)

        llSecond.setOnClickListener(secondClickListener)
        tvSecond.setOnClickListener(secondClickListener)
        ivSecond.setOnClickListener(secondClickListener)

        llDiscord.setOnClickListener(discordClickListener)
        tvDiscord.setOnClickListener(discordClickListener)
        ivDiscord.setOnClickListener(discordClickListener)

        llVk.setOnClickListener(vkClickListener)
        tvVk.setOnClickListener(vkClickListener)
        ivVk.setOnClickListener(vkClickListener)
    }
}