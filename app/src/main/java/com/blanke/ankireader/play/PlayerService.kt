package com.blanke.ankireader.play

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import com.blanke.ankireader.bean.Note
import com.blanke.ankireader.config.PlayConfig
import com.blanke.ankireader.config.PlayConfig.PlayMode
import com.blanke.ankireader.data.AnkiManager
import com.blanke.ankireader.event.PausePlayEvent
import com.blanke.ankireader.event.StartPlayEvent
import com.blanke.ankireader.event.StopPlayEvent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class PlayerService : Service() {
    enum class PlayState {
        NORMAL, PLAYING, PAUSED
    }

    private lateinit var mPlayConfig: PlayConfig
    private var playHelpersConsumer: MutableList<BasePlayHelper> =
        mutableListOf() //按照顺序进行消费
    private var currentState: PlayState = PlayState.NORMAL
    private lateinit var musicPlayHelper: MusicPlayHelper
    private lateinit var notificationPlayHelper: NotificationPlayHelper
    private lateinit var floatTextPlayHelper: FloatTextPlayHelper
    private lateinit var handler: Handler
    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        EventBus.getDefault().register(this)
        running = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action //播放信息
        if (action != null) {
            if (action == ACTION_TOGGLE_PLAY_PAUSE) { //toggle
                toggle()
            } else if (action == ACTION_EXIT) {
                this.stopSelf()
            }
        } else { //first init
            mPlayConfig = PlayConfig.loadConfig(this)
            destroyPlayHelpers()
            initPlayHelpers()
            play()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initPlayHelpers() {
        playHelpersConsumer = ArrayList()
        if (mPlayConfig.notificationSwitch) {
            notificationPlayHelper = NotificationPlayHelper.getInstance(this)
            playHelpersConsumer.add(notificationPlayHelper)
        }

        if (mPlayConfig.playSwitch) {
            musicPlayHelper = MusicPlayHelper.getInstance(mPlayConfig, this)
            playHelpersConsumer.add(musicPlayHelper)
        }

        floatTextPlayHelper = FloatTextPlayHelper.getInstance(this)
        val floatView = TextFloatView(this, mPlayConfig)
        floatTextPlayHelper.setFloatView(floatView)
        playHelpersConsumer.add(floatTextPlayHelper)
    }

    private fun toggle() {
        if (currentState == PlayState.PLAYING) {
            pause()
        } else {
            play()
        }
    }

    @SuppressLint("CheckResult")
    private fun play() {
        if (currentState == PlayState.PLAYING) {
            return
        }

        currentState = PlayState.PLAYING
        Observable.create(object : ObservableOnSubscribe<Note> {
            @Throws(Exception::class)
            override fun subscribe(e: ObservableEmitter<Note>) {
                var notes: MutableList<Note> = if (mPlayConfig.ttsSwitch) { //读取所有
                    AnkiManager.getNotesByDeckId(mPlayConfig.playDeckIds)
                } else { //读取只有 anki 音频的
                    AnkiManager.getAllHasMediaNotesByDeckId(mPlayConfig.playDeckIds)
                } as MutableList<Note>
                require(notes.size != 0) { "牌组识别为空!" }
                EventBus.getDefault().post(StartPlayEvent())
                var i = getNextIndex(
                    if (mPlayConfig.playReverse) notes.size else -1,
                    notes.size
                )
                while (currentState == PlayState.PLAYING) {
                    for (j in 0 until mPlayConfig.playLoopCount) { //循环 x 次
                        val note = notes[i]
                        e.onNext(note)
                        Thread.sleep(mPlayConfig.playIntervalTime.toLong())
                    }
                    i = getNextIndex(i, notes.size)
                }
            }

            private fun getNextIndex(i: Int, count: Int): Int {
                if (mPlayConfig.playMode == PlayMode.Loop) { //循环
                    return if (mPlayConfig.playReverse) {
                        (i - 1) % count
                    } else (i + 1) % count
                } else if (mPlayConfig.playMode == PlayMode.Random) { //随机
                    return getRandomIndex(count)
                }
                return (i + 1) % count
            }
        }).subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.trampoline()) //在上一个默认线程 newThread run
            .subscribe({ note -> consumPlayHelper(note) }) { throwable ->
                throwable.printStackTrace()
                stopPlayHelpers()
                handler.post(Runnable {
                    val trace = throwable.stackTrace[0]
                    Toast.makeText(
                        this@PlayerService, """
     发生了一个错误,错误详情(可以截图反馈到酷安):
     ${throwable.message}:
     ${trace.className}:${trace.lineNumber}
     """.trimIndent(),
                        Toast.LENGTH_LONG
                    ).show()
                })
                stopSelf()
            }
    }

    private fun pause() {
        currentState = PlayState.PAUSED
        pausePlayHelpers()
    }

    @Subscribe
    fun onEventStop(event: StopPlayEvent) {
        stopSelf()
    }

    @Subscribe
    fun onEventPause(event: PausePlayEvent) {
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentState = PlayState.NORMAL
        destroyPlayHelpers()
        EventBus.getDefault().unregister(this)
        running = false
        EventBus.getDefault().post(StopPlayEvent())
    }

    @Throws(Exception::class)
    private fun consumPlayHelper(note: Note) {
        for (basePlayHelper in playHelpersConsumer) {
            basePlayHelper.play(note)
        }
    }

    private fun destroyPlayHelpers() {
        for (basePlayHelper in playHelpersConsumer) {
            basePlayHelper.destroy()
        }
    }

    private fun pausePlayHelpers() {
        for (basePlayHelper in playHelpersConsumer) {
            basePlayHelper.pause()
        }
    }

    private fun stopPlayHelpers() {
        for (basePlayHelper in playHelpersConsumer) {
            basePlayHelper.stop()
        }
    }

    fun getCurrentState(): PlayState {
        return currentState
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getRandomIndex(end: Int): Int {
        return Random().nextInt(end)
    }

    companion object {
        const val ACTION_TOGGLE_PLAY_PAUSE: String = "1"
        const val ACTION_EXIT: String = "2"
        private var running = false
        fun isRunning(): Boolean {
            return running
        }
    }
}
