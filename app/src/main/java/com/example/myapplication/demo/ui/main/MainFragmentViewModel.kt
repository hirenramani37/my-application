package com.example.myapplication.demo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.base.BaseViewModel
import com.example.myapplication.common.data.network.model.data_class_exmple
import kotlinx.coroutines.launch

class MainFragmentViewModel : BaseViewModel() {
    private val _mtUserInfo = MutableLiveData<List<data_class_exmple.data_class_exmpleItem>>()
    val userInfo: LiveData<List<data_class_exmple.data_class_exmpleItem>> = _mtUserInfo

    private val _mtUserInfoError = MutableLiveData<Throwable>()
    val mtUserInfoError: LiveData<Throwable> = _mtUserInfoError

    fun getUserInfoFromMT() {
        viewModelScope.launch {
            val mtUser = api1Repository.getUsers()
            processDataEvent(mtUser, onError = {
                _mtUserInfoError.postValue(it)
            }) {
                _mtUserInfo.postValue(it)
            }
        }
    }

}