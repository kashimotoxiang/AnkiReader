package com.blanke.ankireader.play

import android.app.Service
import android.media.MediaPlayer


import com.blanke.ankireader.bean.Note
import com.blanke.ankireader.config.PlayConfig
import com.blanke.ankireader.utils.HtmlUtils
import com.blanke.ankireader.utils.TtsUtils

import java.io.IOException

/**
 * Created by blanke on 2017/5/14.
 */
class MusicPlayHelper(
    private val playConfig: PlayConfig,
    service: Service
) : BasePlayHelper(service) {
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TtsUtils = TtsUtils.getInstance(service)

    @Throws(Exception::class)
    override fun play(note: Note) {
        if (playConfig.ttsUseAll or note.mediaPaths.isEmpty()) {
            playTTS(note)
        } else {
            playLocalMusic(note)
        }
    }

    //播放 tts
    @Throws(InterruptedException::class)
    private fun playTTS(note: Note) {
        if (playConfig.ttsUseFront) {
            playTTSReal(HtmlUtils.removeAllTags(note.front.toString()))
        }
        if (playConfig.ttsUseBack) {
            playTTSReal(HtmlUtils.removeAllTags(note.back.toString()))
        }
    }

    //播放 anki mp3
    @Throws(Exception::class)
    private fun playLocalMusic(note: Note) {
        playMusicReal(note)
        while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            Thread.sleep(SLEEP_TIME)
        }
    }

    @Throws(IOException::class)
    private fun playMusicReal(note: Note) {
        if (mediaPlayer == null) {
            return
        }
        mediaPlayer!!.reset() // 把各项参数恢复到初始状态
        mediaPlayer!!.setDataSource(note.getPrimaryMediaPath())
        mediaPlayer!!.prepare() // 进行缓冲
        mediaPlayer!!.start()
    }

    @Throws(InterruptedException::class)
    private fun playTTSReal(text: String) {
        if (tts == null) {
            return
        }
        tts.speakText(text)
        while (tts != null && tts.isSpeaking()) {
            Thread.sleep(SLEEP_TIME)
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        tts.tts.stop()
    }

    override fun stop() {
        mediaPlayer?.stop()
        tts.tts.stop()
    }

    override fun reset() {
        mediaPlayer?.reset()
    }

    override fun destroy() {
        stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val SLEEP_TIME: Long = 400
        fun getInstance(playConfig: PlayConfig, service: Service): MusicPlayHelper {
            return MusicPlayHelper(playConfig, service)
        }
    }

    init {
        mediaPlayer = MediaPlayer()
    }
}

