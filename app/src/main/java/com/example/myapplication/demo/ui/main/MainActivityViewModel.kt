package com.example.myapplication.demo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.base.BaseViewModel
import com.example.myapplication.common.base.SingleLiveEvent
import com.example.myapplication.common.data.database.entities.UserLocal
import com.example.myapplication.common.data.network.model.UserListResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel : BaseViewModel() {

    private val _userInfo = SingleLiveEvent<UserListResponse>()
    val userInfo: LiveData<UserListResponse> = _userInfo

    private val _userInfoError = MutableLiveData<Throwable>()
    val userInfoError: LiveData<Throwable> = _userInfoError

    var localUser: LiveData<List<UserLocal>>? = null

    init {
        localUser = dao.getMTUser()
    }

    fun callApi(page: Int,pageSize: Int) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.getUsers(page,pageSize),
                onError = {
                _userInfoError.postValue(it)
            }) {
                _userInfo.postValue(it)
            }
        }
    }

    fun insertUser(userLocal: UserLocal,data: UserListResponse.Data) {
        viewModelScope.launch {
           // Timber.e("insertedUser : ${userLocal?.size}")
            //data.forEach {
            //    if(userLocal.airId!=data._id){
                    val insertedUser =  dao.insertMTUser(userLocal)
                    Timber.e("insertedUser : ${insertedUser.size}")
                //}
            //}


        }
    }

    fun insertAll(item: List<UserLocal?>?){
        viewModelScope.launch {
            // Timber.e("insertedUser : ${userLocal?.size}")
            val insertedUser =  dao.insertAllUser(item)

           // Timber.e("insertedUser : ${insertedUser.size}")
        }
    }

    fun deleteUser(userLocal: UserLocal){
        viewModelScope.launch {
            val deletedUser = dao.deleteMTUser()
            Timber.e("deletedUser : ${deletedUser}")
        }

    }

}