package com.kazumaproject.eyetracker.util

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "dlib-for-android"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)
    private val CURRENT_MODE = Pair("current_mode",0)
    private val CURRENT_COLOR_MODE = Pair("current_color_mode",0)
    private val CURRENT_CAMERA_SELECTOR = Pair("current_camera_selector",false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        get() = preferences.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)

        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

    var currentMode: Int
        get() = preferences.getInt(CURRENT_MODE.first, CURRENT_MODE.second)

    set(value) = preferences.edit {
        it.putInt(CURRENT_MODE.first, value)
    }

    var currentColorMode: Int
        get() = preferences.getInt(CURRENT_COLOR_MODE.first, CURRENT_COLOR_MODE.second)

        set(value) = preferences.edit {
            it.putInt(CURRENT_COLOR_MODE.first, value)
        }

    var currentCameraState: Boolean
        get() = preferences.getBoolean(CURRENT_CAMERA_SELECTOR.first, CURRENT_CAMERA_SELECTOR.second)

        set(value) = preferences.edit {
            it.putBoolean(CURRENT_CAMERA_SELECTOR.first, value)
        }

}