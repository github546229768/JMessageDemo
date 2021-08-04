package com.rl.jmessagedemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.jpush.im.android.api.JMessageClient

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: MutableLiveData<String> = _text

    init {
        text.value = JMessageClient.getMyInfo()?.userName
    }
}