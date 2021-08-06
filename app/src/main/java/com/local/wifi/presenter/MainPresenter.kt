package com.local.wifi.presenter

import android.util.Log
import com.local.wifi.MainView
import com.local.wifi.uiView.ConnectButton
import com.local.wifi.uiView.ConnectButtonStates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    val client = Client()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
    }

    fun sendCommand(cmd : String){
        client.sendCommand(cmd)
    }

    fun onMonitorOffPressed() {
        Log.e(this.toString(), "onMonitorOffPressed")
        client.sendCommand("m:0;")
    }

    fun onMonitorOnPressed() {
        Log.e(this.toString(), "onMonitorOnPressed")
        client.sendCommand("m:1;")
    }

    fun onConnectStatusPressed() {
        Log.e(this.toString(), "onConnectStatusPressed")
    }

    fun onLeftPressed() {
        Log.e(this.toString(), "onLeftPressed")
        client.sendCommand("b:0;")
    }

    fun onCenterPressed() {
        Log.e(this.toString(), "onCenterPressed")
        client.sendCommand("b:1;")
    }

    fun onRightPressed() {
        Log.e(this.toString(), "onRightPressed")
        client.sendCommand("b:2;")
    }

    fun onIpPressed() {
        Log.e(this.toString(), "onIpPressed")


        if(client.isActive) {
            client.disconnect()
            viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
            return
        }
        presenterScope.launch {
            client.client("192.168.0.101", 8850).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        when (it) {
                            is ClientStatus.Connecting -> {
                                viewState.setConnectedStatus(ConnectButtonStates.CONNECTING)
                            }
                            is ClientStatus.Disconnected -> {
                                viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
                            }
                            is ClientStatus.Connected -> {
                                viewState.setConnectedStatus(ConnectButtonStates.CONNECTED)
                            }
                        }
                        Log.e(this.toString(), it.toString())
                    }, {})

        }
    }

}