package com.losman.wifiremotecontrol.presenter

import io.reactivex.Observable
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

sealed class RemoteServerStatus {
    object Connecting : RemoteServerStatus()
    object Connected : RemoteServerStatus()
    object Disconnected : RemoteServerStatus()
    class Error(val message: String) : RemoteServerStatus()
    class Message(val message: String) : RemoteServerStatus()
}

class Client() {
    var isActive = false
    private var data: String = ""
    private var outputData: String = ""

    fun sendCommand(output: String) {
        outputData = output
    }

    fun client(addr: String, port: Int, password: String) =
        Observable.create<RemoteServerStatus> { emitter ->
            emitter.onNext(RemoteServerStatus.Connecting)
            isActive = true
            outputData = "helloServer;p:${password};"
            try {
                //val connection = Socket(addr, port)
                val connection = Socket()
                connection.connect(InetSocketAddress(addr, port), 3 * 1000)

                val writer = connection.getOutputStream()
                writer.write(1)
                val reader = Scanner(connection.getInputStream())
                if (connection.isConnected)
                    emitter.onNext(RemoteServerStatus.Connected)


                while (isActive) {
                    if (connection.isClosed)
                        isActive = false

                    var input = ""
                    if (connection.inputStream.available() > 0) {
                        data = reader.nextLine()
                        emitter.onNext(RemoteServerStatus.Message(data))
                    }
                    if (outputData.isNotEmpty()) {
                        writer.write(outputData.length)
                        writer.write(outputData.toByteArray())
                        outputData = ""
                    }

                    Thread.sleep(1)
                }
                reader.close()
                writer.close()
                connection.close()
                emitter.onNext(RemoteServerStatus.Disconnected)
            } catch (e: Exception) {

                var message = e.message ?: "Server error"
                if(message.contains("ECONNRESET"))
                    message = "Connection reset by peer"
                emitter.onNext(RemoteServerStatus.Error(message))
            }
        }

    fun disconnect() {
        isActive = false
        outputData = ""
    }

}