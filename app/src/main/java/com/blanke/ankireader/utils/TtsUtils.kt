package com.blanke.ankireader.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import java.util.*

class TtsUtils(context: Context) {
    private val context: Context = context.applicationContext
    val tts: TextToSpeech = TextToSpeech(this.context, OnInitListener { i ->
        onInit(i)
    })


    private fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setPitch(1.0f) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            tts.setSpeechRate(1.0f)
        }
    }


    fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    fun isSpeaking(): Boolean {
        return tts.isSpeaking
    }

    companion object {
        private var singleton: TtsUtils? = null
        fun getInstance(context: Context): TtsUtils {
            synchronized(TtsUtils::class.java) {
                if (singleton == null) {
                    singleton = TtsUtils(context)
                }
                return singleton as TtsUtils
            }
        }
    }
}
