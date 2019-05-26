package com.learn.smackandroid.Controller

import android.app.Application
import com.learn.smackandroid.Utilities.SharedPrefs

class App :Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }

}