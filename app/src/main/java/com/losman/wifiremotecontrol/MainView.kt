package com.losman.wifiremotecontrol

import com.losman.wifiremotecontrol.uiView.ConnectButtonStates
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface MainView : MvpView {
    @AddToEndSingle
    fun setConnectedStatus(state: ConnectButtonStates)

    @Skip
    fun showIpDialog(ip: String, password: String)

    @Skip
    fun showMessage(message: String)
}