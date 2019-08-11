package com.hex.evegate.ui.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hex.evegate.ui.mvp.model.allComms
import com.hex.evegate.ui.mvp.view.CommsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@InjectViewState
class CommsPresenter : MvpPresenter<CommsView>(), CoroutineScope by MainScope() {
    override fun attachView(view: CommsView?) {
        super.attachView(view)
        viewState.showComms(allComms)
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}