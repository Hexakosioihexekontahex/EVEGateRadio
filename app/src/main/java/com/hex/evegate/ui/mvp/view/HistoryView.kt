package com.hex.evegate.ui.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.hex.evegate.ui.mvp.model.Song

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface HistoryView : MvpView {

    fun showHistory(songList: List<Song>?)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showMessage(res: Int)
}