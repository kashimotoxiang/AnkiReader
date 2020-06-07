package com.blanke.ankireader.utils


import android.text.Html


/**
 * Created by blanke on 2017/6/11.
 */
object HtmlUtils {
    fun removeAllTags(htmltext: String): String {
        return Html.fromHtml(htmltext).toString().replace("\n".toRegex(), "").trim { it <= ' ' }
    }

    fun removeNoBrTags(htmltext: String): String {
        return Html.fromHtml(htmltext).toString().trim { it <= ' ' }
    }
}
