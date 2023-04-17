package com.example.myapplication.demo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import coil.ImageLoader
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.util.CoilUtils
import com.example.myapplication.BuildConfig
import com.example.myapplication.common.data.database.AppDatabase
import com.example.myapplication.common.data.database.daos.AppDao
import com.example.myapplication.common.data.prefs.SharedPref
import com.google.gson.Gson
import com.example.myapplication.common.multilanguage.LocaleManager
import okhttp3.OkHttpClient
import timber.log.Timber

class App : Application() {

    private lateinit var pref: SharedPref
    private lateinit var dao: AppDao

    companion object {
        private lateinit var mInstance: App

        @Synchronized
        fun getInstance(): App = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        if (BuildConfig.DEBUG) Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
            }
        })

        pref = SharedPref(this, Gson())
        dao = AppDatabase.getInstance(applicationContext).getDao()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(if (base != null) LocaleManager.setLocale(base) else base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    fun getPref() = pref

    fun getDao() = dao
}