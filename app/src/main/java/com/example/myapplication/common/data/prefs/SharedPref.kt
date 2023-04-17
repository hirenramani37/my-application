package com.example.myapplication.common.data.prefs

import android.content.Context
import com.example.myapplication.common.data.network.model.UserInfo
import com.google.gson.Gson

class SharedPref(context: Context, private val gson: Gson) : EncryptedPreferences(context) {

    private inline fun <reified T> toJson(value: T?) =
        if (value == null) null else gson.toJson(value)

    private inline fun <reified T> fromJson(value: String?) =
        if (value.isNullOrEmpty()) null else gson.fromJson(value, T::class.java)

    //<editor-fold desc="Clear App Data">
    fun clearAppUserData() {
        //TODO Clear your User Data here, when user logs out
    }
    //</editor-fold>

    var authToken: String?
        get() = getString(this::authToken.name)
        set(value) = setString(this::authToken.name, value)


    var userInfo: UserInfo?
        set(value) = setString(this::userInfo.name, toJson(value))
        get() = try {
            fromJson(getString(this::userInfo.name))
        } catch (e: Exception) {
            null
        }

    var mtUserId: String?
        get() = getString(this::mtUserId.name)
        set(value) = setString(this::mtUserId.name, value)

}