package com.example.project0719

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    private var sharedPreferences: SharedPreferences? = null

    private const val FILE = "config_file"
    const val IS_ADMIN = "is_admin"
    private val LOCK = Any()

    fun get(context: Context, key: String): Boolean {
        synchronized(LOCK) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            }
            return sharedPreferences!!.getBoolean(key, false)
        }
    }

    fun put(context: Context, key: String, value: Boolean) {
        synchronized(LOCK) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            }
            sharedPreferences!!.edit().putBoolean(key, value).apply()
        }
    }
}