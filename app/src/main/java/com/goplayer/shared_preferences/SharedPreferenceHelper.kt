package com.goplayer.shared_preferences

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by user on 9/15/2017.
 */
class SharedPreferenceHelper {

    companion object {

        private val context: Context? = null

        fun putString(mContext: Context, key: String, value: String) {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            val edit = preferences.edit()
            edit.putString(key, value)
            edit.apply()
        }

        fun getString(mContext: Context, key: String, _default: String): String? {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            return preferences.getString(key, _default)
        }

        fun putInt(mContext: Context, key: String, value: Int) {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            val edit = preferences.edit()
            edit.putInt(key, value)
            edit.apply()
        }

        fun getInt(mContext: Context, key: String, _default: Int): Int {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            return preferences.getInt(key, _default)
        }

        fun putBoolean(mContext: Context, key: String, value: Boolean) {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            val edit = preferences.edit()
            edit.putBoolean(key, value)
            edit.apply()
        }

        fun getBoolean(mContext: Context, key: String, _default: Boolean): Boolean {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            return preferences.getBoolean(key, _default)
        }

        fun clearAllPreferences(mContext: Context) {
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext)
            val edit = preferences.edit()
            edit.clear()
            edit.apply()
        }
    }
}