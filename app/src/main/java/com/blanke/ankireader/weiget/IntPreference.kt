package com.blanke.ankireader.weiget

import android.content.Context
import android.preference.Preference


import android.util.AttributeSet


class IntPreference : Preference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun setValue(value: Int) {
        persistInt(value)
    }

    fun getValue(defaultValue: Int): Int {
        return getPersistedInt(defaultValue)
    }
}
