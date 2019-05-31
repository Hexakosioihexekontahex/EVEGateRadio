package com.hex.evegate.ui.mvp.model

import com.hex.evegate.R

data class Comms(var logoRes: Int, var commsNameRes: Int, var dir: String)

val allComms = listOf(
        Comms(R.drawable.telegram_icon, R.string.evegateradio_chat, "https://t.me/evegateradio"),
        Comms(R.drawable.telegram_icon, R.string.eve_online_rus, "https://t.me/EVE_ONLINE_RUS"),
        Comms(R.drawable.youtube_icon, R.string.youtube, "https://www.youtube.com/channel/UCAif8AY9riM1ADWWrytHYZw"),
        Comms(R.drawable.discord_icon, R.string.discord, "https://discord.gg/7DpE7wF"),
        Comms(R.drawable.vk_icon, R.string.vk, "https://vk.com/evegateradio")
)
