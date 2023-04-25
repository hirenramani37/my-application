package com.example.myapplication.common.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserLocal {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    var displayname: String? = null
    var trips: Int? = null
    var airId: String? = null
}