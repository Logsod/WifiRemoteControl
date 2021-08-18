package com.losman.wifiremotecontrol

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.losman.wifiremotecontrol.databinding.ActivityMainBinding
import com.losman.wifiremotecontrol.presenter.MainPresenter
import com.losman.wifiremotecontrol.uiView.CircleButton
import com.losman.wifiremotecontrol.uiView.ConnectButtonStates
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider
import io.github.muddz.styleabletoast.StyleableToast





class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainView,
    CircleButton.OnButtonClickListener {

    private var active: Boolean = false
    private var data: String = ""
    private var outputData: String = ""
    lateinit var binding: ActivityMainBinding

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
            //showIpDialog()
            //active = true
            //CoroutineScope(IO).launch { client("192.168.0.101", 8850) }
        }

        supportActionBar?.hide()

        binding.seekBarVolume.progress = presenter.currentVolume
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
        })
    }

    override fun showMessage(message: String) {
        if (message.isEmpty()) return
        StyleableToast.Builder(this)
            .text(message)
            .textColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .backgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            .show()
    }


    override fun showIpDialog(ip: String, password: String) {

        val dialog = Dialog(this, R.style.ThemeDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.ip_dialog)
        val editTextIp = dialog.findViewById<AppCompatEditText>(R.id.edit_text_ip)
        val editTextPassword = dialog.findViewById<AppCompatEditText>(R.id.edit_text_password)
        editTextIp.setText(ip)
        editTextPassword.setText(password)
        val buttonConnect = dialog.findViewById<Button>(R.id.button_connect)
        buttonConnect.setOnClickListener {
            dialog.dismiss()
            presenter.onConnectPressed(
                editTextIp.text.toString().trim(),
                editTextPassword.text.toString().trim()
            )
        }
        dialog.show()
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