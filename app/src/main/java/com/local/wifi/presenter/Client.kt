package com.local.wifi.presenter

import android.util.Log
import io.reactivex.Observable
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

sealed class ClientStatus{
    object Connecting : ClientStatus()
    object Connected : ClientStatus()
    object Disconnected : ClientStatus()

}

class Client() {
    var isActive = false
    private var data: String = ""
    private var outputData: String = "helloServer"

    fun sendCommand(output : String){
        outputData = output
    }
    fun client(addr: String, port: Int) = Observable.create<ClientStatus> { emitter ->
        emitter.onNext(ClientStatus.Connecting)
        isActive = true
        try {
            //val connection = Socket(addr, port)
            val connection = Socket()
            connection.connect(InetSocketAddress(addr,port), 3*1000)

            val writer = connection.getOutputStream()
            writer.write(1)
            val reader = Scanner(connection.getInputStream())
            if(connection.isConnected)
                emitter.onNext(ClientStatus.Connected)


            while (isActive) {
                var input = ""
                if (connection.inputStream.available() > 0) {
                    data = reader.nextLine()
                    Log.e("tag", data)

                }
                if (outputData.isNotEmpty()) {
                    writer.write(outputData.length)
                    writer.write(outputData.toByteArray())
                    outputData = ""
                }


            }
            reader.close()
            writer.close()
            connection.close()
        } catch (e: Exception) {
            emitter.onNext(ClientStatus.Disconnected)

        }
    }

    fun disconnect() {
        isActive = false
        outputData = "helloServer"
    }

}