package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.ui.mvp.model.allComms
import com.hex.evegate.ui.mvp.view.CommsView

@InjectViewState
class CommsPresenter : MvpPresenter<CommsView>() {
    override fun attachView(view: CommsView?) {
        super.attachView(view)
        viewState.showComms(allComms)
    }
}