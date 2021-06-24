package com.muramsyah.evotingapp.sf

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreference(context: Context) {

    companion object {
        private const val PREFS_NAME = "e_vote_pref"
        private const val IS_LOGIN = "isLogin"
        private const val NAME = "name"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isLogin: Boolean?
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(value) {
            prefs.edit {
                putBoolean(IS_LOGIN, value as Boolean)
            }
        }

    var name: String?
        get() = prefs.getString(NAME, "LoremIpsum")
        set(value) {
            prefs.edit {
                putString(NAME, value)
            }
        }

}