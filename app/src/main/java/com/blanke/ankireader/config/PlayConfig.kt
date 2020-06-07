package com.blanke.ankireader.config

import android.content.Context
import android.preference.PreferenceManager
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.blanke.ankireader.Config
import com.blanke.ankireader.R

data class PlayConfig(
    var commonTextSize: Int,
    var commonTextColor: Int,
    var commonTextBackgroundColor: Int,
    var commonTextLength: Int,
    var commonTextGravity: Int,
    var commonTextClickStop: Boolean,
    var playSwitch: Boolean,
    var playLoopCount: Int,
    var playIntervalTime: Int,
    var playMode: PlayMode,
    var playReverse: Boolean,
    var notificationSwitch: Boolean,
    var ttsSwitch: Boolean,
    var ttsUseAll: Boolean,
    var ttsUseFront: Boolean,
    var ttsUseBack: Boolean,
    var playDeckIds: MutableList<Long>
) {

    enum class PlayMode {
        Loop, Random
    }

    companion object {
        fun loadConfig(context: Context): PlayConfig {
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val commonTextSize = preferences.getInt(
                context.getString(R.string.key_common_textsize),
                Config.FONT_SIZE
            )
            val commonTextColor = preferences.getInt(
                context.getString(R.string.key_common_textcolor),
                ContextCompat.getColor(context, R.color.defaultCommonTextColor)
            )
            val commonTextBackgroundColor = preferences
                .getInt(
                    context.getString(R.string.key_common_background),
                    ContextCompat.getColor(context, R.color.defaultCommonBackgroundColor)
                )
            val commonTextLength =
                preferences.getInt(context.getString(R.string.key_common_text_maxlength), 50)

            var commonTextGravity = Gravity.CENTER
            when (preferences.getString(context.getString(R.string.key_common_text_gravity), "0")) {
                "1" -> commonTextGravity = Gravity.START
                "2" -> commonTextGravity = Gravity.END
            }

            val commonTextClickStop =
                preferences.getBoolean(context.getString(R.string.key_common_click_stop), true)
            val playSwitch =
                preferences.getBoolean(context.getString(R.string.key_play_switch), true)
            val playLoopCount =
                preferences.getInt(context.getString(R.string.key_play_loop_count), 3)
            val playIntervalTime =
                preferences.getInt(context.getString(R.string.key_play_interval_time), 800)
            val playMode = if (preferences.getString(
                    context.getString(R.string.key_random_play),
                    "0"
                ) == "1"
            ) PlayMode.Random else PlayMode.Loop
            val playReverse =
                preferences.getBoolean(context.getString(R.string.key_play_reverse), false)
            val notificationSwitch =
                preferences.getBoolean(context.getString(R.string.key_notification_switch), true)

            //deckIds
            var playDeckIds =
                preferences.getString(context.getString(R.string.key_play_deck_ids), "")

            val playDeckIdsList = if (playDeckIds!!.isNotBlank()) {
                playDeckIds.split(",".toRegex()).map { it.toLong() }
                    .toMutableList()
            } else {
                mutableListOf<Long>()
            }

            val ttsSwitch =
                preferences.getBoolean(context.getString(R.string.key_tts_switch), false)
            val ttsUseAll =
                preferences.getBoolean(context.getString(R.string.key_tts_use_all), false)
            val ttsUseFront =
                preferences.getBoolean(context.getString(R.string.key_tts_front_switch), false)
            val ttsUseBack =
                preferences.getBoolean(context.getString(R.string.key_tts_back_switch), false)



            return PlayConfig(
                commonTextSize,
                commonTextColor,
                commonTextBackgroundColor,
                commonTextLength,
                commonTextGravity,
                commonTextClickStop,
                playSwitch,
                playLoopCount,
                playIntervalTime,
                playMode,
                playReverse,
                notificationSwitch,
                ttsSwitch,
                ttsUseAll,
                ttsUseFront,
                ttsUseBack,
                playDeckIdsList
            )
        }

        fun saveDeckIds(context: Context, vararg deckIds: Long) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val sb = StringBuilder()
            if (deckIds != null) {
                for (deckId in deckIds) {
                    sb.append("$deckId,")
                }
            }
            val edit = preferences.edit()
            edit.putString(context.getString(R.string.key_play_deck_ids), sb.toString())
            edit.apply()
        }
    }
}
