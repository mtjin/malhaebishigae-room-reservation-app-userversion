package com.mtjin.free_room.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(RESERVATION_APP, Context.MODE_PRIVATE)

    var registerInput: String
        get() = sharedPref.getString(REGISTER_INPUT_KEY, "").toString()
        set(value) {
            val editor = sharedPref.edit()
            editor.putString(REGISTER_INPUT_KEY, value)
            editor.apply()
        }

    var contentInput: String
        get() = sharedPref.getString(CONTENT_INPUT_KEY, "").toString()
        set(value) {
            val editor = sharedPref.edit()
            editor.putString(CONTENT_INPUT_KEY, value)
            editor.apply()
        }

    var alarmSetting: Boolean
        get() = sharedPref.getBoolean(ALARM_SETTING_KEY, true)
        set(value) {
            val editor = sharedPref.edit()
            editor.putBoolean(ALARM_SETTING_KEY, value)
            editor.apply()
        }

    var businessCode: String
        get() = sharedPref.getString(BUSINESS_CODE_KEY, BUSINESS_CODE).toString()
        set(value) {
            val editor = sharedPref.edit()
            editor.putString(BUSINESS_CODE_KEY, value)
            editor.commit()
        }

    companion object {
        private const val RESERVATION_APP = "RESERVATION_APP"
        private const val REGISTER_INPUT_KEY = "REGISTER_INPUT_KEY"
        private const val CONTENT_INPUT_KEY = "CONTENT_INPUT_KEY"
        private const val ALARM_SETTING_KEY = "ALARM_SETTING_KEY"
        private const val BUSINESS_CODE_KEY = "BUSINESS_CODE_KEY"
    }
}