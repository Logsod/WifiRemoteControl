package com.local.wifi.presenter

import android.content.Context
import com.local.wifi.MainView
import com.local.wifi.uiView.ConnectButtonStates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope

import javax.inject.Inject


@InjectViewState
class MainPresenter @Inject constructor(
    var context: Context
) : MvpPresenter<MainView>() {

    private val client = Client()
    var currentVolume = 0


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
    }

    fun sendCommand(cmd: String) {
        if (cmd.first() == 'v') {
            val command = cmd.replace(";", "").trim().split(":")
            if (command.size == 2) {
                currentVolume = command[1].trim().toInt()
            }
        }
        client.sendCommand(cmd)
    }

    fun onMonitorOffPressed() {
        client.sendCommand("m:0;")
    }

    fun onMonitorOnPressed() {
        client.sendCommand("m:1;")
    }

    fun onConnectStatusPressed() {
    }

    fun onLeftPressed() {
        client.sendCommand("b:0;")
    }

    fun onCenterPressed() {
        client.sendCommand("b:1;")
    }

    fun onRightPressed() {
        client.sendCommand("b:2;")
    }

    fun onConnectPressed(remoteServerIp: String) {
        saveRemoteAddr(remoteServerIp)
        if (client.isActive) {
            client.disconnect()
            viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
        }
        presenterScope.launch {
            client.client(remoteServerIp, 8850).subscribeOn(Schedulers.io())
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
                    }, {})

        }
    }

    fun onIpPressed() {
        val settings = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        val ip = settings.getString("remoteIp", "") ?: ""
        viewState.showIpDialog(ip)
    }

    fun saveRemoteAddr(str: String) {
        val settings = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        settings.edit().putString("remoteIp", str).apply()
    }
}