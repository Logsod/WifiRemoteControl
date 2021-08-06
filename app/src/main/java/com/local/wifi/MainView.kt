package com.local.wifi

import com.local.wifi.uiView.ConnectButton
import com.local.wifi.uiView.ConnectButtonStates
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainView : MvpView {
    @AddToEndSingle
    fun setConnectedStatus(state : ConnectButtonStates)

}