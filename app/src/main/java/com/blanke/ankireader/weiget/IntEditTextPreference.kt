package com.blanke.ankireader.weiget

import android.content.Context
import android.preference.EditTextPreference


import android.util.AttributeSet


class IntEditTextPreference : EditTextPreference {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun getPersistedString(defaultReturnValue: String): String {
        return getPersistedInt(-1).toString()
    }

    override fun persistString(value: String): Boolean {
        return persistInt(Integer.valueOf(value))
    }
}
