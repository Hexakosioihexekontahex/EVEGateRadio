package com.hex.evegate.ui.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView : MvpView {

    fun showProgress(percent: Float)
    fun setTextViewCountText(count: String)
    fun setTextViewSongName(songName: String)
    fun setTextViewPlayList(playlist: String)
    fun showLive(isLive: Boolean)
    fun showArt(artUrl: String)
    fun startVisualizer()
    fun stopVisualizer()

    @StateStrategyType(value = SkipStrategy::class)
    fun showShortToast(message: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun showShortToast(res: Int)
}