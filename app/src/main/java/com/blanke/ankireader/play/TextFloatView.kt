package com.blanke.ankireader.play


import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import com.blanke.ankireader.bean.Note
import com.blanke.ankireader.config.PlayConfig
import com.blanke.ankireader.event.StopPlayEvent
import org.greenrobot.eventbus.EventBus


class TextFloatView(context: Context, private var playConfig: PlayConfig) :
    AppCompatTextView(context) {
    private fun init(playConfig: PlayConfig) {
        setTextColor(playConfig.commonTextColor)
        setBackgroundColor(playConfig.commonTextBackgroundColor)
        textSize = playConfig.commonTextSize.toFloat()
        gravity = playConfig.commonTextGravity
        minWidth = 300
        minHeight = 100
        setPadding(15, 5, 15, 5)
    }

    fun setNote(note: Note) {
        post {
            val maxLength = playConfig.commonTextLength
            text = if (note.getFullContent().length > maxLength) {
                note.getFullContent().subSequence(0, maxLength).toString() + "..."
            } else {
                note.getFullContent()
            }
        }
    }

    private fun onDoubleClick() {
        if (!playConfig.commonTextClickStop) {
            return
        }
        EventBus.getDefault().post(StopPlayEvent())
    }

    fun initView(windowManager: WindowManager, params: WindowManager.LayoutParams) {
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        windowManager.updateViewLayout(this@TextFloatView, params)
        setOnTouchListener(object : OnTouchListener {
            var downX = 0f
            var downY = 0f
            var paramX = 0
            var paramY = 0
            var downCount = 0
            var lastDownTime: Long = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    downX = event.rawX
                    downY = event.rawY
                    paramX = params.x
                    paramY = params.y
                    downCount++
                    val nowTime = System.currentTimeMillis()
                    if (downCount == 1) {
                        lastDownTime = nowTime
                    } else if (downCount == 2) { //双击事件
                        onDoubleClick()
                        if (nowTime - lastDownTime < 600) {
                            lastDownTime = 0
                            downCount = 0
                        } else {
                            downCount = 1
                            lastDownTime = nowTime
                        }
                    }
                } else if (event.action == MotionEvent.ACTION_MOVE) {
                    val nowX = event.rawX
                    val nowY = event.rawY
                    params.x = (paramX + nowX - downX) as Int
                    params.y = (paramY + nowY - downY) as Int
                    windowManager.updateViewLayout(this@TextFloatView, params)
                }
                return true
            }
        })
    }
}
