package com.blanke.ankireader.bean

import android.text.Html
import java.util.*

/**
 * Created by on 2016/10/14.
 */
data class Note(
    val id: Long,
    val tags: String,
    val front: CharSequence,
    val back: CharSequence,
    var mediaPaths: MutableList<String>
) {
    fun addMediaPath(path: String) {
        mediaPaths.add(path)
    }

    fun getPrimaryMediaPath(): String? {
        return if (mediaPaths.size > 0) mediaPaths[0] else null
    }

    fun getFullContent(): CharSequence {
        return Html.fromHtml("$front<br/>$back")
    }

    override fun toString(): String {
        return "Note{" +
                "back='" + back + '\'' +
                ", id=" + id +
                ", tags='" + tags + '\'' +
                ", front='" + front + '\'' +
                ", mediaPath='" + mediaPaths + '\'' +
                '}'
    }


    init {
        mediaPaths = ArrayList()
    }
}
