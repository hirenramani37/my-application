package com.example.myapplication.demo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.example.myapplication.common.data.database.entities.UserLocal
import com.example.myapplication.common.data.network.model.data_class_exmple
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel : BaseViewModel() {

    private val _userInfo = SingleLiveEvent<List<data_class_exmple.data_class_exmpleItem>>()
    val userInfo: LiveData<List<data_class_exmple.data_class_exmpleItem>> = _userInfo

    private val _userInfoError = MutableLiveData<Throwable>()
    val userInfoError: LiveData<Throwable> = _userInfoError

    var localUser: LiveData<List<UserLocal>>? = null

    init {
        localUser = dao.getMTUser()
    }

    fun callApi() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.getUsers(), onError = {
                _userInfoError.postValue(it)
            }) {
                _userInfo.postValue(it)
            }
        }
    }

    fun insertUser(userLocal: UserLocal) {
        viewModelScope.launch {
            val insertedUser = dao.insertMTUser(userLocal)
            Timber.e("insertedUser : ${insertedUser.size}")
        }
    }

}