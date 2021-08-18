package com.losman.wifiremotecontrol.presenter

import android.content.Context
import android.util.Log
import com.losman.wifiremotecontrol.MainView
import com.losman.wifiremotecontrol.uiView.ConnectButtonStates
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

    fun onConnectPressed(remoteServerIp: String, serverPassword: String) {
        saveSettings(remoteServerIp, serverPassword)
        if (client.isActive) {
            client.disconnect()
            viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
        }
        presenterScope.launch {
            client.client(remoteServerIp, 8850, serverPassword).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        when (it) {
                            is RemoteServerStatus.Connecting -> {
                                viewState.setConnectedStatus(ConnectButtonStates.CONNECTING)
                            }
                            is RemoteServerStatus.Disconnected -> {
                                viewState.setConnectedStatus(ConnectButtonStates.DISCONNECTED)
                            }
                            is RemoteServerStatus.Connected -> {
                                viewState.setConnectedStatus(ConnectButtonStates.CONNECTED)
                                viewState.showMessage("Connected")
                            }
                            is RemoteServerStatus.Message ->{
                                val re = Regex("[^A-Za-z0-9 ]")
                                val message = re.replace(it.message, "")
                                Log.e(this.toString(),message)
                                viewState.setConnectedStatus(ConnectButtonStates.CONNECTING)
                                viewState.showMessage(message)
                            }
                        }
                    }, {})

        }
    }

    fun onIpPressed() {
        val settings = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        val ip = settings.getString("remoteIp", "") ?: ""
        val password = settings.getString("password", "") ?: ""
        viewState.showIpDialog(ip, password)
    }

    fun saveSettings(ip: String, password: String) {
        val settings = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)

        settings.edit().putString("remoteIp", ip).apply()
        settings.edit().putString("password", password).apply()
    }
}