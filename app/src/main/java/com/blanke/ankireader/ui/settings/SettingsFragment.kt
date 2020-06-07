package com.blanke.ankireader.ui.settings


import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.blanke.ankireader.R


/**
 * Created by blanke on 2017/6/8.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    private var commonScreen: PreferenceScreen? = null
    private var commonTextSize: EditTextPreference? = null
    private var playMode: ListPreference? = null
    private var playReverse: EditTextPreference? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
        commonScreen = findPreference<PreferenceScreen>(getString(R.string.key_float_mode_common))
        commonTextSize = findPreference<EditTextPreference>(getString(R.string.key_common_textsize))
        playMode = findPreference<ListPreference>(getString(R.string.key_random_play))
        playReverse = findPreference<EditTextPreference>(getString(R.string.key_play_reverse))
    }
}
