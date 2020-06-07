package com.blanke.ankireader.play

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent


import android.text.Html
import androidx.core.app.NotificationCompat
import com.blanke.ankireader.R
import com.blanke.ankireader.bean.Note
import com.blanke.ankireader.play.PlayerService.PlayState
import com.blanke.ankireader.ui.MainActivity


/**
 * 通知栏播放
 * Created by blanke on 2017/5/14.
 */
class NotificationPlayHelper(service: Service) : BasePlayHelper(service) {
    private var lastNode: Note? = null
    private fun initNotification() {}

    @Throws(Exception::class)
    override fun play(note: Note) {
        lastNode = note
        service.startForeground(
            NOTIFICATION_ID,
            getNotificationText(note.front, note.back)
        )
    }

    override fun pause() {
        try {
            lastNode?.let { play(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {}
    override fun reset() {}
    override fun destroy() {
    }

    private fun getNotificationText(title: CharSequence, content: CharSequence): Notification? {
        if (service == null) {
            return null
        }
        val builder = NotificationCompat.Builder(service)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_music)
        val style = NotificationCompat.BigTextStyle()
        style.bigText(Html.fromHtml(content.toString()))
        style.setBigContentTitle(title)
        builder.setStyle(style)
        builder.setAutoCancel(false)
        builder.setOngoing(true)
        builder.setShowWhen(false)
        val mPlayPauseIntent = getIntent(PlayerService.ACTION_TOGGLE_PLAY_PAUSE)
        var playPauseIcon = R.drawable.ic_play
        var info = "开始"
        val playerService = service as PlayerService
        if (playerService.getCurrentState()
            == PlayState.PLAYING
        ) {
            playPauseIcon = R.drawable.ic_pause
            info = "暂停"
        }
        val stopText = "停止"
        val mStopIntent = getIntent(PlayerService.ACTION_EXIT)
        builder.addAction(playPauseIcon, info, mPlayPauseIntent)
        builder.addAction(R.drawable.ic_stop, stopText, mStopIntent)
        builder.setContentIntent(getContentIntent())
        return builder.build()
    }

    private fun getContentIntent(): PendingIntent {
        val contentIntent = Intent(service, MainActivity::class.java)
        return PendingIntent.getActivity(service, 0, contentIntent, 0)
    }

    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(service, PlayerService::class.java)
        intent.action = action
        return PendingIntent.getService(service, 0, intent, 0)
    }

    companion object {
        private const val NOTIFICATION_ID = 888
        fun getInstance(service: Service): NotificationPlayHelper {
            return NotificationPlayHelper(service)
        }
    }

    init {
        initNotification()
    }
}
