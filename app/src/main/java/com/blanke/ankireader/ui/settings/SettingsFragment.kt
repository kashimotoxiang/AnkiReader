package com.blanke.ankireader.ui.settings


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.blanke.ankireader.R
import com.blanke.ankireader.weiget.IntPreference


/**
 * Created by blanke on 2017/6/8.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    //    private lateinit var choseMode: ListPreference
//    private lateinit var commonScreen: PreferenceScreen
    private lateinit var commonTextSize: IntPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
//
//        choseMode = findPreference(getString(R.string.key_float_mode)) as ListPreference
//        commonScreen = findPreference(getString(R.string.key_float_mode_common)) as PreferenceScreen
//        choseMode.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
//            val index = choseMode.findIndexOfValue(newValue.toString())
//            commonScreen.setEnabled(index == 1)
//            true
//        }
//        //mock change event
//        choseMode.getOnPreferenceChangeListener()
//            .onPreferenceChange(choseMode, choseMode.getValue())
//        //普通配置
//        commonTextSize = findPreference(getString(R.string.key_common_textsize)) as IntPreference
//        commonTextSize.setOnPreferenceClickListener(OnPreferenceClickListener {
//            val size = commonTextSize.getValue(Config.FONT_SIZE)
//            ChoseTextSizeDialog.show(
//                activity, R.string.text_common_textsize, size
//            ) { size -> commonTextSize.setValue(size) }
//            true
//        })
//        //播放设置
//        val playMode = findPreference(getString(R.string.key_play_mode)) as ListPreference
//        val playReverse = findPreference(getString(R.string.key_play_reverse))
//        playMode.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
//            val position = playMode.findIndexOfValue(newValue.toString())
//            playReverse.isEnabled = position == 0
//            true
//        }
//        playMode.onPreferenceChangeListener.onPreferenceChange(playMode, playMode.value)

    }
}
