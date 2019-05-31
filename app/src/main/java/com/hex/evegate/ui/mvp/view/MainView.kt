package com.hex.evegate.ui.mvp.view

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView : MvpView {

    fun showProgress(percent: Float)
    fun setCount(count: String)
    fun setSongName(songName: String)
    fun setPlayList(playlist: String)
    fun showLive(isLive: Boolean)
    fun showArt(artUrl: String)
    fun startVisualizer()
    fun stopVisualizer()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showMessage(res: Int)
}