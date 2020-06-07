package com.blanke.ankireader.play


import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.blanke.ankireader.bean.Note

class FloatTextPlayHelper(service: Service) : BasePlayHelper(service) {
    private var windowManager: WindowManager? = null
    private var floatView: TextFloatView? = null
    private var isViewAdded = false
    fun setFloatView(floatView: TextFloatView) {
        this.floatView = floatView
        addFloatView()
    }

    private fun initPermission(): Boolean {
        if (!Settings.canDrawOverlays(service)) {
            Toast.makeText(
                service,
                "请在设置中授予悬浮窗权限，重新播放",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            service.startActivity(intent)
            return false
        }
        return true
    }

    private fun addFloatView(): Boolean {
        if ((floatView == null) or (!initPermission())) {
            return false
        }
        windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = floatView as View
        val params = WindowManager.LayoutParams()
        params.type = WindowManager.LayoutParams.TYPE_PHONE
        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.x = 0
        params.y = 0
        windowManager!!.addView(view, params)
        floatView!!.initView(windowManager!!, params)
        isViewAdded = true
        return true
    }

    @Throws(Exception::class)
    override fun play(note: Note) {
        if (service != null) {
            if (isViewAdded) {
                floatView?.setNote(note)
            } else {
                addFloatView()
            }
        }
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun reset() {
    }

    override fun destroy() {
        if (floatView != null) {
            windowManager?.removeView(floatView as View)
            floatView = null
        }
    }


    companion object {
        private const val NOTIFICATION_ID = 888
        fun getInstance(service: Service): FloatTextPlayHelper {
            return FloatTextPlayHelper(
                service
            )
        }
    }

    init {
        initPermission()
    }
}
