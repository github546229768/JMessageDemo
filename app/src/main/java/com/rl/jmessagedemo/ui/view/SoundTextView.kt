package com.rl.jmessagedemo.ui.view

import android.app.Dialog
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.rl.jmessagedemo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream


/**

 * @Auther: 杨景

 * @datetime: 2021/8/9

 * @desc:

 */

class SoundTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    companion object {
        const val DOWN_TALK_STATUS = 1
        const val UP_SEND_STATUS = 2
        const val CANCEL_SEND_STATUS = 3

        const val SHORTEST_TIME_VOICE = 800
    }

    private var currentStatus = 0 //当前状态

    private lateinit var dialog: Dialog
    private lateinit var status: TextView
    private lateinit var chronometer: Chronometer
    private var currentTimeMillis = 0L
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var voiceFile: File
    private var onMessage: OnMessage? = null

    interface OnMessage {
        fun sendVoiceMessage(voiceFile: File, voiceTime: Int)
    }

    fun setOnMessage(onMessage: OnMessage) {
        this.onMessage = onMessage
    }

    init {
        initView()
    }

    private fun initView() {
        val inflate = View.inflate(context, R.layout.jmui_dialog_record_voice, null)
        chronometer = inflate.findViewById(R.id.voice_time)
        status = inflate.findViewById(R.id.status)
        dialog = Dialog(context).apply {
            setContentView(inflate)
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    currentTimeMillis = System.currentTimeMillis()
                    chronometer.apply {
                        base = SystemClock.elapsedRealtime()
                        start()
                    }
                    startRecording()
                    dialog.show()
                    currentStatus = UP_SEND_STATUS
                }
                MotionEvent.ACTION_MOVE -> {
                    currentStatus = if (y < -100) {
                        CANCEL_SEND_STATUS
                    } else {
                        UP_SEND_STATUS
                    }
                }
                MotionEvent.ACTION_UP -> {
                    dialog.hide()
                    releaseRecorder()
                    if (currentStatus == UP_SEND_STATUS) {
                        if (System.currentTimeMillis() - currentTimeMillis < SHORTEST_TIME_VOICE) {
                            ToastUtils.showShort("录音时间太短")
                        } else if (System.currentTimeMillis() - currentTimeMillis < 60000) {//短语音 只能小于一分钟
                            CoroutineScope(Dispatchers.IO).launch {
                                val voiceTime = getVoiceTime()
                                if (voiceTime == 0) {
                                    withContext(Dispatchers.Main) {
                                        ToastUtils.showLong("若要使用语音功能，请打开录音权限")
                                    }
                                } else {
                                    onMessage?.sendVoiceMessage(voiceFile, voiceTime)
                                }
                            }
                        }
                    }
                    currentStatus = DOWN_TALK_STATUS
                }
            }
            updateStatus()
        }
        return true
    }

    private fun getVoiceTime(): Int {
        if (!voiceFile.exists()) {
            return 0
        }
        val fileInputStream = FileInputStream(voiceFile)
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(fileInputStream.fd)
            prepare()
        }
        val duration = mediaPlayer.duration / 1000
        return if (duration < 1) 1 else if (duration > 60) 60 else duration
    }

    private fun releaseRecorder() {
        mediaRecorder.stop()
        mediaRecorder.release()
    }

    private fun startRecording() {
        val dirFilePath = context.filesDir.absolutePath + "/voice"
        val dirFile = File(dirFilePath)
        dirFile.mkdir()
//        val filePath = "${dirFilePath}/${System.currentTimeMillis()}.amr"//内部
        val filePath = "${dirFilePath}/${System.currentTimeMillis()}.wav"//内部
        voiceFile = File(filePath)
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                    setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                    setOutputFile(voiceFile.absolutePath)
                    voiceFile.createNewFile()
                    prepare()
                    start()
                }
            }
        }
    }

    private fun updateStatus() {
        when (currentStatus) {
            DOWN_TALK_STATUS -> {
                status.text = "上滑取消"
                text = "按住说话"
            }
            UP_SEND_STATUS -> {
                status.text = "上滑取消"
                text = "松开发送"
            }
            CANCEL_SEND_STATUS -> {
                status.text = "松开取消发送"
                text = "松开取消发送"
            }
        }
    }

}