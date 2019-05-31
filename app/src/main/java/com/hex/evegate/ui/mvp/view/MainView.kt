package com.hex.evegate.ui.mvp.view

import com.arellomobile.mvp.MvpView

interface MainView : MvpView {

    fun showProgress(percent: Float)
    fun setTextViewCountText(count: String)
    fun setTextViewSongName(songName: String)
    fun setTextViewPlayList(playlist: String)
    fun showLive(isLive: Boolean)
    fun showArt(artUrl: String)
    fun showShortToast(message: String)
    fun showShortToast(res: Int)
    fun startVisualizer()
    fun stopVisualizer()
}