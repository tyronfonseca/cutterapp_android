package com.tf.clasificacioncutter.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum

class AppSettings (context: Context) : IAppSettings {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cutter_app_settings", Context.MODE_PRIVATE)

    // Keys
    private object Keys {
        const val DONT_SEPARATE_NAME = "dontSeparateName"
        const val INCLUDE_EXTRAS_IN_EXPORT = "includeExtrasInExport"
        const val TEXT_BEFORE_NUM = "textBeforeNum"
        const val CHARS_BEFORE_NUM = "charsBeforeNum"
        const val TEXT_AFTER_NUM = "textAfterNum"
        const val CHARS_AFTER_NUM = "charsAfterNum"
        const val IGNORE_ARTICLES = "ignoreArticles"
        const val USE_FORMAT_MAC = "useFormatMac"
    }

    // Properties with Compose State

    override var dontSeparateName: Boolean
        get() = _dontSeparateName
        set(value) {
            _dontSeparateName = value
            prefs.edit { putBoolean(Keys.DONT_SEPARATE_NAME, value) }
        }
    private var _dontSeparateName by mutableStateOf(
        prefs.getBoolean(Keys.DONT_SEPARATE_NAME, false)
    )

    override var ignoreArticles: Boolean
        get() = _ignoreArticles
        set(value) {
            _ignoreArticles = value
            prefs.edit { putBoolean(Keys.IGNORE_ARTICLES, value) }
        }
    private var _ignoreArticles by mutableStateOf(
        prefs.getBoolean(Keys.IGNORE_ARTICLES, true)
    )

    override var textBeforeNum: TextBeforeAfterNum
        get() = _textBeforeNum
        set(value) {
            _textBeforeNum = value
            prefs.edit().putInt(Keys.TEXT_BEFORE_NUM, value.value).apply()
        }
    private var _textBeforeNum by mutableStateOf(
        TextBeforeAfterNum.fromInt(
            prefs.getInt(Keys.TEXT_BEFORE_NUM, TextBeforeAfterNum.AUTHOR_SURNAME.value)
        )
    )

    override var charsBeforeNum: Int
        get() = maxOf(0, _charsBeforeNum)
        set(value) {
            val clampedValue = maxOf(0, value)
            _charsBeforeNum = clampedValue
            prefs.edit { putInt(Keys.CHARS_BEFORE_NUM, clampedValue) }
        }
    private var _charsBeforeNum by mutableIntStateOf(
        maxOf(0, prefs.getInt(Keys.CHARS_BEFORE_NUM, 1))
    )

    override var textAfterNum: TextBeforeAfterNum
        get() = _textAfterNum
        set(value) {
            _textAfterNum = value
            prefs.edit { putInt(Keys.TEXT_AFTER_NUM, value.value) }
        }
    private var _textAfterNum by mutableStateOf(
        TextBeforeAfterNum.fromInt(
            prefs.getInt(Keys.TEXT_AFTER_NUM, TextBeforeAfterNum.TITLE.value)
        )
    )

    override var charsAfterNum: Int
        get() = maxOf(0, _charsAfterNum)
        set(value) {
            val clampedValue = maxOf(0, value)
            _charsAfterNum = clampedValue
            prefs.edit { putInt(Keys.CHARS_AFTER_NUM, clampedValue) }
        }
    private var _charsAfterNum by mutableIntStateOf(
        maxOf(0, prefs.getInt(Keys.CHARS_AFTER_NUM, 0))
    )

    override var includeExtrasInExport: Boolean
        get() = _includeExtrasInExport
        set(value) {
            _includeExtrasInExport = value
            prefs.edit { putBoolean(Keys.INCLUDE_EXTRAS_IN_EXPORT, value) }
        }
    private var _includeExtrasInExport by mutableStateOf(
        prefs.getBoolean(Keys.INCLUDE_EXTRAS_IN_EXPORT, false)
    )

    override var useFormatMac: Boolean
        get() = _useFormatMac
        set(value) {
            _useFormatMac = value
            prefs.edit { putBoolean(Keys.USE_FORMAT_MAC, value) }
        }
    private var _useFormatMac by mutableStateOf(
        prefs.getBoolean(Keys.USE_FORMAT_MAC, true)
    )
}