package com.hex.evegate.ui.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.hex.evegate.ui.mvp.model.Comms

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface CommsView : MvpView {

    fun showComms(list: List<Comms>)
}