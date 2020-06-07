package com.blanke.ankireader.play

import android.app.Service
import com.blanke.ankireader.bean.Note

/**
 * Created by blanke on 2017/5/14.
 */
open class BasePlayHelper(
    var service:Service
) {
    @Throws(Exception::class)
    open fun play(note: Note) {
    }

    open fun pause() {
    }

    open fun stop() {
    }

    open fun reset() {
    }

    open fun destroy() {
    }

}
