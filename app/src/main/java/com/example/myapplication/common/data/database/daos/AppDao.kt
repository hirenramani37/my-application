package com.example.myapplication.common.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.common.data.database.entities.UserLocal


@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMTUser(vararg items: UserLocal):List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUser(item: List<UserLocal?>?)

    @Update
    suspend fun updateMTUser(item: UserLocal)

    @Query("DELETE FROM UserLocal")
    suspend fun deleteMTUser()

    @Query("SELECT * FROM UserLocal")
    fun getMTUser(): LiveData<List<UserLocal>>
}