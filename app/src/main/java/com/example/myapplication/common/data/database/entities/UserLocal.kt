package com.example.myapplication.common.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserLocal {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    var displayAlias: String? = null
    var displayPic: String? = null
}