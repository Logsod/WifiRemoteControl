package com.local.wifi

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.local.wifi.databinding.ActivityMainBinding
import com.local.wifi.presenter.MainPresenter
import com.local.wifi.uiView.CircleButton
import com.local.wifi.uiView.ConnectButton
import com.local.wifi.uiView.ConnectButtonStates
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.log


class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainView,
    CircleButton.OnButtonClickListener {

    private var active: Boolean = false
    private var data: String = ""
    private var outputData: String = ""
    lateinit var binding: ActivityMainBinding

    private suspend fun client(addr: String, port: Int) {
        try {
            //val connection = Socket(addr, port)
            val connection = Socket()
            connection.connect(InetSocketAddress(addr,port), 3*1000)

            val writer = connection.getOutputStream()
            writer.write(1)
            val reader = Scanner(connection.getInputStream())
            while (active) {
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

                delay(100)
            }
            reader.close()
            writer.close()
            connection.close()
        } catch (e: Exception) {
            Log.e(this.toString(), e.message)
        }
    }

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.circleButton.listener = this

        binding.buttonMonitorOff.setOnClickListener {
            if (it is TextView)
                animateColor(it)
            presenter.onMonitorOffPressed()
        }
        binding.buttonMonitorOn.setOnClickListener {
            if (it is TextView)
                animateColor(it)
            presenter.onMonitorOnPressed()
        }
        binding.buttonIp.setOnClickListener {
//            if (it is TextView)
//                animateColor(it)
            presenter.onIpPressed()
            //active = true
            //CoroutineScope(IO).launch { client("192.168.0.101", 8850) }
        }

        supportActionBar?.hide()

        binding.seekBarVolume.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                outputData = "  v:$progress;"
                presenter.sendCommand(outputData)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                outputData = "  v:" + seekBar?.progress.toString() + ";"
                presenter.sendCommand(outputData)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                outputData = "  v:" + seekBar?.progress.toString() + ";"
                presenter.sendCommand(outputData)
            }
        }

        )


        //CoroutineScope(IO).launch { makeServiceCall() }
        //GlobalScope.launch {  }
//        val data : String = "some data"
//        Thread {
//
//            val client = Socket("192.168.0.101", 8850)
//            val output = PrintWriter(client.getOutputStream(), true)
//            val input = BufferedReader(InputStreamReader(client.inputStream))
//            output.print("hello")
//            Log.e("debug","Client sending [Hello]")
//            output.println()
//            (0..50).asFlow().onEach { it ->
//                output.println(it)
//                delay(100)
//            }
//            while (true){
//
//                Log.e("debug","Client receiving [${input.readLine()}]")
//
//            }
//            client.close()
//
//
//            Log.e("tag","socket is closed")
//        }.start()


    }

    fun animateColor(view: TextView) {
        val colorAnim: ObjectAnimator = ObjectAnimator.ofInt(
            view, "textColor",
            Color.parseColor("#F44336"), Color.WHITE
        )
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.start()
    }

    override fun setConnectedStatus(state: ConnectButtonStates) {
        Log.e(this.toString(),"setConnectedStatus ")
        binding.buttonConnectedStatus.setStatus(state)
    }

    override fun leftButtonPressed() {
        presenter.onLeftPressed()
    }

    override fun centerButtonPressed() {
        presenter.onCenterPressed()
    }

    override fun rightButtonPressed() {
        presenter.onRightPressed()
    }
}