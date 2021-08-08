package com.rl.jmessagedemo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.im.android.api.JMessageClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : BaseViewModel(application) {

    private val _msgNumLiveData = MutableLiveData<Int>()
    val msgNumLiveData: LiveData<Int> = _msgNumLiveData

    init {
        fetchData()
    }

    @Synchronized
    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            _msgNumLiveData.postValue(JMessageClient.getAllUnReadMsgCount())
        }
    }
}